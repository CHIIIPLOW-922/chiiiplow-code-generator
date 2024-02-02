package com.chiiiplow.builder;

import com.chiiiplow.bean.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.BufferedWriter;
import java.util.Date;

/**
 * 构建注释
 *
 * @author CHIIIPLOW
 * @date 2024/01/25
 */
public class BuildComment {
    /**
     * 创建类注释
     *
     * @param bw           BW
     * @param classComment 类评论
     * @throws Exception 例外
     */
    public static void createClassComment(BufferedWriter bw, String classComment) throws Exception {
        bw.write("/**\n" +
                " * " + "@author " + Constants.PROJECT_AUTHOR + "\n" +
                " * " + "@description " + (StringUtils.isEmpty(classComment) ? " " : classComment) + "\n" +
                " * " + "@date " + DateFormatUtils.format(new Date(), "yyyy/MM/dd") + "\n" +
                " */");
        bw.newLine();
    }

    /**
     * 创建字段注释
     *
     * @param bw           BW
     * @param fieldComment 字段注释
     * @throws Exception 例外
     */
    public static void createFieldComment(BufferedWriter bw, String fieldComment) throws Exception {
        bw.write("\t/**\n" +
                "\t * " + (StringUtils.isEmpty(fieldComment) ? " " : fieldComment) + "\n" +
                "\t */");
        bw.newLine();
    }

    /**
     * 创建 XML 注释
     *
     * @param bw         BW
     * @param xmlComment XML 注释
     * @throws Exception 例外
     */
    public static void createXmlComment(BufferedWriter bw, String xmlComment) throws Exception {
        bw.write("\t<!--" + xmlComment + "-->");
        bw.newLine();
    }


    /**
     * 创建 XML 字段注释
     *
     * @param bw         BW
     * @param xmlComment XML 注释
     * @throws Exception 例外
     */
    public static void createXmlFieldComment(BufferedWriter bw, String xmlComment) throws Exception {
        bw.write("\t\t<!--" + xmlComment + "-->");
        bw.newLine();
    }
}
