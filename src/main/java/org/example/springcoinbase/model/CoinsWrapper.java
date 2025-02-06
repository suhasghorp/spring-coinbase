package org.example.springcoinbase.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class CoinsWrapper {
    private ArrayList<Coin> coins;
}
