package org.example.spingcoinbase.handlers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class CacheHandler {
    private Cache<String, String> cache = null;

    public CacheHandler() {
        cache = Caffeine.newBuilder()
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
}
