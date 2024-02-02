package com.chiiiplow.builder;

import com.chiiiplow.bean.Constants;
import com.chiiiplow.bean.FieldInfo;
import com.chiiiplow.bean.TableInfo;
import com.chiiiplow.utils.FieldUtils;
import com.chiiiplow.utils.JsonUtils;
import com.chiiiplow.utils.PropertiesUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CHIIIPLOW
 */
public class BuildTable {

    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private static Connection conn = null;

    /**
     * SQL 显示表状态
     */
    private static final String SQL_SHOW_TABLE_STATUS = "show table status";

    /**
     * SQL 显示表字段
     */
    private static final String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";

    /**
     * SQL 显示索引
     */
    private static final String SQL_SHOW_INDEX = "show index from %s";

    static {
        String driver = PropertiesUtils.getValueFromMap("db.driver.name");
        String url = PropertiesUtils.getValueFromMap("db.url");
        String username = PropertiesUtils.getValueFromMap("db.username");
        String password = PropertiesUtils.getValueFromMap("db.password");
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            logger.error("连接数据库失败", e);
        }
    }

    /**
     * 获取表信息
     *
     * @return {@link List}<{@link TableInfo}>
     */
    public static List<TableInfo> getTableInfo() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            rs = ps.executeQuery();
            while (rs.next()) {
                String tableName = rs.getString("name");
                String comment = rs.getString("Comment");
//                logger.info("tableName:{};comment:{}",tableName,comment);

                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    beanName = tableName.substring(tableName.indexOf("_") + 1);
                }
                beanName = processField(beanName, true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARAM);
                getFieldInfo(tableInfo);

                getIndex(tableInfo);

                tableInfoList.add(tableInfo);


//                logger.info("表:{}", JsonUtils.convertObj2Json(tableInfo));
//                logger.info("字段:{}", JsonUtils.convertObj2Json(tableInfo.getFieldList()));
//                logger.info("tableName:{};beanName:{};paramName:{};fieldList:{};haveDate:{};haveDateTime:{};haveDecimal:{}", tableInfo.getTableName(), tableInfo.getBeanName(), tableInfo.getBeanParamName(), tableInfo.getFieldList(), tableInfo.getHaveDate(), tableInfo.getHaveDateTime(), tableInfo.getHaveBigDecimal());
            }
            //logger.info("tableInfo:{}", JsonUtils.convertObj2Json(tableInfoList));

        } catch (Exception e) {
            logger.error("获取数据表失败", e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return tableInfoList;
        }
    }


    /**
     * 获取字段信息
     *
     * @param tableInfo 表格信息
     * @return {@link List}<{@link FieldInfo}>
     */
    private static void getFieldInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResults = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        List<FieldInfo> extendFieldInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
            fieldResults = ps.executeQuery();
            while (fieldResults.next()) {
                String tableFieldName = fieldResults.getString("Field");
                String sqlType = fieldResults.getString("Type");
                String comment = fieldResults.getString("Comment");
//                String key = fieldResults.getString("Key");
                String extra = fieldResults.getString("extra");
                if (sqlType.indexOf("(") > 0) {
                    sqlType = sqlType.substring(0, sqlType.indexOf("("));
                }
                //实体类字段插入
                FieldInfo fieldInfo = new FieldInfo();
                String propertyFieldName = processField(tableFieldName, false);
                fieldInfo.setFieldName(tableFieldName);
                fieldInfo.setPropertyName(propertyFieldName);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(sqlType);
                fieldInfo.setJavaType(transferJavaType(sqlType));
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra) ? true : false);
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqlType)) {
                    tableInfo.setHaveDate(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, sqlType)) {
                    tableInfo.setHaveDateTime(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, sqlType)) {
                    tableInfo.setHaveBigDecimal(true);
                }
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, sqlType)) {
                    FieldInfo fuzzyStringField = new FieldInfo();
                    fuzzyStringField.setJavaType(fieldInfo.getJavaType());
                    fuzzyStringField.setPropertyName(propertyFieldName + Constants.SUFFIX_BEAN_FUZZY);
                    fuzzyStringField.setFieldName(fieldInfo.getFieldName());
                    fuzzyStringField.setSqlType(sqlType);
                    extendFieldInfoList.add(fuzzyStringField);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, sqlType) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqlType)) {
                    FieldInfo dateTypeStart = new FieldInfo();
                    dateTypeStart.setJavaType("String");
                    dateTypeStart.setPropertyName(propertyFieldName + Constants.SUFFIX_BEAN_TIME_START);
                    dateTypeStart.setFieldName(fieldInfo.getFieldName());
                    dateTypeStart.setSqlType(sqlType);
                    extendFieldInfoList.add(dateTypeStart);

                    FieldInfo dateTypeEnd = new FieldInfo();
                    dateTypeEnd.setJavaType("String");
                    dateTypeEnd.setPropertyName(propertyFieldName + Constants.SUFFIX_BEAN_TIME_END);
                    dateTypeEnd.setFieldName(fieldInfo.getFieldName());
                    dateTypeEnd.setSqlType(sqlType);
                    extendFieldInfoList.add(dateTypeEnd);
                }

                fieldInfoList.add(fieldInfo);
                //query类字段插入


//                logger.info("tableName:{};sqlType:{};comment:{};fieldName:{};autoIncrement:{};javaType:{}", tableInfo.getTableName(), fieldInfo.getSqlType(), fieldInfo.getComment(), fieldInfo.getFieldName(), fieldInfo.getAutoIncrement(), fieldInfo.getJavaType());
            }
            tableInfo.setFieldList(fieldInfoList);
            tableInfo.setExtendFieldList(extendFieldInfoList);
        } catch (Exception e) {
            logger.error("获取字段信息失败", e);
        } finally {
            if (fieldResults != null) {
                try {
                    fieldResults.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 获取索引
     *
     * @param tableInfo 表格信息
     */
    private static void getIndex(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResults = null;
        //List<FieldInfo> fieldInfoList = new ArrayList<>();

        try {
            Map<String, FieldInfo> tempMap = new HashMap<>();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                tempMap.put(fieldInfo.getFieldName(), fieldInfo);
            }
            ps = conn.prepareStatement(String.format(SQL_SHOW_INDEX, tableInfo.getTableName()));
            fieldResults = ps.executeQuery();
            while (fieldResults.next()) {
                String keyName = fieldResults.getString("Key_name");
                int nonUnique = fieldResults.getInt("Non_unique");
                String columnName = fieldResults.getString("Column_name");
                if (nonUnique == 1) {
                    continue;
                }
                List<FieldInfo> fieldIndexInfoList = tableInfo.getKeyIndexMap().get(keyName);
                if (fieldIndexInfoList == null) {
                    fieldIndexInfoList = new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName, fieldIndexInfoList);
                }
                /*for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                    if (fieldInfo.getFieldName().equals(columnName)){
                        fieldIndexInfoList.add(fieldInfo);
                    }
                }*/
                fieldIndexInfoList.add(tempMap.get(columnName));
            }

        } catch (Exception e) {
            logger.error("获取索引失败", e);
        } finally {
            if (fieldResults != null) {
                try {
                    fieldResults.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 处理字段
     *
     * @param field                田
     * @param upperCaseFirstLetter 首字母大写
     * @return {@link String}
     */
    private static String processField(String field, Boolean upperCaseFirstLetter) {
        StringBuffer sb = new StringBuffer();
        String[] fields = field.split("_");
        sb.append(upperCaseFirstLetter ? FieldUtils.upperCaseFirstLetter(fields[0]) : FieldUtils.lowerCaseFirstLetter(fields[0]));
        for (int i = 1; i < fields.length; i++) {
            sb.append(FieldUtils.upperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    /**
     * 转化 Java 类型
     *
     * @return {@link String}
     */
    private static String transferJavaType(String type) {
        if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPES, type)) {
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
            return "BigDecimal";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
            return "Date";
        } else {
            throw new RuntimeException("无法识别该类型:" + type);
        }

    }
}
