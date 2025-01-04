package org.example.spingcoinbase;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.spingcoinbase.handlers.CoinbaseWebSocketHandler;
import org.example.spingcoinbase.services.CoinManagerService;
import org.example.spingcoinbase.services.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import java.util.Objects;


@SpringBootApplication
@EnableScheduling
@EnableCaching
public class SpingCoinbaseApplication {

    public static ConfigurableApplicationContext ctx;
    private static final String COINBASE_WS_URL = "wss://ws-feed.exchange.coinbase.com";

    @Autowired
    WebSocketClient webSocketClient ;
    @Autowired
    CoinbaseWebSocketHandler webSocketHandler;
    @Autowired
    private ConsumerService consumerService;
    @Autowired
    private CoinManagerService coinManagerService;

    @PostConstruct
    public void start() throws Exception {
        WebSocketSession session = webSocketClient.execute(webSocketHandler, COINBASE_WS_URL).get();
        TelemetryLogger.info("WebSocket connection established: " + Objects.requireNonNull(session.getRemoteAddress()));
    }

    @PreDestroy
    public void requestShutdown2PreDestroy() {
        TelemetryLogger.info("Requested Shutdown (via Context) of the Spring Boot Container");
        ctx.close();
    }


    public static void main(String[] args) {
        ctx = SpringApplication.run(SpingCoinbaseApplication.class, args);
    }

}
