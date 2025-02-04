package org.example.springcoinbase.tasks;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springcoinbase.handlers.CacheHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class LoggerTask {

    private CacheHandler cacheHandler;

    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public void log() {log.info(cacheHandler.getAll());}
}
