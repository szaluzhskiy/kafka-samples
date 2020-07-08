package com.stas.learning.messaging.kafka.controller;

import com.stas.learning.messaging.kafka.services.ConsumerSeekService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerSeekController {
  @Autowired
  private ConsumerSeekService seekService;

  @GetMapping("/read/{topic}/{partition}/{offset}")
  public void read(@PathVariable("topic") String topic, @PathVariable("partition") Integer partition, @PathVariable("offset") long offset) {
    seekService.read(offset, new TopicPartition(topic, partition));
  }
}
