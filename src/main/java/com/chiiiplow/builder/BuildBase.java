package com.chiiiplow.builder;

import com.chiiiplow.bean.Constants;
import com.chiiiplow.utils.JsonUtils;
import org.apache.commons.lang3.ClassLoaderUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 构建基础
 *
 * @author CHIIIPLOW
 * @date 2024/01/29
 */
public class BuildBase {
    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);

//    public static void main(String[] args) {
//        execute();
//    }

    public static void execute() {
        List<String> headInfoList = new ArrayList<>();

        headInfoList.add("package " + Constants.PACKAGE_UTILS);
        build("DateUtil", Constants.PATH_UTILS, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_MAPPERS);
        build("BaseMapper", Constants.PATH_MAPPERS, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_QUERY);
        headInfoList.add("import " + Constants.PACKAGE_ENUMS + ".PageSize");
        build("SimplePage", Constants.PATH_QUERY, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_QUERY);
        build("BaseQuery", Constants.PATH_QUERY, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_ENUMS);
        build("PageSize", Constants.PATH_ENUMS, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_VO);
        build("PaginationResultVO", Constants.PATH_VO, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_VO);
        build("ResponseVO", Constants.PATH_VO, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_ENUMS);
        build("ResponseCodeEnum", Constants.PATH_ENUMS, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_EXCEPTION);
        headInfoList.add("import " + Constants.PACKAGE_ENUMS +".ResponseCodeEnum");
        build("BusinessException", Constants.PATH_EXCEPTION, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headInfoList.add("import " + Constants.PACKAGE_ENUMS +".ResponseCodeEnum");
        headInfoList.add("import " + Constants.PACKAGE_VO +".ResponseVO");
        build("ABaseController", Constants.PATH_CONTROLLER, headInfoList);

        headInfoList.clear();
        headInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headInfoList.add("import " + Constants.PACKAGE_ENUMS +".ResponseCodeEnum");
        headInfoList.add("import " + Constants.PACKAGE_VO +".ResponseVO");
        headInfoList.add("import " + Constants.PACKAGE_EXCEPTION +".BusinessException");
        build("AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER, headInfoList);

    }

    private static void build(String fileName, String outputPath, List<String> headInfoList) {
        File folder = new File(outputPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File javaFile = new File(outputPath, fileName + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            os = new FileOutputStream(javaFile);
            osw = new OutputStreamWriter(os, "utf-8");
            bw = new BufferedWriter(osw);

            is = BuildBase.class.getClassLoader().getResourceAsStream("template/" + fileName + ".txt");
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);

            for (String s : headInfoList) {
                bw.write(s + ";");
                if (StringUtils.startsWith(s, "package")) {
                    bw.newLine();
                    bw.newLine();
                } else {
                    bw.newLine();
                }

            }
            bw.newLine();

            String lineInfo = null;
            while ((lineInfo = br.readLine()) != null) {
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            logger.error("创建文件失败。", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
