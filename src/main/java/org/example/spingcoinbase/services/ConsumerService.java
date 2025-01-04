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

        //System.out.println(Thread.currentThread().getName() + " - Consumer service - Processing coin: " + coin.getSymbol());
        cacheHandler.put(coin.getSymbol(), String.valueOf(coin.getPrice()));
        if (coin.getPrice() <= coin.getThreshold()){
            try {
                lock.lock();
                long now = Instant.now().getEpochSecond();
                if ((now - coinManagerService.getCallTime(coin.getSymbol())) > 900) {
                    //log.info("Sinch Service - Processing sequence: " + coin.getSequence());
                    TelemetryLogger.info("Calling...coin: " + coin.getSymbol());
                    twilioTask.addCoin(coin);
                    coinManagerService.setCallTime(coin.getSymbol(), now);
                    //coinManagerService.setThreshold(coin.getSymbol(), coin.getPrice() * 0.95);
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