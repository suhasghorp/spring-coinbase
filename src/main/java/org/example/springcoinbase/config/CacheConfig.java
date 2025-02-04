package org.example.springcoinbase.config;

import org.example.springcoinbase.handlers.CacheHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheHandler cacheHandler() {
        return new CacheHandler();
    }
}
