package com.stas.learning.messaging.kafka.controller;

import com.stas.learning.messaging.kafka.domain.DataChild;
import com.stas.learning.messaging.kafka.domain.DataKey;
import com.stas.learning.messaging.kafka.services.ProduceService;
import com.stas.learning.messaging.transaction.domain.TxDataChild;
import com.stas.learning.messaging.transaction.domain.TxDataKey;
import com.stas.learning.messaging.transaction.service.TxKafkaProducerService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ProducerController {

  @Autowired
  private ProduceService producer;

  @Autowired(required = false) // just for work with profiles
  private TxKafkaProducerService txProducerService;

  @PostMapping
  public void generateTenMessages() {
    for (int i = 0; i < 1; i++) {
      producer.generate(new DataKey("v1", "foo"), new DataChild("parentValue", "fooValue"));
    }
  }

  @PostMapping(path = "tx")
  public void sendTxEvent(@RequestParam(required = false) String key, @RequestParam(required = false) String value) {
    if (Objects.nonNull(txProducerService)) {
      TxDataKey txDataKey = new TxDataKey(key, "service key");
      TxDataChild txDataChild = new TxDataChild("parent value from service", value);
      txProducerService.send(txDataKey, txDataChild);
    }
  }
}
