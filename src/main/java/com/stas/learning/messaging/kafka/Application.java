package com.stas.learning.messaging.kafka;

import com.stas.learning.messaging.kafka.services.ProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
