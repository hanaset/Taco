package com.hanaset.taco.utils;

public class TacoPercentChecker {

    public static boolean profitCheck(Double bid, Double ask, Double percent) {

        return (bid - ask) / bid * 100.f > percent;
    }
}
