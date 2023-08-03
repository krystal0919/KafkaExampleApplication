package com.testcode.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    public String message;
//    @KafkaListener(topics = "my-topic")
//    public void consume(String message) throws Exception {
//        int num = Integer.parseInt(message);
//        int complexAnswer = complicatedComputation(num);
//        this.message = Integer.toString(complexAnswer);
//    }
//
//    public int complicatedComputation(int n) {
//        int result = 0;
//
//        for (int i = 1; i <= n; i++) {
//            int term = 1;
//
//            for (int j = 1; j <= i; j++) {
//                term *= j;
//            }
//
//            result += term;
//        }
//
//        return result;
//    }
}
