package org.example.springcoinbase;

import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springcoinbase.handlers.CoinbaseWebSocketHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
    * This is the main class for the Spring Boot Application
    * http://localhost:8080/coin/updates
    * http://localhost:8080/coin/prices
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
@Slf4j
@AllArgsConstructor
public class SpringCoinbaseApplication implements CommandLineRunner {

    public static ConfigurableApplicationContext ctx;

    CoinbaseWebSocketHandler webSocketHandler;

    @PreDestroy
    public void requestShutdown2PreDestroy() {
        log.info("Requested Shutdown (via Context) of the Spring Boot Container");
        ctx.close();
    }


    public static void main(String[] args) {
        ctx = SpringApplication.run(SpringCoinbaseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        webSocketHandler.connect();
    }
}
