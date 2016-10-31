package com.dotions.commons.test.cache;

import com.dotions.commons.cache.CacheBlock;

public class CacheTest {

    public static void main(String[] args) {
        CacheBlock<String, String> cache1 = new CacheBlock<String, String>();
        CacheBlock<String, Integer> cache2 = new CacheBlock<String, Integer>();
        
        String k = "mykey";
        
        cache1.put(k, "v-aaaaaa", 5500);
        cache2.put(k, 333333, 5500);
        System.out.println(cache1.get(k));
        System.out.println(cache2.get(k));
        
        boolean str;
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            str = cache1.hasExpire(k);
            System.out.println("Get::::hasExpire::::" + str);
        } while(!str);
    }

}
