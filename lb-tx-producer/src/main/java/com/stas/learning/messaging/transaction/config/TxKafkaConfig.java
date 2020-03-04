package com.stas.learning.messaging.transaction.config;

import com.stas.learning.messaging.transaction.domain.TxDataChild;
import com.stas.learning.messaging.transaction.domain.TxDataKey;
import com.stas.learning.messaging.transaction.serializers.TxDataJsonSerializer;
import com.stas.learning.messaging.transaction.serializers.TxDataKeySerializer;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile(TxKafkaConfig.NOT_DISABLE_TRANSACTION_PROFILE)
@Slf4j
@Configuration
@EnableKafka
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.stas.learning.kafka.transaction"})
public class TxKafkaConfig {

  public static final String NOT_DISABLE_TRANSACTION_PROFILE = "!disableKafkaTransaction";

  @Autowired
  private KafkaProperties kafkaProperties; // default configuration from application.yml

  @Value("${spring.application.name}")
  private String applicationName;

  @Bean
  public Map<String, Object> transactionProducerConfig() {
    // use all properties from service application.yml and overwrite only needed
    Map<String, Object> configurations = new HashMap<>(kafkaProperties.buildProducerProperties());
    configurations.put(ProducerConfig.ACKS_CONFIG, "all");
    configurations.put(ProducerConfig.RETRIES_CONFIG, 10); // or can be kept default value Integer.MAX_VALUE
    configurations.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // ordered messages in retries
    configurations.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    configurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, TxDataKeySerializer.class.getCanonicalName());
    configurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TxDataJsonSerializer.class.getCanonicalName());
    return configurations;
  }

  @Bean
  public ProducerFactory<TxDataKey, TxDataChild> producerFactory() {
    DefaultKafkaProducerFactory<TxDataKey, TxDataChild> factory =
        new DefaultKafkaProducerFactory<>(transactionProducerConfig());
    factory.setTransactionIdPrefix(applicationName + "-tx-id-");
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
}
