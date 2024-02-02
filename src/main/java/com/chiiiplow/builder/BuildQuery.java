package com.chiiiplow.builder;

import com.chiiiplow.bean.Constants;
import com.chiiiplow.bean.FieldInfo;
import com.chiiiplow.bean.TableInfo;
import com.chiiiplow.utils.FieldUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * 构建查询
 *
 * @author CHIIIPLOW
 * @date 2024/01/30
 */
public class BuildQuery {

    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

    /**
     * 执行
     *
     * @param tableInfo 表格信息
     */
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_QUERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }
//        System.out.println(JsonUtils.convertObj2Json(folder));
        File file = new File(folder, tableInfo.getBeanName() + Constants.SUFFIX_BEAN_PARAM + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            os = new FileOutputStream(file);
            osw = new OutputStreamWriter(os, "utf-8");
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();

            //导包操作
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
            }
            bw.newLine();

            //类
            BuildComment.createClassComment(bw, tableInfo.getComment() + "查询");
            bw.write("public class " + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_PARAM + " extends BaseQuery {");
            bw.newLine();
            //属性以及属性注解
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw, fieldInfo.getComment());
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    BuildComment.createFieldComment(bw, fieldInfo.getComment() + "模糊查询");
                    bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_FUZZY + ";");
                    bw.newLine();
                    bw.newLine();
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    BuildComment.createFieldComment(bw, fieldInfo.getComment() + "开始日期区间");
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_TIME_START + ";");
                    bw.newLine();
                    bw.newLine();
                    BuildComment.createFieldComment(bw, fieldInfo.getComment() + "结束时间区间");
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_TIME_END + ";");
                    bw.newLine();
                    bw.newLine();
                }
            }

            //Getter和Setter方法
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String fieldName = FieldUtils.upperCaseFirstLetter(fieldInfo.getPropertyName());
                bw.newLine();
                bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + fieldName + "() {");
                bw.newLine();
                bw.write("\t\treturn " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                bw.write("\tpublic void " + "set" + fieldName + "(" + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ") {");
                bw.newLine();
                bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    String fuzzyMethodName = fieldName + Constants.SUFFIX_BEAN_FUZZY;
                    String fuzzyFieldName = fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_FUZZY;
                    bw.newLine();
                    bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + fuzzyMethodName + "() {");
                    bw.newLine();
                    bw.write("\t\treturn " + fuzzyFieldName + ";");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tpublic void " + "set" + fuzzyMethodName + "(" + fieldInfo.getJavaType() + " " + fuzzyFieldName + ") {");
                    bw.newLine();
                    bw.write("\t\tthis." + fuzzyFieldName + " = " + fuzzyFieldName + ";");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    String startMethodName = fieldName + Constants.SUFFIX_BEAN_TIME_START;
                    String endMethodName = fieldName + Constants.SUFFIX_BEAN_TIME_END;
                    String startFieldName = fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_TIME_START;
                    String endFieldName = fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_TIME_END;
                    bw.newLine();
                    bw.write("\tpublic String get" + startMethodName + "() {");
                    bw.newLine();
                    bw.write("\t\treturn " + startFieldName + ";");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tpublic void " + "set" + startMethodName + "(String " + startFieldName + ") {");
                    bw.newLine();
                    bw.write("\t\tthis." + startFieldName + " = " + startFieldName + ";");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tpublic String get" + endMethodName + "() {");
                    bw.newLine();
                    bw.write("\t\treturn " + endFieldName + ";");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tpublic void " + "set" + endMethodName + "(String " + endFieldName + ") {");
                    bw.newLine();
                    bw.write("\t\tthis." + endFieldName + " = " + endFieldName + ";");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                }
            }
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Query文件失败", e);
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
