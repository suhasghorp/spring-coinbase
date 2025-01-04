package org.example.spingcoinbase.services;

import lombok.Getter;
import org.example.spingcoinbase.model.Coin;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
@PropertySource(value = { "classpath:coins.properties" }, ignoreResourceNotFound = false, name = "coins")

public class CoinManagerService implements EnvironmentAware {

    private static Environment environment;

    public void updateCoinPriceLevel(String symbol, double price) {
        coins.get(symbol).setThreshold(price);
    }

    public void setThreshold(String symbol, double threshold) {
        coins.get(symbol).setThreshold(threshold);
    }

    private Map<String, Coin> coins = new ConcurrentHashMap<>();

    public Map<String, Coin> getCoins(){
        if (coins.isEmpty()){
            loadCoins();
        }
        return coins;
    }

    public void loadCoins(){

        if (environment != null) {
            org.springframework.core.env.PropertySource<?> coinsSource = ((ConfigurableEnvironment) environment).getPropertySources().get("coins");
            assert coinsSource != null;
            Properties coinsProps = (Properties) coinsSource.getSource();

            for (Map.Entry<Object, Object> e : coinsProps.entrySet()) {
                String coin = e.getKey().toString();
                double threshold = e.getValue().toString().isEmpty() ? 0.0 : Double.parseDouble(e.getValue().toString());
                coins.put(coin, new Coin(coin, null, threshold));
            }
        }
        /*coins.put("BTC-USD", new Coin("BTC-USD", null, 95200.00));
        coins.put("XRP-USD", new Coin("XRP-USD", null, 1.96));
        coins.put("HBAR-USD", new Coin("HBAR-USD", null, 0.26));
        coins.put("LINK-USD", new Coin("LINK-USD", null, 20.00));
        coins.put("AAVE-USD", new Coin("AAVE-USD", null, 307.62));
        coins.put("ETH-USD", new Coin("ETH-USD", null, 3007.39));
        coins.put("ADA-USD", new Coin("ADA-USD", null, 0.76));
        coins.put("DOGE-USD", new Coin("DOGE-USD", null, 0.28));
        coins.put("ONDO-USD", new Coin("ONDO-USD", null, 1.20));*/
    }

    public void setCallTime(String symbol, Long callTime) {
        coins.get(symbol).setCallTime(callTime);
    }
    public long getCallTime(String symbol) {
        return coins.get(symbol).getCallTime();
    }


    @Override
    public void setEnvironment(Environment environment) {
        CoinManagerService.environment = environment;
    }

    public void shutdown() {
        if (environment != null) {
            org.springframework.core.env.PropertySource<?> coinsSource = ((ConfigurableEnvironment) environment).getPropertySources().get("coins");
            assert coinsSource != null;
            Properties coinsProps = (Properties) coinsSource.getSource();

            for (Map.Entry<String, Coin> e : coins.entrySet()) {
                double threshold = e.getValue().getThreshold();
                coinsProps.setProperty(e.getKey(), String.valueOf(threshold));
            }
            try {
                URL res = getClass().getClassLoader().getResource("coins.properties");
                assert res != null;
                FileOutputStream outputStream = new FileOutputStream(res.getPath());
                coinsProps.store(outputStream, "Updated coins");
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
