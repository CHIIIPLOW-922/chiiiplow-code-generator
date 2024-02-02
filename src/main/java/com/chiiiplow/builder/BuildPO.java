package com.chiiiplow.builder;

import com.chiiiplow.bean.Constants;
import com.chiiiplow.bean.FieldInfo;
import com.chiiiplow.bean.TableInfo;
import com.chiiiplow.utils.DateUtil;
import com.chiiiplow.utils.FieldUtils;
import com.chiiiplow.utils.JsonUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author CHIIIPLOW
 * @date 2024/01/25
 */
public class BuildPO {

    private static final Logger logger = LoggerFactory.getLogger(BuildPO.class);

    /**
     * 执行
     *
     * @param tableInfo 表格信息
     */
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_PO);
        if (!folder.exists()) {
            folder.mkdirs();
        }
//        System.out.println(JsonUtils.convertObj2Json(folder));
        File file = new File(folder, tableInfo.getBeanName() + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            os = new FileOutputStream(file);
            osw = new OutputStreamWriter(os, "utf-8");
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();

            //导包操作
            bw.write("import java.io.Serializable;");
            bw.newLine();
            Boolean haveJsonIgnore = false;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (ArrayUtils.contains(Constants.BEAN_JSON_IGNORE_FIELDS.split(","), fieldInfo.getFieldName())) {
                    haveJsonIgnore = true;
                    break;
                }
            }
            if (haveJsonIgnore) {
                bw.write(Constants.BEAN_JSON_IGNORE_CLASS);
                bw.newLine();
            }
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write(Constants.BEAN_DATE_DESERIALIZATION_CLASS);
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_UTILS + "." + "DateUtil;");
                bw.newLine();
            }
            bw.newLine();

            //类
            BuildComment.createClassComment(bw, tableInfo.getComment());
            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();
            //属性以及属性注解
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw, fieldInfo.getComment());
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, "yyyy-MM-dd HH:mm:ss"));
                    bw.newLine();
                    bw.write("\t" + String.format(Constants.BEAN_DATE_DESERIALIZATION_EXPRESSION, "yyyy-MM-dd HH:mm:ss"));
                    bw.newLine();
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, "yyyy-MM-dd"));
                    bw.newLine();
                    bw.write("\t" + String.format(Constants.BEAN_DATE_DESERIALIZATION_EXPRESSION, "yyyy-MM-dd"));
                    bw.newLine();
                }
                if (ArrayUtils.contains(Constants.BEAN_JSON_IGNORE_FIELDS.split(","), fieldInfo.getFieldName())) {
                    bw.write("\t" + Constants.BEAN_JSON_IGNORE_EXPRESSION);
                    bw.newLine();
                }
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
            }

            //Getter和Setter方法
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.newLine();
                bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + FieldUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()) + "() {");
                bw.newLine();
                bw.write("\t\treturn " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                bw.write("\tpublic void " + "set" + FieldUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()) + "(" + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ") {");
                bw.newLine();
                bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }

            //toString方法
            StringBuffer toString = new StringBuffer();
            Integer index = 0;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                index++;
                String propertyName = fieldInfo.getPropertyName();
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    toString.append(fieldInfo.getComment() + " : \" + (" + fieldInfo.getPropertyName() + " == null ? \"空\" : " + "DateUtil.format(" + propertyName + ", " + "\"yyyy-MM-dd\"" + ")) ");
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    toString.append(fieldInfo.getComment() + " : \" + (" + fieldInfo.getPropertyName() + " == null ? \"空\" : " + "DateUtil.format(" + propertyName + ", " + "\"yyyy-MM-dd HH:mm:ss\"" + ")) ");
                } else {
                    toString.append(fieldInfo.getComment() + " : \" + (" + fieldInfo.getPropertyName() + " == null ? \"空\" : " + propertyName + ") ");
                }
                if (index < tableInfo.getFieldList().size()) {
                    toString.append("+ ").append("\" ,");
                }
            }
            String toStringStr = toString.toString();
            toStringStr = "\"" + toStringStr;
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn " + toStringStr + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建PO文件失败", e);
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
