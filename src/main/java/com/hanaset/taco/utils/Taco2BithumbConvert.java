package com.hanaset.taco.utils;

import com.hanaset.taco.item.TransactionItem;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.math.BigDecimal;

@Slf4j
public class Taco2BithumbConvert {

    final private static JSONParser jsonParser = new JSONParser();

    public static TransactionItem convertTransaction(String data, String pair) {

        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
            JSONArray jsonArray = (JSONArray)jsonObject.get("data");

            JSONObject bithumbObject = (JSONObject)jsonArray.get(0);

            return TransactionItem.builder()
                    .market("bithumb")
                    .pair(pair)
                    .price(new BigDecimal(bithumbObject.get("price").toString()))
                    .amount(new BigDecimal(bithumbObject.get("units_traded").toString()))
                    .build();

        }catch (ParseException e) {
            log.error("[parser error] -> {}", data);
        }

        return null;
    }
}
