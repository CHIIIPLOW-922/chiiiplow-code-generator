package com.chiiiplow;

import com.chiiiplow.bean.TableInfo;
import com.chiiiplow.builder.*;
import com.chiiiplow.utils.JsonUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.List;

/**
 * @author CHIIIPLOW
 * @date 2024/01/25
 */
public class Main {
    public static void main(String[] args) {
        String osName = System.getProperty("os.name");
        System.out.println("操作系统：" + osName);
        BuildBase.execute();

        List<TableInfo> tableInfoList = BuildTable.getTableInfo();
        for (TableInfo tableInfo : tableInfoList) {
            //创建PO
            BuildPO.execute(tableInfo);
            //创建入参实体
            BuildQuery.execute(tableInfo);
            //构建Mapper
            BuildMapper.execute(tableInfo);
            //创建MapperXml
            BuildMapperXml.execute(tableInfo);
            //创建Service
            BuildService.execute(tableInfo);
            //创建ServiceImpl
            BuildServiceImpl.execute(tableInfo);
            //创建Controller
            BuildController.execute(tableInfo);
        }
    }
}