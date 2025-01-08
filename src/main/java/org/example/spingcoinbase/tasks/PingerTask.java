package org.example.spingcoinbase.tasks;

import org.example.spingcoinbase.handlers.CoinbaseWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PingerTask {

    @Autowired
    private final CoinbaseWebSocketHandler webSocketHandler;

    public PingerTask(CoinbaseWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }


    @Scheduled(fixedRate = 5000) // Send ping every 5 seconds
    public void sendPings() {
        try {
            webSocketHandler.sendPing();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
