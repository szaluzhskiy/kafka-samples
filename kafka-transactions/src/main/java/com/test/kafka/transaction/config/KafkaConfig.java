package com.test.kafka.transaction.config;

import com.test.kafka.transaction.domain.TxDataChild;
import com.test.kafka.transaction.domain.TxDataKey;
import com.test.kafka.transaction.serializers.TxDataJsonDeserializer;
import com.test.kafka.transaction.serializers.TxDataJsonSerializer;
import com.test.kafka.transaction.serializers.TxDataKeyDeserializer;
import com.test.kafka.transaction.serializers.TxDataKeySerializer;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile("!disableAudit")
@Slf4j
@Configuration
@EnableKafka
@EnableTransactionManagement
public class KafkaConfig {

  @Autowired
  private KafkaProperties kafkaProperties; // default configuration from application.yml

  @Bean
  public Map<String, Object> transactionProducerConfig() {
    Map<String, Object> configurations = new HashMap<>(kafkaProperties.buildProducerProperties());
    configurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, TxDataKeySerializer.class.getCanonicalName());
    configurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TxDataJsonSerializer.class.getCanonicalName());
    return configurations;
  }

  @Bean
  public ProducerFactory<TxDataKey, TxDataChild> producerFactory() {
    DefaultKafkaProducerFactory<TxDataKey, TxDataChild> factory =
        new DefaultKafkaProducerFactory<>(transactionProducerConfig());
    factory.setTransactionIdPrefix("test-tx-id-");
    return factory;
  }

  @Bean
  public KafkaTransactionManager<TxDataKey, TxDataChild> transactionManager(
      ProducerFactory<TxDataKey, TxDataChild> producerFactory) {
    return new KafkaTransactionManager<>(producerFactory);
  }

  @Bean
  public KafkaTemplate<TxDataKey, TxDataChild> transactionKafkaTemplate(
      ProducerFactory<TxDataKey, TxDataChild> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  // Use if we want one transaction with DB
//  @Bean
//  public ChainedKafkaTransactionManager<Object, Object> chainedTm(
//      KafkaTransactionManager<String, String> ktm,
//      DataSourceTransactionManager dstm) {
//
//    return new ChainedKafkaTransactionManager<>(ktm, dstm);
//  }
//  @Bean
//  public DataSourceTransactionManager dstm(DataSource dataSource) {
//    return new DataSourceTransactionManager(dataSource);
//  }

  @Bean
  public ChainedKafkaTransactionManager<Object, Object> consumerChainedKafkaTransactionManager(
      KafkaTransactionManager<?, ?> transactionManager) {
    return new ChainedKafkaTransactionManager<>(transactionManager);
  }

  @Bean
  public Map<String, Object> transactionConsumerConfig() {
    Map<String, Object> configurations = new HashMap<>(kafkaProperties.buildConsumerProperties());
    configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, TxDataKeyDeserializer.class.getCanonicalName());
    configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TxDataJsonDeserializer.class.getCanonicalName());
    configurations.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
    return configurations;
  }

  @Bean
  public ConsumerFactory<Object, Object> transactionConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(transactionConsumerConfig());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<?, ?> transactionKafkaListenerContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
      ConsumerFactory<Object, Object> transactionConsumerFactory,
      ChainedKafkaTransactionManager<Object, Object> consumerChainedKafkaTransactionManager,
      AfterRollbackProcessor<Object, Object> afterRollbackProcessor) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    configurer.configure(factory, transactionConsumerFactory);
    factory.getContainerProperties().setTransactionManager(consumerChainedKafkaTransactionManager);
    factory.setAfterRollbackProcessor(afterRollbackProcessor);

    final RetryTemplate retryTemplate = new RetryTemplate();
//    retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2 /* maxAttempts; overwritten with rollback processor */));
    final FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(60000L /* millis; period between retries */);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    factory.setRetryTemplate(retryTemplate);
    return factory;
  }

  @Bean
  public AfterRollbackProcessor<Object, Object> afterRollbackProcessor() {
    return new DefaultAfterRollbackProcessor<>(
        (consumerRecord, e) -> {
          log.error("Error consuming = {} with error {}", consumerRecord, e.getMessage(), e);
          // TODO: can be send to dead letter queue
        }, 3 //maxFailures before process message
    );
  }
}
