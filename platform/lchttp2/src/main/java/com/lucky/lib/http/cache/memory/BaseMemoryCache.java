package com.lucky.lib.http.cache.memory;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 作用描述: 内存缓存的基类
 * @author : xmq
 * @date : 2018/10/19 下午4:32
 */
public abstract class BaseMemoryCache<K, V> {
    private final Map<K, Reference<V>> softMap = Collections.synchronizedMap(new HashMap<K, Reference<V>>());

    public BaseMemoryCache() {
    }

    /**
     * 查
     */
    public V get(K key) {
        V result = null;
        Reference<V> reference = softMap.get(key);
        if (reference != null) {
            result = reference.get();
        }

        return result;
    }

    /**
     * 增/改
     */
    public boolean put(K key, V value) {
        this.softMap.put(key, this.createReference(value));
        return true;
    }

    /**
     * 删
     */
    public V remove(K key) {
        Reference<V> bmpRef = softMap.remove(key);
        return bmpRef == null ? null : bmpRef.get();
    }

    /**
     * 获取所有的key
     */
    public Collection<K> keys() {
        Map var1 = softMap;
        synchronized(softMap) {
            return Collections.unmodifiableSet(softMap.keySet());
        }
    }

    public void clear() {
        this.softMap.clear();
    }

    /**
     * 引用方式创建：{@link java.lang.ref.WeakReference}等
     * @param var1 value
     * @return 引用
     */
    protected abstract Reference<V> createReference(V var1);
}

