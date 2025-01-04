package org.example.spingcoinbase.tasks;

import org.example.spingcoinbase.TelemetryLogger;
import org.example.spingcoinbase.handlers.CacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoggerTask {
    @Autowired
    private CacheHandler cacheHandler;

    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public void log() {TelemetryLogger.info(cacheHandler.getAll());}
}
