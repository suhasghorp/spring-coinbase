package org.example.spingcoinbase.tasks;

import org.example.spingcoinbase.model.Coin;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Component
public class SinchTask {
    private Set<Coin> coins = new HashSet<>();
    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void call() {
        try {
            final String key = "2774d614-51cb-4ba4-a389-3c16092aff68";
            final String secret = "PXFUTlcezkqO6MtoiYqOFQ==";
            var httpClient = HttpClient.newBuilder().build();
            String text = "Price Alert!";
            if (!coins.isEmpty()) {
                for (Coin c : coins) {
                    text += String.format("The price of %s has dropped to %.2f which is lower than %.2f.",
                            c.getSymbol(), c.getPrice(), c.getThreshold());
                }

                var payload = """
                         {
                            "method": "ttsCallout",
                             "ttsCallout": {
                                "cli": "+12064743758",
                                "destination": {
                                    "type": "number",
                                    "endpoint": "+16467454465"
                                },
                                "locale": "en-US",
                                "text": "%s"
                             }
                         }
                        """;
                payload = String.format(payload, text);

                var host = "https://calling.api.sinch.com";
                var pathname = "/calling/v1/callouts";
                var request = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .uri(URI.create(host + pathname))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((key + ":" + secret).getBytes()))
                        .build();
                System.out.println(payload);
                var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
                coins.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCoin(Coin coin) {
        coins.add(coin);
    }
}
