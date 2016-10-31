package com.dotions.commons.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description Cache block. 
 * @author wangyunfei 2016-10-31
 * @version 1.0
 */
public class CacheBlock<K, V> {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheBlock.class);
    
    private static final int DEFAULT_CAPACITY = 16;
    private static final int DEFAULT_CLEANER_SLEEP_TIME = 5000;
    
    private final ConcurrentHashMap<K, Node<V>> cache;
    private static final Cleaner cleaner = new Cleaner("CacheBlock-cleaner");
    
    static {
        // Start the cleaner thread, regular cleaning expire key. 
        cleaner.start();
    }
    
    public CacheBlock() {
        this(DEFAULT_CAPACITY);
    }
    
    public CacheBlock(int initialCapacity) {
        super();
        this.cache = new ConcurrentHashMap<K, Node<V>>(initialCapacity);
        cleaner.monitor(this);
    }
    
    /**
     * Default expire time is never.
     * */
    public void put(K key, V value) {
        cache.put(key, new Node<V>(value));
    }
    
    public final void put(K key, V value, long expireTime) {
        cache.put(key, new Node<V>(value, expireTime));
    }
    
    /**
     * When key not exist, return false
     * */
    public final boolean expire(K key, long expireTime) {
        Node<V> n = getNode(key);
        if(null == n) {
            return false;
        } else {
            n.setExpireTime(expireTime);
            cache.put(key, n);
            return true;
        }
    }
    
    /**
     * Check the key whether expire.
     * */
    public final boolean hasExpire(K key) {
        Node<V> n = getNode(key);
        return null == n ? true : n.hasExpire();
    }
    
    /**
     * Clear this cache.
     * */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Remove value by key.
     * */
    public V remove(K key) {
        Node<V> n = cache.remove(key);
        return null == n ? null : n.getValue();
    }
    
    private Node<V> getNode(K key) {
        Node<V> n = cache.get(key);
        if(null == n) {
            return null;
        } else {
            if(n.hasExpire()) {
                cache.remove(key);
                return null;
            } else {
                return n;
            }
        }
    }
    
    /**
     * Get value by key.
     * */
    public V get(K key) {
        Node<V> n = getNode(key);
        return (null == n) ? null : n.getValue();
    }
    
    /**
     * List keys.
     * */
    public Set<K> keySet() {
        return cache.keySet();
    }
    
    /**
     * The Store node.
     * <br/>
     * value - expireTime
     * */
    static class Node<V> {
        private static final long NEVER_EXPIRE = -1;

        private V value;
        private long expireTime;
        
        public Node(V value, long expireTime) {
            super();
            this.value = value;
            this.expireTime = System.currentTimeMillis() + expireTime;
        }
        
        public Node(V value) {
            super();
            this.value = value;
            this.expireTime = NEVER_EXPIRE;
        }
        
        boolean hasExpire() {
            if(expireTime == NEVER_EXPIRE) {
                return false;
            } else if(System.currentTimeMillis() < expireTime){
                return false;
            } else {
                return true;
            }
        }
        
        long setExpireTime(long expireTime) {
            this.expireTime = System.currentTimeMillis() + expireTime;
            return this.expireTime;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }
    
    /**
     * 
     * */
    @SuppressWarnings("rawtypes")
    static class Cleaner extends Thread {
        
        private String name;
        private List<CacheBlock> blocks = new ArrayList<CacheBlock>();

        public Cleaner(String name) {
            super(name);
            super.setDaemon(true);
        }
        
        public void monitor(CacheBlock block) {
            blocks.add(block);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            logger.info("Cache block cleaner start running, cleaner thread name is [{}]", name);
            
            Set keys;
            CacheBlock block;
            boolean hasExpire;
            Iterator<CacheBlock> iterator;
            
            while(true) {
                try {
                    if(logger.isDebugEnabled()) {
                        logger.info("Block size is [{}], start clean.", blocks.size());
                    }
    
                    iterator = blocks.iterator();
                    
                    while(iterator.hasNext()) {
                        block = iterator.next();
                        
                        keys = block.keySet();
                        if(null == keys || keys.isEmpty()) {
                            continue;
                        } else {
                            if(logger.isDebugEnabled()) {
                                logger.debug("Cache key size is [{}]", keys.size());
                            }
                            
                            for (Object key : keys) {
                                hasExpire = block.hasExpire(key);
                                
                                if(logger.isDebugEnabled()) {
                                    logger.debug("Check cache key, key={}, hasExpire={}", key, hasExpire);
                                }
                            }
                        }
                    }
                
                    Thread.sleep(DEFAULT_CLEANER_SLEEP_TIME);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }
}
