package com.stas.learning.messaging.kafka.requestreply.async.kafka;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class KafkaProducer {

  @Value("${app.kafka.onereq.topic}")
  private String requestTopic;

  @Value("${app.kafka.oneres.topic}")
  private String responseTopic;

  @Autowired
  private KafkaTemplate kafkaTemplate;

  @SneakyThrows
  public void start() {
    while (true) {
      Message<String> message = MessageBuilder
          .withPayload("")
          .setHeader(KafkaHeaders.TOPIC, requestTopic.getBytes())
          .setHeader(KafkaHeaders.REPLY_TOPIC, responseTopic.getBytes())
          .setHeader(KafkaHeaders.PARTITION_ID, 0)
          .setHeader(KafkaHeaders.CORRELATION_ID, UUID.randomUUID().toString().getBytes())
          .setHeader("X-Custom-Header", "Sending Custom Header with Spring Kafka")
          .build();

      log.info("sending message to request topic ='{}' response to topic='{}'", requestTopic, responseTopic);
      kafkaTemplate.send(message);

      Thread.sleep(5000);
    }
  }
}
