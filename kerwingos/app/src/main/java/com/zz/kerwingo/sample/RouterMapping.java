package com.zz.kerwingo.sample;

import java.util.HashMap;
import java.util.Map;

public class RouterMapping {

    public static Map<String, String> get() {
       Map<String, String> map = new HashMap<>();
       map.putAll(RouterMapping1.get());
       map.putAll(RouterMapping2.get());
       return map;
    }
}
