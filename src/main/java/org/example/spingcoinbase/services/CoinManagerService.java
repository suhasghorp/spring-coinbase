package org.example.spingcoinbase.services;

import lombok.Getter;
import org.example.spingcoinbase.model.Coin;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
@PropertySource(value = { "classpath:coins.properties" }, ignoreResourceNotFound = false, name = "coins")

public class CoinManagerService implements EnvironmentAware {

    private static Environment environment;

    public void updateCoinLowThreshold(String symbol, double price) {
        coins.get(symbol).setLowThreshold(price);
    }
    public void updateCoinHighThreshold(String symbol, double price) {
        coins.get(symbol).setHighThreshold(price);
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

                String symbol = e.getKey().toString();
                String threshold = e.getValue().toString();
                double lowThreshold = threshold.isEmpty() ? 0.0 : Double.parseDouble(threshold.split(",")[0]);
                double highThreshold = threshold.isEmpty() ? 0.0 : Double.parseDouble(threshold.split(",")[1]);
                coins.put(symbol, new Coin(0L, symbol, null, lowThreshold, highThreshold));
            }

        }
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
}
