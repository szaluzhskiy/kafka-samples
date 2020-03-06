package com.stas.learning.messaging.transaction.service;


import com.stas.learning.messaging.transaction.config.TxKafkaConfig;
import com.stas.learning.messaging.transaction.domain.TxDataChild;
import com.stas.learning.messaging.transaction.domain.TxDataKey;
import com.stas.learning.messaging.transaction.exception.FileStorageException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Profile(TxKafkaConfig.NOT_DISABLE_TRANSACTION_PROFILE)
@Slf4j
@Service
@RequiredArgsConstructor
public class TxKafkaProducerService {

  private final KafkaTemplate<TxDataKey, TxDataChild> transactionKafkaTemplate;
  private final FileStorageService fileStorageService;
  private final RetryTemplate txKafkaProducerRetryTemplate;
  @Value("${app.kafka.tx.data.topic}")
  private String topic;

  public void send(TxDataKey key, TxDataChild value) {
    String fileName = key.getName() + key.getVersion() + ".txt";
    try {
      SendResult<TxDataKey, TxDataChild> sendResult =
          transactionKafkaTemplate
              .executeInTransaction(kafkaOperations -> {
                writeToFileStorage(key, value, fileName);
                return sendToKafka(kafkaOperations, key, value);
              });
      log.info("send result: {}", sendResult);
    } catch (Exception e) {
      log.error("Error while processing values. Starting compensation actions", e);
      // TODO: what should we do if error is 409 Conflict? Here we'll delete existed file
      fileStorageService.delete(fileName);
      throw new RuntimeException(e);
    } finally {
      transactionKafkaTemplate.flush();
    }
  }

  private SendResult<TxDataKey, TxDataChild> sendToKafka(
      KafkaOperations<TxDataKey, TxDataChild> operations, TxDataKey key, TxDataChild value) {
    final ListenableFuture<SendResult<TxDataKey, TxDataChild>> listenableFuture = operations.send(topic, key, value);
    try {
      return listenableFuture.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new KafkaException(e);
    }
  }

  private void writeToFileStorage(TxDataKey key, TxDataChild value, String fileName) {
    try {
      final Path tempFile = Files.createTempFile("tx-temp-", fileName);
      try (FileWriter fileWriter = new FileWriter(tempFile.toFile())) {
        fileWriter.write(key.toString());
        fileWriter.write(System.lineSeparator());
        fileWriter.write(value.toString());
      }
      fileStorageService.put(fileName, tempFile.toFile());
    } catch (IOException e) {
      throw new FileStorageException("Error while preparing file", e);
    }
  }
}
