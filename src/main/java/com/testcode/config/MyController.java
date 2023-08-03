package com.testcode.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MyController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    public MyController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final List<String> MESSAGE_CACHE = new ArrayList<>();

    @RequestMapping(value = "/process", method = {RequestMethod.GET})
    @CrossOrigin
    public ModelAndView getRequest() {
        return new ModelAndView("process");
    }

    @RequestMapping(value = "/process", method = {RequestMethod.POST})
    public boolean postRequest(@RequestParam(value = "requestData", required = true) String requestData) {

        kafkaTemplate.send("my-topic", requestData);
        return true;
    }

    private static final List<SseEmitter> EMITTERS = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

   @GetMapping("/sse")
   @CrossOrigin(origins = "http://localhost:80/process")
   public SseEmitter sseEmitter() throws IOException {
       SseEmitter emitter = new SseEmitter(-1L); //never timeout
       EMITTERS.add(emitter);
       emitter.onCompletion(() -> EMITTERS.remove(emitter));
       emitter.onTimeout(() -> EMITTERS.remove(emitter));
//       executorService.execute(() -> {
//           try {
//               while (true) {
//                   if (consumer.message != null) {
//                       for (SseEmitter sseEmitter : EMITTERS) {
//                           sseEmitter.send(SseEmitter.event().data(consumer.message));
//                       }
//                   }
//                   consumer.message = null;
//                   Thread.sleep(50);
//               }
//           } catch (Exception e) {
//               EMITTERS.remove(emitter);
//               emitter.completeWithError(e);
//           }
//       });
       return emitter;
   }

    int count = 0;

    @KafkaListener(topics = "my-topic")
    public void consume(String message) {
        count++;
        System.out.println(count);
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
}
