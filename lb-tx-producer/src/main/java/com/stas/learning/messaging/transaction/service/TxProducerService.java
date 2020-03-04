package com.stas.learning.messaging.transaction.service;

import com.stas.learning.messaging.transaction.config.TxKafkaConfig;
import com.stas.learning.messaging.transaction.domain.TxDataChild;
import com.stas.learning.messaging.transaction.domain.TxDataKey;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

@Profile(TxKafkaConfig.NOT_DISABLE_TRANSACTION_PROFILE)
@Slf4j
@Service
@RequiredArgsConstructor
public class TxProducerService {

  private final KafkaTemplate<TxDataKey, TxDataChild> transactionKafkaTemplate;

  @Value("${app.kafka.tx.data.topic}")
  private String topic;

  @Transactional(transactionManager = "transactionManager")
  public void sendWithAnnotation(TxDataKey key, TxDataChild value) {
    try {
      ListenableFuture<SendResult<TxDataKey, TxDataChild>> future =
          transactionKafkaTemplate.send(topic, key, value);

      SendResult<TxDataKey, TxDataChild> sendResult = future.get();
      log.info("send result: {}", sendResult);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    } finally {
      transactionKafkaTemplate.flush();
    }
  }

  public void sendInTransactionWithError(TxDataKey key, TxDataChild value) {
    sendInTransaction(key, value, true);
  }

  public void sendInTransaction(TxDataKey key, TxDataChild value) {
    sendInTransaction(key, value, false);
  }

  private void sendInTransaction(TxDataKey key, TxDataChild value, boolean withError) {
    try {
      ListenableFuture<SendResult<TxDataKey, TxDataChild>> future =
          transactionKafkaTemplate.executeInTransaction(
              t -> {
                final ListenableFuture<SendResult<TxDataKey, TxDataChild>> sendResult = t.send(topic, key, value);
                if (withError) {
                  throw new IllegalStateException("Custom exception");
                }
                return sendResult;
              }
          );

      SendResult<TxDataKey, TxDataChild> sendResult = future.get();
      log.info("send result: {}", sendResult);
    } catch (Exception e) {
      // TODO: do compensation
      throw new RuntimeException(e);
    } finally {
      transactionKafkaTemplate.flush();
    }
  }
}
