package org.example.spingcoinbase;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spingcoinbase.handlers.CacheHandler;
import org.example.spingcoinbase.model.Coin;
import org.example.spingcoinbase.services.CoinManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@Controller
@AllArgsConstructor
public class CoinbaseController {

    private CoinManagerService coinManagerService;
    private CacheHandler cacheHandler;

    @RequestMapping(value = "/shutdown2", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> rule() {
        log.info("Entry Thread Id (Debug): {}", Thread.currentThread().getName());
        Runnable runnable= () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

                log.info("Thread was Interrupted! Error in Thread Sleep (2 Seconds!)");
            }
            log.info("Callable Thread Id: {}", Thread.currentThread().getName());
            SpingCoinbaseApplication.ctx.close();
        };

        new Thread(runnable).start();
        log.info("Exit Thread Id (Debug): {}", Thread.currentThread().getName());
        return new ResponseEntity<>("Shutdown Requested - Will Shutdown in Next 2 Seconds!", HttpStatus.OK);
    }

    @RequestMapping(
            value = "/coin/update",
            params = { "coin", "low", "high" },
            method = GET)
    @ResponseBody
    public ResponseEntity<String> updateCoinPriceLevel(@RequestParam("coin") String coin, @RequestParam("low") double low, @RequestParam("high") double high) {
        coinManagerService.updateCoinLowThreshold(coin, low);
        coinManagerService.updateCoinHighThreshold(coin, high);
        return new ResponseEntity<>("Updated Price for " + coin + " to " + low + " and " + high, HttpStatus.OK);

    }

    @RequestMapping(
            value = "/coin/prices",
            method = GET)
    @ResponseBody
    public ResponseEntity<String> getAllPrices() {
        String prices = cacheHandler.getAll();
        return new ResponseEntity<>(prices, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/coin/limits",
            method = GET)
    @ResponseBody
    public ResponseEntity<String> getAllLimits() {
        StringBuilder sb = new StringBuilder();
        for (Coin coin : coinManagerService.getCoins().values())
            sb.append(coin.getSymbol()).append(":").append(coin.getLowThreshold()).append(",").append(coin.getHighThreshold()).append("\n");
        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }
}
