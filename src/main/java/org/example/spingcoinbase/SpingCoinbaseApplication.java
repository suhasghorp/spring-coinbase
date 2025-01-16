package org.example.spingcoinbase;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.spingcoinbase.handlers.CoinbaseWebSocketHandler;
import org.example.spingcoinbase.services.CoinManagerService;
import org.example.spingcoinbase.services.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
public class SpingCoinbaseApplication implements CommandLineRunner {

    public static ConfigurableApplicationContext ctx;
    @Autowired
    CoinbaseWebSocketHandler webSocketHandler;
    @PreDestroy
    public void requestShutdown2PreDestroy() {
        TelemetryLogger.info("Requested Shutdown (via Context) of the Spring Boot Container");
        ctx.close();
    }


    public static void main(String[] args) {
        ctx = SpringApplication.run(SpingCoinbaseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        webSocketHandler.connect();
    }
}
