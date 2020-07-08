package com.stas.learning.messaging.kafka.services;

import com.stas.learning.messaging.kafka.domain.DataKey;
import com.stas.learning.messaging.kafka.domain.DataChild;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProduceService {

  private final KafkaTemplate<DataKey, DataChild> kafkaTemplate;

  @Value("${app.kafka.data.topic}")
  private String topic;

  public void generate(DataKey key, DataChild value) {
    try {
      ListenableFuture<SendResult<DataKey, DataChild>> future =
          kafkaTemplate.send(topic, 0, key, value);

      SendResult<DataKey, DataChild> sendResult = future.get();
      log.info("send result: {}", sendResult);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    } finally {
      kafkaTemplate.flush();
    }

  }

  public void generateSomeMessages(int n) {
    for (int i = 0; i < n; i++) {
      generate(new DataKey("v1", "foo"), new DataChild("parentValue", "childValue"));
    }
  }
}
