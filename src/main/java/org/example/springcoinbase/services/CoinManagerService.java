package org.example.springcoinbase.services;

import lombok.Getter;
import org.example.springcoinbase.model.Coin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component

public class CoinManagerService implements EnvironmentAware {

    private static Environment environment;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ResourceLoader resourceLoader;


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

    public void saveCoins(){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Coin> coin : coins.entrySet()) {
            String symbol = coin.getKey();
            sb.append(symbol).append("=").append(coin.getValue().getLowThreshold()).append(",")
                    .append(coin.getValue().getHighThreshold()).append("\n");
        }
        s3Service.uploadFile("my-coinbase-bucket", "coins.properties", sb.toString());
    }

    public void loadCoins(){
        List<String> props = s3Service.readFile("my-coinbase-bucket", "coins.properties");
        for (String prod : props) {
            String[] split = prod.split("=");
            double lowThreshold = split[1].isEmpty() ? 0.0 : Double.parseDouble(split[1].split(",")[0]);
            double highThreshold = split[1].isEmpty() ? 0.0 : Double.parseDouble(split[1].split(",")[1]);
            coins.put(split[0], new Coin(0L, split[0], null, lowThreshold, highThreshold));
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
