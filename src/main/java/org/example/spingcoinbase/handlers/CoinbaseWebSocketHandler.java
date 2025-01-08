package org.example.spingcoinbase.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.spingcoinbase.TelemetryLogger;
import org.example.spingcoinbase.services.CoinManagerService;
import org.example.spingcoinbase.services.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class CoinbaseWebSocketHandler extends TextWebSocketHandler  {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private WebSocketSession session = null;
    @Autowired
    private CoinManagerService coinManagerService;
    @Autowired
    private ConsumerService consumerService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        TelemetryLogger.info("Connected to Coinbase WebSocket");
        this.session = session;
        var coins = coinManagerService.getCoins();
        // Add double quotes around each key
        String coinList = coins.keySet().stream().map(key -> "\"" + key + "\"") // Add double quotes around each key
                .collect(Collectors.joining(","));
        // Subscribe to BTC-USD ticker
        String subscribeMessage = "{\n" +
                "  \"type\": \"subscribe\",\n" +
                "  \"channels\": [{\"name\": \"ticker\", \"product_ids\": [" + coinList + "]}]\n" +
                "}";
        session.sendMessage(new TextMessage(subscribeMessage));
    }

    public void sendPing() throws Exception {
        session.sendMessage(new BinaryMessage("PING".getBytes()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);

        // Process the message from Coinbase WebSocket
        if (jsonNode.has("type") && jsonNode.get("type").asText().equals("ticker")) {

            double price = jsonNode.get("price").asDouble();
            String ticker = jsonNode.get("product_id").asText();
            long sequence = jsonNode.get("sequence").asLong();
            var coin = coinManagerService.getCoins().get(ticker);
            coin.setPrice(price);
            coin.setSequence(sequence);
            //TelemetryLogger.info("Coin: " + ticker + ", Price: " + price + ", Sequence: " + sequence);
            consumerService.processMessage(coin);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        TelemetryLogger.info("Connection closed: " + status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        TelemetryLogger.error("Error occurred: " + exception.getMessage());
    }


}