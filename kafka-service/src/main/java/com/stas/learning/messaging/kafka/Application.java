package com.stas.learning.messaging.kafka;

import com.stas.learning.messaging.transaction.annotation.EnableTxKafkaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableTxKafkaService
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
