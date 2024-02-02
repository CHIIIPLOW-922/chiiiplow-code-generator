package com.chiiiplow.builder;

import com.chiiiplow.bean.Constants;
import com.chiiiplow.bean.FieldInfo;
import com.chiiiplow.bean.TableInfo;
import com.chiiiplow.utils.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;


/**
 * 构建控制器
 *
 * @author CHIIIPLOW
 * @date 2024/02/02
 */
public class BuildController {

    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    /**
     * 执行
     *
     * @param tableInfo 表格信息
     */
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_CONTROLLER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
//        System.out.println(JsonUtils.convertObj2Json(folder));
        File file = new File(folder, tableInfo.getBeanName() + "Controller.java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            os = new FileOutputStream(file);
            osw = new OutputStreamWriter(os, "utf-8");
            bw = new BufferedWriter(osw);

            String serviceName = tableInfo.getBeanName() + "Service";

            String serviceLowerName = FieldUtils.lowerCaseFirstLetter(serviceName);

            String beanLowerName = FieldUtils.lowerCaseFirstLetter(tableInfo.getBeanName());

            String controllerLowerName = FieldUtils.lowerCaseFirstLetter(tableInfo.getBeanName()) + "Controller";

            bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_ENUMS + "." + "PageSize;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + "SimplePage;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + "." + "PaginationResultVO;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + "." + "ResponseVO;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();
            bw.newLine();
            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();


            //类
            BuildComment.createClassComment(bw, tableInfo.getComment() + "Controller");
            bw.write("@RestController(\"" + controllerLowerName + "\")");
            bw.newLine();
            bw.write("@RequestMapping(\"/" + beanLowerName + "\")");
            bw.newLine();
            bw.write("public class " + tableInfo.getBeanName() + "Controller extends ABaseController {");
            bw.newLine();
            bw.newLine();
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + serviceName + " " + serviceLowerName + ";");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw, "加载数据列表");
            bw.write("\t@RequestMapping(\"/loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO loadDataList(" + tableInfo.getBeanName() + "Query query) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(" + serviceLowerName + ".findListByPage(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.write("\t@RequestMapping(\"/add\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO add(@RequestBody " + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\tthis."+serviceLowerName + ".add(bean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\t@RequestMapping(\"/addBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tthis."+serviceLowerName + ".addBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增或更新");
            bw.write("\t@RequestMapping(\"/addOrUpdateBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addOrUpdateBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\tthis."+serviceLowerName + ".addOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> stringListEntry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfoList = stringListEntry.getValue();
                Integer index = 0;
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                StringBuilder resultParams = new StringBuilder();
                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(FieldUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    resultParams.append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodName.append("And");
                        methodParams.append(", ");
                        resultParams.append(", ");
                    }
                }
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\t@RequestMapping(\"/getBy"+methodName+"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO getBy" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(" + serviceLowerName + ".getBy" + methodName + "(" + resultParams + "));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\t@RequestMapping(\"/updateBy"+methodName+"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO updateBy" + methodName + "(@RequestBody " + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
                bw.newLine();
                bw.write("\t\tthis."+serviceLowerName+ ".updateBy" + methodName + "(bean, " + resultParams + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\t@RequestMapping(\"/deleteBy"+methodName+"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO deleteBy" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\tthis."+serviceLowerName+ ".deleteBy" + methodName + "(" + resultParams + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

            }

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Controller文件失败", e);
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
