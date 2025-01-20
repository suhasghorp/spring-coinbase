package org.example.spingcoinbase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Coin {
    private long callTime;
    private String symbol;
    private Double price;
    private double lowThreshold;
    private double highThreshold;
    //private long sequence;
}
