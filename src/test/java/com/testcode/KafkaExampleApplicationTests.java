
package com.testcode;
/*
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KafkaExampleApplicationTests {

	@Test
	void contextLoads() {

	}

}
 */

/*
import com.testcode.config.MyController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.servlet.ModelAndView;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KafkaExampleApplicationTests {

	@Mock
	private KafkaTemplate<String, String> kafkaTemplate;

	@InjectMocks
	private MyController myController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testProcessRequest() {
		String requestData = "testData";
		when(kafkaTemplate.send("my-topic", requestData)).thenReturn(null);

		ModelAndView modelAndView = myController.processRequest(requestData);

		verify(kafkaTemplate).send(eq("my-topic"), eq(requestData));
	}
}
 */

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class KafkaExampleApplicationTests {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;

	@Test
	public void testSendMessage() throws Exception {
		String topicName = "test-topic";
		String message = "Hello, Kafka!";
		kafkaTemplate.send(topicName, message);
		Consumer<String, String> consumer = createConsumer();
		consumer.subscribe(Collections.singletonList(topicName));
		ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
		assertThat(records).isNotNull();
		assertThat(records.count()).isEqualTo(1);
		ConsumerRecord<String, String> record = records.iterator().next();
		assertThat(record.key()).isNull();
		assertThat(record.value()).isEqualTo(message);
		consumer.close();
	}

	private Consumer<String, String> createConsumer() {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "false", embeddedKafkaBroker);
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer())
				.createConsumer();
	}
}



