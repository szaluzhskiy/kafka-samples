package com.stas.learning.messaging.transaction.config;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Profile(TxKafkaConfig.NOT_DISABLE_TRANSACTION_PROFILE)
@Configuration
//@AutoConfigureAfter(KafkaAutoConfiguration.class)
//@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class CustomKafkaAutoConfiguration {

  private final KafkaProperties properties;

  private final RecordMessageConverter messageConverter;

  public CustomKafkaAutoConfiguration(KafkaProperties properties,
      ObjectProvider<RecordMessageConverter> messageConverter) {
    this.properties = properties;
    this.messageConverter = messageConverter.getIfUnique();
  }

  static class ConditionalOnKafkaTemplate extends AllNestedConditions {

    public ConditionalOnKafkaTemplate() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnBean(name = TxKafkaConfig.TRANSACTIONAL_KAFKA_TEMPLATE_BEAN_NAME)
    static class onTxKafkaTemplateBean {

    }

    @ConditionalOnSingleCandidate(KafkaTemplate.class)
    static class onOnlySingleKafkaTemplate {

    }

  }

  @Bean
  @Conditional(ConditionalOnKafkaTemplate.class)
  public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory,
      ProducerListener<Object, Object> kafkaProducerListener) {
    KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
    if (this.messageConverter != null) {
      kafkaTemplate.setMessageConverter(this.messageConverter);
    }
    kafkaTemplate.setProducerListener(kafkaProducerListener);
    kafkaTemplate.setDefaultTopic(this.properties.getTemplate().getDefaultTopic());
    return kafkaTemplate;
  }

  static class ConditionalOnProducerFactory extends AllNestedConditions {

    public ConditionalOnProducerFactory() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnBean(name = TxKafkaConfig.TRANSACTIONAL_PRODUCER_FACTORY_BEAN_NAME)
    static class onTxProducerFactoryBean {

    }

    @ConditionalOnSingleCandidate(ProducerFactory.class)
    static class onOnlySingleKafkaTemplate {

    }

  }

  @Bean
  @ConditionalOnMissingBean(ConditionalOnProducerFactory.class)
  public ProducerFactory<?, ?> kafkaProducerFactory() {
    DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(
        this.properties.buildProducerProperties());
    String transactionIdPrefix = this.properties.getProducer().getTransactionIdPrefix();
    if (transactionIdPrefix != null) {
      factory.setTransactionIdPrefix(transactionIdPrefix);
    }
    return factory;
  }

}
