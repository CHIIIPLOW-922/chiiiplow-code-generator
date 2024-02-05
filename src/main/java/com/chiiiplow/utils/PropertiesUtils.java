package com.chiiiplow.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CHIIIPLOW
 */
public class PropertiesUtils {

    private static final String APPLICATION_YAML = "application.yml";

    private static final Yaml yaml = new Yaml();

    private static final Map<String, String> PROPERTIES_MAP = new ConcurrentHashMap<>();


    static {
        try (InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(APPLICATION_YAML)) {
            Map<String, Object> map = yaml.load(is);
            analyzeMap(map, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void analyzeMap(Map<String, Object> map, String parentKey) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String currentKey = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                analyzeMap((Map<String, Object>) entry.getValue(), currentKey);
            } else {
                PROPERTIES_MAP.put(currentKey, entry.getValue().toString());
            }
        }
    }


    public static String getValueFromMap(String key){
        return PROPERTIES_MAP.get(key);
    }


}
