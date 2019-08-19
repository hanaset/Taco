package com.hanaset.taco.utils;

import com.hanaset.taco.item.TransactionItem;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.math.BigDecimal;

@Slf4j
public class Taco2UpbitConvert {

    final private static JSONParser jsonPaser = new JSONParser();

    public static JSONObject convertJSONObject(String data) throws ParseException {
        JSONArray jsonArray = (JSONArray) jsonPaser.parse(data);
        JSONObject upbitObject = (JSONObject) jsonArray.get(0);

        return upbitObject;
    }

    public static TransactionItem convertTransaction(String data, String pair) {

        try {

            JSONObject upbitObject = convertJSONObject(data);

            return TransactionItem.builder()
                    .market("upbit")
                    .pair(pair)
                    .price(new BigDecimal(upbitObject.get("trade_price").toString()))
                    .amount(new BigDecimal(upbitObject.get("trade_volume").toString()))
                    .build();

        } catch (ParseException e) {
            log.error("[parser error] -> {}", data);
        }

        return null;
    }
}
