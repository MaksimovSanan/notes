package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapTest {
    public static void weakHashMapTest() {
        Object object = new Object();
        Map<Object, String> weakHashMap = new WeakHashMap<>();
//        Map<Object, String> weakHashMap = new HashMap<>();
        weakHashMap.put(object, "test");

        System.out.println(weakHashMap);
        System.out.println("weakHashMap.isEmpty()=" + weakHashMap.isEmpty());

        object = null;
        System.gc();

        System.out.println(weakHashMap);
        System.out.println("weakHashMap.isEmpty()=" + weakHashMap.isEmpty());
    }
}
