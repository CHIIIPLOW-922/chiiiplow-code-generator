package com.chiiiplow.builder;

import com.chiiiplow.bean.Constants;
import com.chiiiplow.bean.FieldInfo;
import com.chiiiplow.bean.TableInfo;
import com.chiiiplow.utils.FieldUtils;
import com.sun.org.apache.xerces.internal.parsers.CachingParserPool;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;


/**
 * 构建映射器
 *
 * @author CHIIIPLOW
 * @date 2024/01/30
 */
public class BuildMapper {

    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    /**
     * 执行
     *
     * @param tableInfo 表格信息
     */
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS);
        if (!folder.exists()) {
            folder.mkdirs();
        }
//        System.out.println(JsonUtils.convertObj2Json(folder));
        File file = new File(folder, tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            os = new FileOutputStream(file);
            osw = new OutputStreamWriter(os, "utf-8");
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_MAPPERS + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

            //类
            BuildComment.createClassComment(bw, tableInfo.getComment() + Constants.SUFFIX_BEAN_MAPPER);
            bw.write("public interface " + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER + "<T, P> extends BaseMapper {");
            bw.newLine();
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();


            for (Map.Entry<String, List<FieldInfo>> stringListEntry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfoList = stringListEntry.getValue();
                Integer index = 0;
                StringBuilder methodName = new StringBuilder();

                StringBuilder methodParams = new StringBuilder();
                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(FieldUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyFieldInfoList.size()) {
                        methodName.append("And");
                    }
                    methodParams.append("@Param(\"" + fieldInfo.getPropertyName() + "\") " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParams.append(", ");
                    }
                }
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tT selectBy" + methodName + "(" + methodParams + ");");
                bw.newLine();

                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger updateBy" + methodName + "(" + "@Param(\"bean\") T t, " + methodParams + ");");
                bw.newLine();

                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger deleteBy" + methodName + "(" + methodParams + ");");
                bw.newLine();

            }

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Mapper文件失败", e);
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
