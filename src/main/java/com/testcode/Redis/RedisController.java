package com.testcode.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisController {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String OFFSETS_KEY = "kafka:offsets";
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    public RedisController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Store the offset in Redis
    public void storeOffset(String topic, int partition, long offset) {
        try {
            String key = getOffsetKey(topic, partition);
            redisTemplate.opsForValue().set(key, String.valueOf(offset));
        } catch (Exception e) {
            LOGGER.error("Error storing offset in Redis: ", e);
        }
    }

    // Retrieve the stored offset from Redis
    public long retrieveStoredOffset(String topic, int partition) {
        try {
            String key = getOffsetKey(topic, partition);
            String offset = redisTemplate.opsForValue().get(key);
            return offset != null ? Long.parseLong(offset) : -1;
        } catch (Exception e) {
            LOGGER.error("Error retrieving offset from Redis: ", e);
            return -1;
        }
    }

    // Get the Redis key for storing the offset
    private String getOffsetKey(String topic, int partition) {
        return OFFSETS_KEY + ":" + topic + ":" + partition;
    }
}