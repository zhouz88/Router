package com.zz.kerwingo.sample;

import java.util.HashMap;
import java.util.Map;

public class RouterMapping2 {
    public static Map<String, String> get() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("route://Lib2glidetest", "com.zz.lib2.C");
        return mapping;
    }
}
