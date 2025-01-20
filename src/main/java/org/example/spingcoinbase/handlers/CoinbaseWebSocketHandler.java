package org.example.spingcoinbase.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.spingcoinbase.services.CoinManagerService;
import org.example.spingcoinbase.services.ConsumerService;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CoinbaseWebSocketHandler extends TextWebSocketHandler  {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String COINBASE_WS_URL = "ws://ws-feed.exchange.coinbase.com";

    private final CoinManagerService coinManagerService;
    private final ConsumerService consumerService;

    public CoinbaseWebSocketHandler(CoinManagerService coinManagerService, ConsumerService consumerService) {
        this.coinManagerService = coinManagerService;
        this.consumerService = consumerService;
    }


    public void connect() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(this, COINBASE_WS_URL).get();
        if (session.isOpen()) {
            log.info("WebSocket connection established: {}", session.getRemoteAddress());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error occurred: {}", exception.getMessage(), exception);
        reconnect();
    }
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    private void reconnect() {
        try {
            log.info("Reconnecting to Coinbase WebSocket...");
            TimeUnit.SECONDS.sleep(5); // Wait before reconnecting
            connect();
        } catch (Exception e) {
            log.error("Failed to reconnect: {}", e.getMessage(),e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connected to Coinbase WebSocket");
        var coins = coinManagerService.getCoins();
        String coinList = coins.keySet().stream().map(key -> "\"" + key + "\"") // Add double quotes around each key
                .collect(Collectors.joining(","));
        String subscribeMessage = "{\n" +
                "  \"type\": \"subscribe\",\n" +
                "  \"channels\": [{\"name\": \"ticker\", \"product_ids\": [" + coinList + "]}, {\"name\": \"heartbeat\", \"product_ids\": [\"BTC-USD\"]}]\n" +
                "}";
        session.sendMessage(new TextMessage(subscribeMessage));
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
            consumerService.processMessage(coin);
        }
        if (jsonNode.has("type") && jsonNode.get("type").asText().equals("heartbeat")) {
            var lastHeartbeatTime = System.currentTimeMillis();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Connection closed: {}", status);
        if (status.getCode() != CloseStatus.NORMAL.getCode()){
            log.info("Trying to reconnect after status code: {}", status.getCode());
            reconnect();
        }
    }




}