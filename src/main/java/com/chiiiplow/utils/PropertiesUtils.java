package com.chiiiplow.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CHIIIPLOW
 */
public class PropertiesUtils {

//     private static final Logger

    private static final Properties props = new Properties();

    private static final Map<String,String> PROPERTIES_MAP =new ConcurrentHashMap<>();


    static {
        try (InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties")){
            props.load(is);
            Iterator<Object> iterator = props.keySet().iterator();
            while (iterator.hasNext()){
                String key =(String) iterator.next();
                PROPERTIES_MAP.put(key,props.getProperty(key));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getValueFromMap(String key){
        return PROPERTIES_MAP.get(key);
    }


//    public static void main(String[] args) {
//        System.out.println(getValueFromMap("db.username"));
//    }
}
