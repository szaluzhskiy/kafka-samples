package com.test.kafka.transaction.service;

import com.test.kafka.transaction.entity.Message;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomConsumerService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final MessageTxService messageTxService;

  @Value("${app.kafka.end.topic}")
  private String endTopic;

  @KafkaListener(topics = "${app.kafka.topic}", containerFactory = "transactionKafkaListenerContainerFactory")
  @SendTo("${app.kafka.send.to.topic}")
  public String listen(String value, MessageHeaders messageHeaders) {
    log.info("Consumed: headers = {}, value = {}", messageHeaders, value);
    final Message message = messageTxService
        .create(messageHeaders.get(KafkaHeaders.RECEIVED_MESSAGE_KEY, String.class), value);
    return message.toString();
  }

  @KafkaListener(topics = "${app.kafka.send.to.topic}", containerFactory = "transactionKafkaListenerContainerFactory")
  public void listenReply(String value) {
    log.info("Consumed reply: {}", value);
    kafkaTemplate.send(endTopic, UUID.randomUUID().toString(), value);
  }
}
