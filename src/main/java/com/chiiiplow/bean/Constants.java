package com.chiiiplow.bean;

import com.chiiiplow.utils.PropertiesUtils;

/**
 * 常数
 *
 * @author CHIIIPLOW
 * @date 2024/01/25
 */
public class Constants {


    //Json忽略字段
    public static String BEAN_JSON_IGNORE_FIELDS;

    public static String BEAN_JSON_IGNORE_EXPRESSION;

    public static String BEAN_JSON_IGNORE_CLASS;

    //序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;

    public static String BEAN_DATE_FORMAT_CLASS;


    //反序列化
    public static String BEAN_DATE_DESERIALIZATION_EXPRESSION;

    public static String BEAN_DATE_DESERIALIZATION_CLASS;


    public static String PROJECT_AUTHOR;

    public static Boolean IGNORE_TABLE_PREFIX;

    public static String SUFFIX_BEAN_PARAM;

    public static String SUFFIX_BEAN_FUZZY;

    public static String SUFFIX_BEAN_TIME_START;

    public static String SUFFIX_BEAN_TIME_END;

    public static String SUFFIX_BEAN_MAPPER;

    private static final String PATH_JAVA = "java";


    private static final String PATH_RESOURCE = "resources";

    public static String PATH_BASE;

    public static String PATH_PO;

    public static String PACKAGE_BASE;

    public static String PACKAGE_PO;

    //mappers包
    public static String PACKAGE_MAPPERS;

    public static String PATH_MAPPERS_XML;

    public static String PATH_MAPPERS;

    //query包
    public static String PACKAGE_QUERY;

    public static String PATH_QUERY;

    //enums包
    public  static String PACKAGE_ENUMS;

    public static String PATH_ENUMS;

    //VO包
    public static String PACKAGE_VO;

    public static String PATH_VO;

    //service包
    public static String PACKAGE_SERVICE;

    public static String PATH_SERVICE;

    //serviceImpl包
    public static String PACKAGE_SERVICE_IMPL;

    public static String PATH_SERVICE_IMPL;

    //Exception包
    public static String PACKAGE_EXCEPTION;

    public static String PATH_EXCEPTION;

    //controller包
    public static String PACKAGE_CONTROLLER;

    public static String PATH_CONTROLLER;

    //Utils包
    public static String PATH_UTILS;

    public static String PACKAGE_UTILS;

    static {

        BEAN_JSON_IGNORE_FIELDS = PropertiesUtils.getValueFromMap("bean.json.ignore.fields");

        BEAN_JSON_IGNORE_EXPRESSION = PropertiesUtils.getValueFromMap("bean.json.ignore.expression");

        BEAN_JSON_IGNORE_CLASS = PropertiesUtils.getValueFromMap("bean.json.ignore.class");


        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getValueFromMap("bean.date.format.expression");

        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getValueFromMap("bean.date.format.class");

        BEAN_DATE_DESERIALIZATION_EXPRESSION = PropertiesUtils.getValueFromMap("bean.date.deserialization.expression");

        BEAN_DATE_DESERIALIZATION_CLASS = PropertiesUtils.getValueFromMap("bean.date.deserialization.class");


        PROJECT_AUTHOR = PropertiesUtils.getValueFromMap("project.author");

        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getValueFromMap("ignore.table.prefix"));
        SUFFIX_BEAN_PARAM = PropertiesUtils.getValueFromMap("suffix.bean.param");
        SUFFIX_BEAN_FUZZY = PropertiesUtils.getValueFromMap("suffix.bean.fuzzy");
        SUFFIX_BEAN_TIME_START = PropertiesUtils.getValueFromMap("suffix.bean.time.start");
        SUFFIX_BEAN_TIME_END = PropertiesUtils.getValueFromMap("suffix.bean.time.end");
        SUFFIX_BEAN_MAPPER = PropertiesUtils.getValueFromMap("suffix.bean.mapper");

        PACKAGE_BASE = PropertiesUtils.getValueFromMap("package.base");

        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.po");

        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.utils");

        PACKAGE_MAPPERS = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.mappers");

        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.query");

        PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.enums");

        PACKAGE_VO = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.vo");

        PACKAGE_EXCEPTION = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.exception");

        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.service");

        PACKAGE_SERVICE_IMPL = PACKAGE_SERVICE + "." + PropertiesUtils.getValueFromMap("package.service.impl");

        PACKAGE_CONTROLLER = PACKAGE_BASE + "." + PropertiesUtils.getValueFromMap("package.controller");


        PATH_BASE = PropertiesUtils.getValueFromMap("path.base");

        PATH_BASE = PATH_BASE + PATH_JAVA;

        PATH_PO = PATH_BASE + "/" + PACKAGE_PO.replace(".", "/");

        PATH_UTILS = PATH_BASE + "/" + PACKAGE_UTILS.replace(".", "/");

        PATH_QUERY = PATH_BASE + "/" + PACKAGE_QUERY.replace(".", "/");

        PATH_ENUMS = PATH_BASE + "/" + PACKAGE_ENUMS.replace(".", "/");

        PATH_VO = PATH_BASE + "/" + PACKAGE_VO.replace(".", "/");

        PATH_EXCEPTION = PATH_BASE + "/" + PACKAGE_EXCEPTION.replace(".", "/");

        PATH_SERVICE = PATH_BASE + "/" + PACKAGE_SERVICE.replace(".", "/");

        PATH_SERVICE_IMPL = PATH_BASE + "/" + PACKAGE_SERVICE_IMPL.replace(".", "/");

        PATH_CONTROLLER = PATH_BASE + "/" + PACKAGE_CONTROLLER.replace(".", "/");

        PATH_MAPPERS = PATH_BASE + "/" + PACKAGE_MAPPERS.replace(".", "/");

        PATH_MAPPERS_XML = PropertiesUtils.getValueFromMap("path.base") + PATH_RESOURCE + "/" + PACKAGE_MAPPERS.replace(".", "/");


    }

    public static final String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};

    public static final String[] SQL_DATE_TYPES = new String[]{"date"};

    public static final String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "double", "float"};

    public static final String[] SQL_STRING_TYPES = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};

    public static final String[] SQL_INTEGER_TYPES = new String[]{"int", "tinyint"};

    public static final String[] SQL_LONG_TYPES = new String[]{"bigint"};


//    public static void main(String[] args) {
//        System.out.println(PATH_MAPPERS_XML);
//        System.out.println(PATH_PO);
//        System.out.println(PACKAGE_PO);
//    }
}
