package com.stas.learning.messaging.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.stas.learning.messaging.kafka", "com.test.kafka.transaction"})
@SpringBootApplication
public class Application {

  /* @Autowired
   private ProduceService service;
 */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  public void run(String... args) throws Exception {
    // service.generateSomeMessages(1);
  }
}
