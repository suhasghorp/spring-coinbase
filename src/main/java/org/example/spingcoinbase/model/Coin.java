package org.example.spingcoinbase.model;

@lombok.Data
public class Coin {

    private volatile long callTime;
    private String symbol;
    private Double price;
    private double threshold;
    private long sequence;

    public Coin(String symbol, Double price, double threshold) {
        this.symbol = symbol;
        this.price = price;
        this.threshold = threshold;
    }


}
