package com.chiiiplow.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字段工具类
 *
 * @author CHIIIPLOW
 * @date 2024/01/25
 */
public class FieldUtils {
    /**
     * 首字母大写
     *
     * @param field 田
     * @return {@link String}
     */
    public static String upperCaseFirstLetter(String field){
        if (StringUtils.isEmpty(field)){
            return field;
        }
        return field.substring(0,1).toUpperCase() + field.substring(1);
    }


    /**
     * 首字母小写
     *
     * @param field 田
     * @return {@link String}
     */
    public static String lowerCaseFirstLetter(String field){
        if (StringUtils.isEmpty(field)){
            return field;
        }
        return field.substring(0,1).toLowerCase() + field.substring(1);
    }


}
