package com.hanaset.taco.utils;

import com.hanaset.taco.item.TransactionItem;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.math.BigDecimal;

@Slf4j
public class Taco2CoinoneConvert {

    final private static JSONParser jsonParser = new JSONParser();

    public static JSONObject convertJSONObject(String data) throws ParseException {

        JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
        JSONArray jsonArray = (JSONArray) jsonObject.get("completeOrders");
        JSONObject coinoneObject = (JSONObject) jsonArray.get(0);

        return coinoneObject;
    }

    public static TransactionItem convertTransaction(String data, String pair) {

        try {
            JSONObject coinoneObject = convertJSONObject(data);

            return TransactionItem.builder()
                    .market("coinone")
                    .pair(pair)
                    .price(new BigDecimal(coinoneObject.get("price").toString()))
                    .amount(new BigDecimal(coinoneObject.get("qty").toString()))
                    .build();
        } catch (ParseException e) {
            log.error("[parser error] -> {}", e.getUnexpectedObject());
        }

        return null;
    }
}
