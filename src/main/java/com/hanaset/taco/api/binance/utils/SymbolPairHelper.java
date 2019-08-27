package com.hanaset.taco.api.binance.utils;

public class SymbolPairHelper {
    public static String getBinanceSymbolPair(String coin) {
        return coin.replace("_", "").toUpperCase();
    }

    public static String getKuCoinSymbolPair(String coin) {
        return coin.replace("_", "-").toUpperCase();
    }

    public static String getWhalexSymbolPair(String coin) {
        return coin.replace("_", "/").toUpperCase();
    }

    public static String getBiboxSymbolPair(String coin) {
        return coin.toUpperCase();
    }

    public static String getBitzSymbolPair(String coin) {
        return coin;
    }

    public static String getIdexSymbolPair(String coin) {
        if (!coin.contains("_")) {
            return coin;
        }

        String[] tmp = coin.split("_");
        return tmp[1].toUpperCase() + "_" + tmp[0].toUpperCase();
    }

    public static String getHuobiSymbolPair(String coin) {
        return coin.replace("_", "").toLowerCase();
    }

    public static String getUpbitSymbolPair(String coin) {
        if (!coin.contains("_")) {
            return coin;
        }

        String[] tmp = coin.split("_");
        return tmp[1].toUpperCase() + "-" + tmp[0].toUpperCase();
    }

    public static String getBithumbCurrency(String coin) {

        if (!coin.contains("_")) {
            return coin.toUpperCase();
        }

        return coin.split("_")[0].toUpperCase();
    }

    public static String getHitBtcSymbolPair(String coin) {
        if (!coin.contains("_")) {
            return coin.toUpperCase();
        }

        return coin.replace("_", "").toUpperCase();
    }

    public static String getCoinOneCurrency(String coin) {
        if (!coin.contains("_")) {
            return coin.toUpperCase();
        }

        return coin.split("_")[0].toUpperCase();
    }

    public static String translateCoinByUpbit(String upbitCoin) {

        String market = upbitCoin.split("-")[0].toLowerCase();
        String asset =  upbitCoin.split("-")[1].toLowerCase();

        return asset+"_"+market;
    }
}
