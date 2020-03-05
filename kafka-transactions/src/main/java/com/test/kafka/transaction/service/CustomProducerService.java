package com.test.kafka.transaction.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomProducerService {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public void sendInTransaction(String topic, String value, boolean withError) {
    try {
      final String key = UUID.randomUUID().toString();
      log.info("Send message: key = {}, value = {}", key, value);
      ListenableFuture<SendResult<String, String>> future =
          kafkaTemplate.executeInTransaction(
              t -> {
                final ListenableFuture<SendResult<String, String>> sendResult = t.send(topic, key, value);
                if (withError) {
                  throw new IllegalStateException(
                      String.format("Custom exception: key = %s, value = %s", key, value));
                }
                return sendResult;
              }
          );

      SendResult<String, String> sendResult = future.get(30, TimeUnit.SECONDS);
      log.info("send result: {}", sendResult);
    } catch (Exception e) {
      log.error("Error while sending message to kafka", e);
      throw new RuntimeException(e);
    } finally {
      kafkaTemplate.flush();
    }
  }
}
