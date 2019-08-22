package com.hanaset.taco.utils;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Slf4j
public class Taco2JsonConvert {

    public static JSONObject convertJSONObject (String data) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object object = jsonParser.parse(data);
            JSONObject jsonObject = (JSONObject) object;

            return jsonObject;

        }catch (ParseException e) {
            log.error("parser error -> {}", e.getMessage());
        }

        return null;
    }
}
