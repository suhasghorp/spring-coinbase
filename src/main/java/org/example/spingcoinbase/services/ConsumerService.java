package org.example.spingcoinbase.services;

import org.example.spingcoinbase.TelemetryLogger;
import org.example.spingcoinbase.handlers.CacheHandler;
import org.example.spingcoinbase.model.Coin;
import org.example.spingcoinbase.tasks.TwilioTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Instant;

@Service
public class ConsumerService {

    @Autowired
    TwilioTask twilioTask;
    @Autowired
    CoinManagerService coinManagerService;
    @Autowired
    CacheHandler cacheHandler;

    Lock lock = new ReentrantLock();

    @Async
    public void processMessage(Coin coin) {

        cacheHandler.put(coin.getSymbol(), String.valueOf(coin.getPrice()));
        if (coin.getPrice() <= coin.getThreshold()){
            try {
                lock.lock();
                long now = Instant.now().getEpochSecond();
                if ((now - coinManagerService.getCallTime(coin.getSymbol())) > 900) {
                    TelemetryLogger.info("Calling twilio for coin: " + coin.getSymbol());
                    twilioTask.addCoin(coin);
                    coinManagerService.setCallTime(coin.getSymbol(), now);

                }
            } catch (Exception e) {
                TelemetryLogger.error(e.getMessage());
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }
    }
}