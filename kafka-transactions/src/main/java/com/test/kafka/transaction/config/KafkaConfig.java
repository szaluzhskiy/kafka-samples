package com.test.kafka.transaction.config;

import javax.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Configuration
@EnableKafka
@EnableTransactionManagement
public class KafkaConfig {

  // TODO: check why jpa repository can't work without bean with name exactly 'transactionManager'
  @Bean
  public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  @Bean
  public ChainedKafkaTransactionManager<Object, Object> chainedKafkaTransactionManager(
      KafkaTransactionManager<?, ?> kafkaTransactionManager,
      JpaTransactionManager transactionManager) {
    return new ChainedKafkaTransactionManager<>(kafkaTransactionManager, transactionManager);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<?, ?> transactionKafkaListenerContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
      ConsumerFactory<Object, Object> consumerFactory,
      ProducerFactory<Object, Object> producerFactory,
      ChainedKafkaTransactionManager<Object, Object> chainedKafkaTransactionManager,
      KafkaTemplate<Object, Object> kafkaTemplate) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    configurer.configure(factory, consumerFactory);
    factory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager);

    final SeekToCurrentErrorHandler errorHandler = new SeekToCurrentErrorHandler(
        (consumerRecord, e) -> log.error("Can't consume record {} because of error {}", consumerRecord, e, e),
        3
    );
    factory.setErrorHandler(errorHandler);
    factory.setStatefulRetry(true);
//    factory.setReplyTemplate(kafkaTemplate);
    factory.setReplyTemplate(new KafkaTemplate<Object, Object>(producerFactory){
      @Override
      protected ListenableFuture<SendResult> doSend(ProducerRecord producerRecord) {
        // emulate error in @SendTo
        throw new IllegalArgumentException("Custom send error");
      }
    });
    return factory;
  }
}
