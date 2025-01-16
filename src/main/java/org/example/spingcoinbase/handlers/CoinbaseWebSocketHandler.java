package org.example.spingcoinbase.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.spingcoinbase.TelemetryLogger;
import org.example.spingcoinbase.services.CoinManagerService;
import org.example.spingcoinbase.services.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class CoinbaseWebSocketHandler extends TextWebSocketHandler  {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String COINBASE_WS_URL = "ws://ws-feed.exchange.coinbase.com";
    private WebSocketSession session = null;
    @Autowired
    private CoinManagerService coinManagerService;
    @Autowired
    private ConsumerService consumerService;

    public void connect() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(this, COINBASE_WS_URL).get();
        if (session.isOpen()) {
            System.out.println("WebSocket connection established: " + session.getRemoteAddress());
            //twilioCallTask.sms("Coinbase WebSocket connection established");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Error occurred: " + exception.getMessage());
        //twilioCallTask.sms("Coinbase WebSocket connection reconnect");
        reconnect();
    }
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    private void reconnect() {
        try {
            System.out.println("Reconnecting to Coinbase WebSocket...");
            TimeUnit.SECONDS.sleep(5); // Wait before reconnecting
            connect();
        } catch (Exception e) {
            System.out.println("Failed to reconnect: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
            coin.setSequence(sequence);
            //TelemetryLogger.info("Coin: " + ticker + ", Price: " + price + ", Sequence: " + sequence);
            consumerService.processMessage(coin);
        }
        if (jsonNode.has("type") && jsonNode.get("type").asText().equals("heartbeat")) {
            var lastHeartbeatTime = System.currentTimeMillis();
            //System.out.println("Received heartbeat: " + message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        TelemetryLogger.info("Connection closed: " + status);
        if (status.getCode() != CloseStatus.NORMAL.getCode()){
            TelemetryLogger.info("Trying to reconnect after status code: " + status.getCode());
            reconnect();
        }
    }




}