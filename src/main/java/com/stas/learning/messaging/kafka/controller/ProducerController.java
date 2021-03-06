package com.stas.learning.messaging.kafka.controller;

import com.stas.learning.messaging.kafka.domain.DataKey;
import com.stas.learning.messaging.kafka.domain.DataChild;
import com.stas.learning.messaging.kafka.services.ProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

  @Autowired
  private ProduceService producer;

  @GetMapping("/generate")
  public void generateTenMessages() {
    for (int i = 0; i < 100; i++) {
      producer.generate(new DataKey("v1", "foo-" + i), new DataChild("parentValue", "fooValue-" + i));
    }
  }
}
