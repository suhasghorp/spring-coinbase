package org.example.springcoinbase.handlers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Component
public class CacheHandler {
    private final Cache<String, String> cache;
    private final Cache<String, String> logCache;

    public CacheHandler() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
        logCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }
    public String getAll() {
        return cache.asMap().toString();
    }

    public void putLog(String key, String value) {
        logCache.put(key, value);
    }
    public ConcurrentMap<String,String> getLogs() {
        return logCache.asMap();
    }

}
