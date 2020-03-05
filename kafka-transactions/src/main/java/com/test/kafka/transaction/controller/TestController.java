package com.test.kafka.transaction.controller;


import com.test.kafka.transaction.service.CustomProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tx")
@RequiredArgsConstructor
public class TestController {

  private final CustomProducerService customProducerService;
  @Value("${app.kafka.topic}")
  private String appTopic;

  @PostMapping("")
  public void send() {
    customProducerService.sendInTransaction(
        appTopic,
        "Some message " + Math.round(Math.random() * 1000),
        false);
  }

  @PostMapping("error")
  public void sendWithError() {
    customProducerService.sendInTransaction(
        appTopic,
        "Some message with error " + Math.round(Math.random() * 1000),
        true);
  }
}
