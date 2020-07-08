package com.stas.learning.messaging.kafka.requestreply.async.kafka;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class KafkaRemoteConsumer {

  @KafkaListener(topics = "${app.kafka.onereq.topic}")
  @SendTo
  public String start(@Payload String data,
                      @Header("X-Custom-Header") String customHeader) {
    log.info("data={}, custom header={}", data, customHeader);
    return "OK";
  }
}
