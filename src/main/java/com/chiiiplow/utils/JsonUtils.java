package com.chiiiplow.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * json 工具类
 *
 * @author CHIIIPLOW
 * @date 2024/01/25
 */
public class JsonUtils {

    public static String convertObj2Json(Object obj){
        if (null == obj){
            return null;
        }
        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }
}
