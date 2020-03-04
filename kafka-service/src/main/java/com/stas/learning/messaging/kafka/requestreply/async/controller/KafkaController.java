package com.stas.learning.messaging.kafka.requestreply.async.controller;

import com.stas.learning.messaging.kafka.requestreply.async.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

  @Autowired
  private KafkaProducer kafkaProducer;

  @RequestMapping(method = RequestMethod.POST, path = "/rr/start")
  public void start() {
    kafkaProducer.start();
  }
}
