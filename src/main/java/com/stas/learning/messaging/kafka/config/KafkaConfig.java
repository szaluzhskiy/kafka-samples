package com.stas.learning.messaging.kafka.config;

import com.stas.learning.messaging.kafka.domain.DataChild;
import com.stas.learning.messaging.kafka.domain.DataKey;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

/*    @Bean
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    public ConsumerFactory<DataKey, DataChild> consumerFactory() {
      return new DefaultKafkaConsumerFactory<DataKey, DataChild>(consumerConfigs());
    }*/

    @Bean
    public Map<String, Object> consumerConfigs() {
      Map<String, Object> props = new HashMap<>();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9093");
      return props;
    }

  /*@Bean
  public ConcurrentKafkaListenerContainerFactory<DataKey, DataChild> kafkaConsumerFactory(ConsumerFactory consumerFactory)
  {
    ConcurrentKafkaListenerContainerFactory<DataKey, DataChild> factory = new ConcurrentKafkaListenerContainerFactory<DataKey, DataChild>();

    factory.setConsumerFactory(consumerFactory);
    return factory;
  }*/
}
