package org.example.springcoinbase.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springcoinbase.handlers.CacheHandler;
import org.example.springcoinbase.model.Coin;
import org.example.springcoinbase.tasks.TwilioTask;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Instant;

@Slf4j
@Service
@AllArgsConstructor
public class ConsumerService {

    private final TwilioTask twilioTask;

    private final CoinManagerService coinManagerService;

    private final CacheHandler cacheHandler;

    private final Lock lock = new ReentrantLock();

    @Async
    public void processMessage(Coin coin) {

        cacheHandler.put(coin.getSymbol(), String.valueOf(coin.getPrice()));
        if (coin.getPrice() <= coin.getLowThreshold() || coin.getPrice() >= coin.getHighThreshold()) {
            try {
                lock.lock();
                long now = Instant.now().getEpochSecond();
                if ((now - coinManagerService.getCallTime(coin.getSymbol())) > 900) {
                    log.info("Calling twilio for coin: {}", coin.getSymbol());
                    twilioTask.addCoin(coin);
                    coinManagerService.setCallTime(coin.getSymbol(), now);

                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }

        }
    }
}