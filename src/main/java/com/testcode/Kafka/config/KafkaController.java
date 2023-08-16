package com.testcode.Kafka.config;

import com.testcode.Redis.RedisController;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class KafkaController implements ConsumerSeekAware {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaListenerEndpointRegistry endpointRegistry;
    private Consumer<?, ?> assignedConsumer;
    private static final List<SseEmitter> EMITTERS = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @Autowired
    private RedisController redisController;

    @Autowired
    public KafkaController(KafkaTemplate<String, String> kafkaTemplate, KafkaListenerEndpointRegistry endpointRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.endpointRegistry = endpointRegistry;
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

        return true;
    }

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
    public void consume(ConsumerRecord<String, String> message) throws IOException {
        count++;
        //System.out.println(count);

        for (SseEmitter sseEmitter : EMITTERS) {
            try {
                int num = Integer.parseInt(message.value());
                int complexAnswer = complicatedComputation(num);
                sseEmitter.send(SseEmitter.event().data(complexAnswer));
            } catch (NumberFormatException e) {
                // Handle non-integer values gracefully
                sseEmitter.send(SseEmitter.event().data("Invalid value: " + message.value()));
            } catch (Exception e) {
                EMITTERS.remove(sseEmitter);
                sseEmitter.completeWithError(e);
            }
        }

        // Store the offset in Redis
        String topic = message.topic();
        int partition = message.partition();
        long offset = message.offset();
        redisController.storeMessage(topic, partition, offset, message.value());
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