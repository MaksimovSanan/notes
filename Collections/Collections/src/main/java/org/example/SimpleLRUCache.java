package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleLRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    /*
    initialCapacity - изначальный размер мапы
    loadFactor - коэфициент при котором происходит расширение мапы. loadFactor > 1 -> мапа не расширяется
    accesOrder - true -> сортировка от менее используемых к более
    initialCapacity + 1 для того чтобы не ловить коллизиции
     */
    public SimpleLRUCache(int capacity) {
        super(capacity + 1, 1.1F, true);
        this.capacity = capacity;
    }

    /*
    protected метод в LinkedHashMap, вызывается после каждой вставки, по дефолту возвращает false;
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > capacity;
    }

    public static void simpleLRUCacheTest() {
        SimpleLRUCache<Integer, String> simpleLRUCache = new SimpleLRUCache<>(3);
        simpleLRUCache.put(2, "two");
        simpleLRUCache.put(1, "one");
        simpleLRUCache.put(3, "three");

        System.out.println(simpleLRUCache);

        simpleLRUCache.get(1);
        System.out.println(simpleLRUCache);

        simpleLRUCache.get(2);
        System.out.println(simpleLRUCache);

        simpleLRUCache.put(5, "five");
        System.out.println(simpleLRUCache);
    }
}
