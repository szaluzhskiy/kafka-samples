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
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class KafkaLocalConsumer {

  @KafkaListener(topics = "${app.kafka.oneres.topic}")
  public void listen(@Payload String data, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
    log.info("Remote server send responce data = {}, correlationID = {}", data, correlationId);
  }
}
