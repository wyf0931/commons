package com.dotions.commons.test.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dotions.commons.cache.CacheBlock;

public class CacheBlockTest {
    
    CacheBlock<String, String> cache1;
    CacheBlock<String, Integer> cache2;
    
    String key1, key2;

    @Before
    public void setUp() throws Exception {
        cache1 = new CacheBlock<String, String>();
        cache2 = new CacheBlock<String, Integer>();
        
        key1 = "test-key1";
        key2 = "test-key2";
    }

    @After
    public void tearDown() throws Exception {
        cache1.clear();
        cache2.clear();
    }

    @Test
    public final void testPut() {
        String test_value1 = "test-value";
        try {
            cache1.put(key1, test_value1);
        } catch (Exception e) {
            fail("Put value fail.");
        }
        assertTrue("Put success", true);
    }

    @Test
    public final void testHasExpire() {
        String test_value1 = "test-value1";
        String test_value2 = "test-value2";
        cache1.put(key1, test_value1);
        cache1.put(key2, test_value2, 500);
        
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            fail("testHasExpire fail.");
        }
        
        assertTrue(!cache1.hasExpire(key1));
        assertTrue(cache1.hasExpire(key2));
    }

    @Test
    public final void testClear() {
        String test_value1 = "test-value";
        cache1.put(key1, test_value1);
        assertTrue(cache1.keySet().size() == 1);
        
        cache1.clear();
        
        assertTrue(cache1.keySet().size() == 0);
    }

    @Test
    public final void testRemove() {
        String test_value1 = "test-value";
        cache1.put(key1, test_value1);
        String value = cache1.get(key1);
        assertEquals("Get value success 1", test_value1, value);
        
        cache1.remove(key1);
        value = cache1.get(key1);
        assertTrue(value == null);
    }

    @Test
    public final void testGet() {
        String test_value1 = "test-value";
        cache1.put(key1, test_value1);
        String value = cache1.get(key1);
        assertEquals("Get value success 1", test_value1, value);
        
        Integer test_value2 = 31231231;
        cache2.put(key2, test_value2);
        Integer value2 = cache2.get(key2);
        assertSame("Get value success 2", test_value2, value2);
    }

    @Test
    public final void testKeySet() {
        String test_value1 = "test-value1";
        String test_value2 = "test-value2";
        cache1.put(key1, test_value1);
        cache1.put(key2, test_value2);
        
        Set<String> keys = cache1.keySet();
        
        assertTrue(keys != null);
        assertTrue(keys.size() == 2);
        
        assertTrue(keys.contains(key1));
        assertTrue(keys.contains(key2));
    }

}
