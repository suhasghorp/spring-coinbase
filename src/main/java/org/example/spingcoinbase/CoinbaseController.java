package org.example.spingcoinbase;

import org.example.spingcoinbase.handlers.CacheHandler;
import org.example.spingcoinbase.services.CoinManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class CoinbaseController {
    @Autowired private CoinManagerService coinManagerService;
    @Autowired private CacheHandler cacheHandler;

    @RequestMapping(value = "/shutdown2", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> rule() {
        TelemetryLogger.info("Entry Thread Id (Debug): " + Thread.currentThread().getName());
        Runnable runnable= () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

                TelemetryLogger.info("Thread was Interrupted! Error in Thread Sleep (2 Seconds!)");
            }
            TelemetryLogger.info("Callable Thread Id: " + Thread.currentThread().getName());
            SpingCoinbaseApplication.ctx.close();
        };

        new Thread(runnable).start();
        TelemetryLogger.info("Exit Thread Id (Debug): " + Thread.currentThread().getName());
        return new ResponseEntity<>("Shutdown Requested - Will Shutdown in Next 2 Seconds!", HttpStatus.OK);
    }

    @RequestMapping(
            value = "/coin/update",
            params = { "coin", "price" },
            method = GET)
    @ResponseBody
    public String updateCoinPriceLevel(@RequestParam("coin") String coin, @RequestParam("price") double price) {
        coinManagerService.updateCoinPriceLevel(coin, price);
        return "Updated Price for " + coin + " to " + price;
    }

    @RequestMapping(
            value = "/coin/prices",
            method = GET)
    @ResponseBody
    public String getAllPrices() {
        String prices = cacheHandler.getAll();
        return prices;
    }
}
