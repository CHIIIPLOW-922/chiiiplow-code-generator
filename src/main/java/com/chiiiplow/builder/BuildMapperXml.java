package com.chiiiplow.builder;

import com.chiiiplow.bean.Constants;
import com.chiiiplow.bean.FieldInfo;
import com.chiiiplow.bean.TableInfo;
import com.chiiiplow.utils.FieldUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 构建映射器 XML
 *
 * @author CHIIIPLOW
 * @date 2024/01/31
 */
public class BuildMapperXml {

    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);

    private static final String BASE_COLUMN_LIST = "base_column_list";

    private static final String BASE_QUERY_CONDITION = "base_query_condition";

    private static final String QUERY_CONDITION = "query_condition";

    private static final String EXTEND_QUERY_CONDITION = "extend_query_condition";

    /**
     * 执行
     *
     * @param tableInfo 表格信息
     */
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS_XML);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER + ".xml");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            os = new FileOutputStream(file);
            osw = new OutputStreamWriter(os, "utf-8");
            bw = new BufferedWriter(osw);

            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            String poName = Constants.PACKAGE_MAPPERS + "." + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER;
            bw.write("<mapper namespace=\"" + poName + "\">");
            bw.newLine();

            //实体映射
            BuildComment.createXmlComment(bw, "实体映射");
            //实体名
            String entityName = Constants.PACKAGE_PO + "." + tableInfo.getBeanName();
            //映射名
            String resultMapName = FieldUtils.lowerCaseFirstLetter(tableInfo.getBeanName()) + "ResultMap";
            bw.write("\t<resultMap id=\"" + resultMapName + "\" type=\"" + entityName + "\">");
            bw.newLine();

            FieldInfo idField = null;
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> stringListEntry : keyIndexMap.entrySet()) {
                if (StringUtils.equals("PRIMARY", stringListEntry.getKey())) {
                    List<FieldInfo> indexFieldList = stringListEntry.getValue();
                    if (indexFieldList.size() == 1) {
                        idField = indexFieldList.get(0);
                        break;
                    }
                }
            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                BuildComment.createXmlFieldComment(bw, fieldInfo.getComment());
                if (idField != null && StringUtils.equals(fieldInfo.getPropertyName(), idField.getPropertyName())) {
                    bw.write("\t\t<id column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\"/>");
                } else {
                    bw.write("\t\t<result column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\"/>");
                }
                bw.newLine();
            }
            bw.write("\t</resultMap>");
            bw.newLine();
            bw.newLine();


            //通用查询结果列表
            BuildComment.createXmlComment(bw, "通用查询结果列表");
            bw.write("\t<sql id=\"" + BASE_COLUMN_LIST + "\">");
            bw.newLine();

            StringBuilder fieldName = new StringBuilder();
            List<FieldInfo> fieldList = tableInfo.getFieldList();
            for (FieldInfo fieldInfo : fieldList) {
                fieldName.append(fieldInfo.getFieldName()).append(", ");
            }
            String substringFieldName = fieldName.substring(0, fieldName.lastIndexOf(","));
            bw.write("\t " + substringFieldName);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

            //基础查询条件构建
            BuildComment.createXmlComment(bw, "基础查询条件");
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String stringType = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    stringType = " and query." + fieldInfo.getPropertyName() + " != ''";
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null" + stringType + "\">");
                bw.newLine();
                bw.write("\t\t\tand " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();
            bw.newLine();

            //扩展查询条件构建
            BuildComment.createXmlComment(bw, "扩展查询条件");
            bw.write("\t<sql id=\"" + EXTEND_QUERY_CONDITION + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getExtendFieldList()) {
                String likeQuery = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    likeQuery = "and " + fieldInfo.getFieldName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + "}, '%')";
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    if (StringUtils.endsWith(fieldInfo.getPropertyName(), Constants.SUFFIX_BEAN_TIME_START)) {
                        likeQuery = "<![CDATA[ and " + fieldInfo.getFieldName() + ">=str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d') ]]>";
                    } else {
                        likeQuery = "<![CDATA[ and " + fieldInfo.getFieldName() + "< date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d'), interval -1 day) ]]>";
                    }
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName() + " != ''\">");
                bw.newLine();
                bw.write("\t\t\t" + likeQuery);
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();
            //query condition
            BuildComment.createXmlComment(bw, "query_condition");
            bw.write("\t<sql id=\"" + QUERY_CONDITION + "\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + EXTEND_QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

            //查询列表selectList接口
            BuildComment.createXmlComment(bw, "查询列表");
            bw.write("\t<select id=\"selectList\" resultMap=\"" + resultMapName + "\">");
            bw.newLine();
            bw.write("\t\tSELECT ");
            bw.newLine();
            bw.write("\t\t<include refid=\"" + BASE_COLUMN_LIST + "\"/>");
            bw.newLine();
            bw.write("\t\tFROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy != null\">");
            bw.newLine();
            bw.write("\t\t\torder by ${query.orderBy}");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.simplePage != null\">");
            bw.newLine();
            bw.write("\t\t\tlimit #{query.simplePage.start}, #{query.simplePage.end}");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            bw.newLine();

            //查询数量selectCount接口
            BuildComment.createXmlComment(bw, "查询数量");
            bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Integer\">");
            bw.newLine();
            bw.write("\t\tSELECT ");
            bw.newLine();
            bw.write("\t\tcount(1)");
            bw.newLine();
            bw.write("\t\tFROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            bw.newLine();


            //单条插入
            BuildComment.createXmlComment(bw, "插入(匹配有值的字段)");
            bw.write("\t<insert id=\"insert\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
            bw.newLine();
            FieldInfo autoIncrementField = null;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getAutoIncrement() != null && fieldInfo.getAutoIncrement()) {
                    autoIncrementField = fieldInfo;
                    break;
                }
            }
            if (autoIncrementField != null) {
                bw.write("\t\t<selectKey keyProperty=\"bean." + autoIncrementField.getFieldName() + "\" resultType=\"" + autoIncrementField.getJavaType() + "\" order=\"AFTER\">");
                bw.newLine();
                bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t</selectKey>");
                bw.newLine();
            }
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            //插入或更新单条
            BuildComment.createXmlComment(bw, "插入或更新(匹配有值的字段)");
            bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            bw.newLine();
            Map<String, String> tempKeyMap = new HashMap<>();
            for (Map.Entry<String, List<FieldInfo>> keyListEntry : keyIndexMap.entrySet()) {
                for (FieldInfo keyItem : keyListEntry.getValue()) {
                    tempKeyMap.put(keyItem.getFieldName(), keyItem.getFieldName());
                }
            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (tempKeyMap.get(fieldInfo.getFieldName()) != null) {
                    continue;
                }
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            //批量插入
            BuildComment.createXmlComment(bw, "添加(批量插入)");
            bw.write("\t<insert id=\"insertBatch\" parameterType=\"" + entityName + "\">");
            bw.newLine();
            StringBuffer insertFieldBuffer = new StringBuffer();
            StringBuffer insertPropertyBuffer = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getAutoIncrement()) {
                    continue;
                }
                insertFieldBuffer.append(fieldInfo.getFieldName()).append(", ");
                insertPropertyBuffer.append("#{item." + fieldInfo.getPropertyName() + "}").append(", ");
            }
            String insertFieldStr = insertFieldBuffer.substring(0, insertFieldBuffer.lastIndexOf(","));
            String insertPropertyStr = insertPropertyBuffer.substring(0, insertPropertyBuffer.lastIndexOf(","));
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertFieldStr + ") VALUES");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\">");
            bw.newLine();
            bw.write("\t\t\t(" + insertPropertyStr + ")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            //批量插入或更新
            BuildComment.createXmlComment(bw, "批量插入或更新");
            bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\"" + entityName + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertFieldStr + ") VALUES");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\">");
            bw.newLine();
            bw.write("\t\t\t(" + insertPropertyStr + ")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (tempKeyMap.get(fieldInfo.getFieldName()) != null) {
                    continue;
                }
                bw.write("\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),");
                bw.newLine();
            }
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            //创建自定义Mapper方法
            for (Map.Entry<String, List<FieldInfo>> stringListEntry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyIndexList = stringListEntry.getValue();

                Integer index = 0;
                StringBuilder methodName = new StringBuilder();

                StringBuilder paramName = new StringBuilder();
                for (FieldInfo fieldInfo : keyIndexList) {
                    index++;
                    methodName.append(FieldUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    paramName.append(fieldInfo.getFieldName() + " = #{" + fieldInfo.getPropertyName() + "}");
                    if (index < keyIndexList.size()) {
                        methodName.append("And");
                        paramName.append(" AND ");
                    }
                }
                //自定义查询
                BuildComment.createXmlComment(bw, "根据" + methodName + "查询");
                bw.write("\t<select id=\"selectBy" + methodName + "\" resultMap=\"" + resultMapName + "\">");
                bw.newLine();
                bw.write("\t\tSELECT <include refid=\"" + BASE_COLUMN_LIST + "\"/>");
                bw.newLine();
                bw.write("\t\tFROM " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\tWHERE " + paramName);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();
                bw.newLine();

                //自定义更新
                BuildComment.createXmlComment(bw, "根据" + methodName + "更新");
                bw.write("\t<update id=\"updateBy" + methodName + "\" parameterType=\"" + entityName + "\">");
                bw.newLine();
                bw.write("\t\tUPDATE " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                    if (tempKeyMap.get(fieldInfo.getFieldName()) != null) {
                        continue;
                    }
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                    bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getFieldName() + "},");
                    bw.newLine();
                    bw.write("\t\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t\t</set>");
                bw.newLine();
                bw.write("\t\tWHERE " + paramName);
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();
                bw.newLine();

                //自定义删除
                BuildComment.createXmlComment(bw, "根据" + methodName + "删除");
                bw.write("\t<delete id=\"deleteBy" + methodName + "\">");
                bw.newLine();
                bw.write("\t\tDELETE FROM " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\tWHERE " + paramName);
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();
                bw.newLine();

            }

            bw.write("</mapper>");

            bw.flush();
        } catch (Exception e) {
            logger.error("创建Mapper XML文件失败", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }


}
