package com.test.kafka.transaction.service;

import com.test.kafka.transaction.entity.Message;
import com.test.kafka.transaction.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageTxService {

  private final MessageRepository messageRepository;

  @Transactional(propagation = Propagation.MANDATORY,
      rollbackFor = Exception.class,
      transactionManager = "chainedKafkaTransactionManager")
  public Message create(String key, String value) {
    Message message = new Message()
        .setKey(key)
        .setValue(value);
    return messageRepository.save(message);
  }
}
