package com.stas.learning.messaging.kafka.services;

import com.stas.learning.messaging.kafka.domain.DataChild;
import com.stas.learning.messaging.kafka.domain.DataKey;
import com.stas.learning.messaging.kafka.serializers.DataJsonDeserializer;
import com.stas.learning.messaging.kafka.serializers.DataKeyDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

@Service
public class ConsumerSeekService {

  public void read(long offset, TopicPartition partition) {
   /* Consumer<DataKey, DataChild> consumer = consumerFactory.createConsumer("group-1");
    consumer.seek(partition, offset);
    ConsumerRecords<DataKey, DataChild> res =  consumer.poll(Duration.ofMinutes(1));
    while (res.records(partition.topic()).iterator().hasNext()) {
      ConsumerRecord<DataKey, DataChild> rec = res.records(partition.topic()).iterator().next();
      System.out.println("Read key = " + rec.key() + " value = " + rec.value());
    }*/
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, DataKeyDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DataJsonDeserializer.class.getName());
    try (Consumer<DataKey, DataChild> consumer = new KafkaConsumer<>(props)) {

      consumer.subscribe(Collections.singletonList("transport.topic"), new ConsumerRebalanceListener() {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

        }


        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
          System.out.println("Assigned " + partitions);
          for (TopicPartition tp : partitions) {
            OffsetAndMetadata oam = consumer.committed(tp);
            if (oam != null) {
              System.out.println("Current offset is " + oam.offset());
            } else {
              System.out.println("No committed offsets");
            }
            if (offset > 0) {
              System.out.println("Seeking to " + offset);
              consumer.seek(tp, offset);
            }
          }
        }
      });
        System.out.println("Calling poll");
        ConsumerRecords<DataKey, DataChild> records = consumer.poll(Duration.ofMillis(100L));
        for (ConsumerRecord<DataKey, DataChild> r : records) {
          if(r.offset() == offset) {
            System.out.println("record from " + r.topic() + "-" + r.partition() + " at offset " + r.offset() + " key = " + r.key() + " value = " + r.value());
          }
        }
    }
  }
}
