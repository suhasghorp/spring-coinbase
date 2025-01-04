package org.example.spingcoinbase.config;

import org.example.spingcoinbase.handlers.CoinbaseWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;


@Configuration
@EnableWebSocket
public class WebsocketConfig  {

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }
    @Bean
    public WebSocketHandler webSocketHandler() {
        return new CoinbaseWebSocketHandler();
    }
}
