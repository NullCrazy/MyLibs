package com.lucky.lib.http.cache.memory;

import android.util.LruCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class ObjectCache<K, V> {

    private static final int DEFAULT_SIZE = 10;

    private LruCache<K, Reference<V>> mKVLruCache = new LruCache<>(sizeof());

    protected abstract V createObject(K key);

    public V get(K key) {
        Reference<V> vReference = mKVLruCache.get(key);
        V value = null;
        synchronized (ObjectCache.this) {
            if (vReference != null) {
                value = vReference.get();
            }
            if (value == null) {
                value = createObject(key);
            }
        }
        mKVLruCache.put(key, new WeakReference<>(value));
        return value;
    }

    protected int sizeof() {
        return DEFAULT_SIZE;
    }


}
