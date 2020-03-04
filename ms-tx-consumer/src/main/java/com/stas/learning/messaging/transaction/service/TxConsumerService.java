package com.stas.learning.messaging.transaction.service;

import com.stas.learning.messaging.transaction.domain.TxDataParent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TxConsumerService {

  @KafkaListener(topics = "${app.kafka.tx.data.topic}", containerFactory = "kafkaListenerContainerFactory")
  public void listen(@Payload TxDataParent value) {
    log.info("Consumed: {}", value);
    // TODO: save to DB
    if (value.getParentValue().contains("error")) {
      throw new IllegalStateException("Custom error to check transaction: " + value);
    }
  }
}
