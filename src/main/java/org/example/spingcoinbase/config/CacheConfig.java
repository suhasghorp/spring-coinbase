package org.example.spingcoinbase.config;

import org.example.spingcoinbase.handlers.CacheHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class CacheConfig {

    @Bean
    public CacheHandler cacheHandler() {
        return new CacheHandler();
    }
}
