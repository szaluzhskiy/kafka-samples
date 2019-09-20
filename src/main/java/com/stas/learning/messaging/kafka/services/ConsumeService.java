package com.stas.learning.messaging.kafka.services;

import com.stas.learning.messaging.kafka.domain.DataParent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Setter
@Component
@Slf4j
public class ConsumeService {

  @KafkaListener(topics = "${app.kafka.data.topic}")
  public void consume(DataParent parent) {
    System.out.println(parent.parentValue);
  }
}
