package com.testcode.Kafka.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class KafkaController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public KafkaController(KafkaTemplate<String, String> kafkaTemplate, RedisTemplate<String, String> redisTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/chat")
    public ModelAndView getRequest() {
        return new ModelAndView("chat");
    }

    @PostMapping("/chat")
    public boolean postRequest(@RequestBody RequestData requestData) {
        String message = requestData.getRequestData();

        // Publish message to Kafka
        kafkaTemplate.send("my-topic", message);

        // Save message to Redis
        redisTemplate.opsForList().rightPush("messages", message);

        return true;
    }

    private static final List<SseEmitter> EMITTERS = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @GetMapping("/sse")
    @CrossOrigin(origins = "http://localhost:80/chat")
    public SseEmitter sseEmitter() {
        SseEmitter emitter = new SseEmitter(-1L); // never timeout
        EMITTERS.add(emitter);
        emitter.onCompletion(() -> EMITTERS.remove(emitter));
        emitter.onTimeout(() -> EMITTERS.remove(emitter));
        return emitter;
    }

    int count = 0;

    @KafkaListener(topics = "my-topic")
    public void consume(String message) {
        count++;
        System.out.println(count);

        // Save message to Redis
        redisTemplate.opsForList().rightPush("messages", message);

        for (SseEmitter sseEmitter : EMITTERS) {
            try {
                int num = Integer.parseInt(message);
                int complexAnswer = complicatedComputation(num);
                sseEmitter.send(SseEmitter.event().data(complexAnswer));
            } catch (Exception e) {
                EMITTERS.remove(sseEmitter);
                sseEmitter.completeWithError(e);
            }
        }
    }

    public int complicatedComputation(int n) {
        int result = 0;

        for (int i = 1; i <= n; i++) {
            int term = 1;

            for (int j = 1; j <= i; j++) {
                term *= j;
            }

            result += term;
        }

        return result;
    }

    public static class RequestData {
        private String requestData;

        public String getRequestData() {
            return requestData;
        }

        public void setRequestData(String requestData) {
            this.requestData = requestData;
        }
    }
}