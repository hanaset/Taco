package com.hanaset.taco.utils;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Slf4j
public class Taco2UpbitConvert {

    final private static JSONParser jsonPaser = new JSONParser();

    public static JSONObject convertJSONObject(String data) throws ParseException {
        JSONArray jsonArray = (JSONArray) jsonPaser.parse(data);
        JSONObject upbitObject = (JSONObject) jsonArray.get(0);

        return upbitObject;
    }

    public static String convertPair(String pair) {

        String data[] = pair.split("-");

        return data[1];
    }
}
