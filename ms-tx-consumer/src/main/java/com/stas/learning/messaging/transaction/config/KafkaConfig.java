package com.stas.learning.messaging.transaction.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
      ConsumerFactory<Object, Object> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    configurer.configure(factory, consumerFactory);

//    final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
//    backOffPolicy.setInitialInterval(10000L);
//    backOffPolicy.setMaxInterval(60000L);
//    backOffPolicy.setMultiplier(1.5);
//    final RetryTemplate retryTemplate = new RetryTemplate();
//    retryTemplate.setRetryPolicy(new AlwaysRetryPolicy());
//    retryTemplate.setBackOffPolicy(backOffPolicy);
//    factory.setRetryTemplate(retryTemplate);

    factory.setStatefulRetry(true);

    // TODO: move to application.yml
    ExponentialBackOff backOff = new ExponentialBackOff();
    backOff.setInitialInterval(10000L);
    backOff.setMaxInterval(60000L);
    backOff.setMultiplier(1.5);
    // can be configured with backoff only on spring-kafka:2.3.2+ version;
    // so we need to use maxFailures=-1 for infinite retry
    final SeekToCurrentErrorHandler errorHandler = new SeekToCurrentErrorHandler(
        (consumerRecord, e) -> log.error("Can't consume record {} because of error {}", consumerRecord, e, e),
        -1
    );
    factory.setErrorHandler(errorHandler);
    return factory;
  }
}
