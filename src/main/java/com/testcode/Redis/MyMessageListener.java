package com.testcode.Redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class MyMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // Implement your logic to handle the received Redis message
        String channel = new String(message.getChannel());
        String payload = new String(message.getBody());
        System.out.println("Received message: " + payload + " from channel: " + channel);
    }
}