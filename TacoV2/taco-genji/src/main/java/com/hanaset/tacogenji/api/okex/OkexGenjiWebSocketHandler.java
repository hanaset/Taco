package com.hanaset.tacogenji.api.okex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OkexGenjiWebSocketHandler extends BinaryWebSocketHandler {


    public OkexGenjiWebSocketHandler() {
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer byteMessage = message.getPayload();
        CharBuffer charBuffer = StandardCharsets.UTF_16BE.decode(byteMessage);

        JSONObject jsonObject = (JSONObject) JSONValue.parse(charBuffer.toString());
        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println(charBuffer.toString());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connected to Okex Web Socket Server! Session - " + session);
    }
}
