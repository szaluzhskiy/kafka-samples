package com.test.kafka.transaction.service;

import com.test.kafka.transaction.domain.TxDataParent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TxConsumerService {

  @KafkaListener(topics = "${app.kafka.tx.data.topic}",
      groupId = "tx-consumer-id",
      containerFactory = "transactionKafkaListenerContainerFactory")
  public void listen(@Payload TxDataParent value) {
    log.info("Consumed: {}", value);
    // TODO: save to DB
//    throw new IllegalStateException("Custom error to check transaction: " + value);
  }
}
