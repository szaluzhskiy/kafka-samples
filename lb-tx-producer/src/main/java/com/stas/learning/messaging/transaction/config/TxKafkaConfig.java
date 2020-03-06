package com.stas.learning.messaging.transaction.config;

import com.stas.learning.messaging.transaction.domain.TxDataChild;
import com.stas.learning.messaging.transaction.domain.TxDataKey;
import com.stas.learning.messaging.transaction.serializers.TxDataJsonSerializer;
import com.stas.learning.messaging.transaction.serializers.TxDataKeySerializer;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile(TxKafkaConfig.NOT_DISABLE_TRANSACTION_PROFILE)
@Slf4j
@Configuration
@EnableKafka
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.stas.learning.messaging.transaction"})
public class TxKafkaConfig {

  public static final String NOT_DISABLE_TRANSACTION_PROFILE = "!disableKafkaTransaction";
  public static final String TRANSACTIONAL_KAFKA_TEMPLATE_BEAN_NAME = "transactionKafkaTemplate";
  public static final String TRANSACTIONAL_PRODUCER_FACTORY_BEAN_NAME = "transactionProducerFactory";

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
    configurations.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 10000L); // interval between retries
    configurations.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000L);
    configurations.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    configurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, TxDataKeySerializer.class.getCanonicalName());
    configurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TxDataJsonSerializer.class.getCanonicalName());
    return configurations;
  }

  @Bean
  @Qualifier(TRANSACTIONAL_PRODUCER_FACTORY_BEAN_NAME)
  public ProducerFactory<TxDataKey, TxDataChild> transactionProducerFactory() {
    DefaultKafkaProducerFactory<TxDataKey, TxDataChild> factory =
        new DefaultKafkaProducerFactory<>(transactionProducerConfig());
    factory.setProducerPerConsumerPartition(true);
    factory.setTransactionIdPrefix(String.format("%s-%s-%s-", applicationName, getIpAddress(), "tx"));
    return factory;
  }

  @Bean
  public KafkaTransactionManager<TxDataKey, TxDataChild> txKafkaTransactionManager(
      ProducerFactory<TxDataKey, TxDataChild> producerFactory) {
    return new KafkaTransactionManager<>(producerFactory);
  }

  @Bean
  @Qualifier(TRANSACTIONAL_KAFKA_TEMPLATE_BEAN_NAME)
  public KafkaTemplate<TxDataKey, TxDataChild> transactionKafkaTemplate(
      ProducerFactory<TxDataKey, TxDataChild> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  private static final String LOCALHOST = "127.0.0.1";

  private static String getIpAddress() {
    try {
      Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
      while (e.hasMoreElements()) {
        NetworkInterface n = e.nextElement();
        Enumeration<InetAddress> ee = n.getInetAddresses();
        while (ee.hasMoreElements()) {
          InetAddress i = ee.nextElement();
          if (!i.isLoopbackAddress() && !(i instanceof Inet6Address)) {
            return i.getHostAddress();
          }
        }
      }
    } catch (SocketException e) {
      log.error("Не удалось получить IP-адрес", e);
    }
    return LOCALHOST;
  }

  @Bean
  public RetryTemplate txKafkaProducerRetryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2));
    retryTemplate.setThrowLastExceptionOnExhausted(true);

    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(10000L);
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
    return retryTemplate;
  }
}
