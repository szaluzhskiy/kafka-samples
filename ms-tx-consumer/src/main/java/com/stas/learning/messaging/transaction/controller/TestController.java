package com.stas.learning.messaging.transaction.controller;


import com.stas.learning.messaging.transaction.domain.TxDataChild;
import com.stas.learning.messaging.transaction.domain.TxDataKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tx")
@Transactional
@RequiredArgsConstructor
public class TestController {

  private final KafkaTemplate<TxDataKey, TxDataChild> kafkaTemplate;

  @Value("${app.kafka.tx.data.topic}")
  private String topic;

  @PostMapping
  public void sendMessages() {
    TxDataKey txDataKey = new TxDataKey("1", "first name");
    TxDataChild txDataChild = new TxDataChild("parent value 1", "child value 1");
    kafkaTemplate.send(topic, txDataKey, txDataChild);
  }

  @PostMapping("error")
  public void sendErrorMessage() {
    TxDataKey txDataKeyError = new TxDataKey("error", "error");
    TxDataChild txDataChildError = new TxDataChild("error", "error");
    kafkaTemplate.send(topic, txDataKeyError, txDataChildError);
  }
}
