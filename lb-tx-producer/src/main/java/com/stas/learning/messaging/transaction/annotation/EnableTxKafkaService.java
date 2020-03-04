package com.stas.learning.messaging.transaction.annotation;

import com.stas.learning.messaging.transaction.config.TxKafkaConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({TxKafkaConfig.class})
public @interface EnableTxKafkaService {

}
