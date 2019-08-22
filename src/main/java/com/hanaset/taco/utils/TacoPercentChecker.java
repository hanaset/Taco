package com.hanaset.taco.utils;

public class TacoPercentChecker {

    public static boolean profitCheck(Double bid, Double ask, Double percent) {

        if(bid - ask > ask + (ask * percent)) {
            return true;
        } else {
            return false;
        }
    }
}
