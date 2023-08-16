package com.testcode.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisController {

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private static final String OFFSETS_KEY = "kafka:offsets";

    @Autowired
    public RedisController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    // Store the message details in Redis
    public void storeMessage(String topic, int partition, long offset, String message) {
        String key = getMessageKey(topic, partition, offset);
        hashOperations.put(OFFSETS_KEY, key, message);
    }

    // Retrieve all stored messages from Redis
    public Map<String, String> retrieveAllMessages() {
        return hashOperations.entries(OFFSETS_KEY);
    }

    // Retrieve the stored message by topic, partition, and offset from Redis
    public String retrieveMessage(String topic, int partition, long offset) {
        String key = getMessageKey(topic, partition, offset);
        return hashOperations.get(OFFSETS_KEY, key);
    }

    // Get the Redis key for storing the message
    private String getMessageKey(String topic, int partition, long offset) {
        return topic + ":" + partition + ":" + offset;
    }
}