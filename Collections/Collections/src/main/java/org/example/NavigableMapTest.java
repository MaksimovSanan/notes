package org.example;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NavigableMapTest {
    public static void navigableMapTest(String[] args) {
        NavigableMap<Integer, String> nMap = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });
        nMap.put(1, "one");
        nMap.put(2, "two");
        nMap.put(3, "three");
        nMap.put(10, "ten");
        nMap.put(5, "five");
        nMap.put(7, "seven");
        for(Map.Entry<Integer, String> entry: nMap.entrySet()) {
            System.out.println(entry);
        }
    }
}
