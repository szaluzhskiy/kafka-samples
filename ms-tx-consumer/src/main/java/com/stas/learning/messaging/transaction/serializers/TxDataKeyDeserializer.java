package com.stas.learning.messaging.transaction.serializers;

import com.stas.learning.messaging.transaction.domain.TxDataKey;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class TxDataKeyDeserializer implements Deserializer<TxDataKey> {

  public void configure(Map<String, ?> map, boolean b) {
  }

  public TxDataKey deserialize(String s, byte[] bytes) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInput in = new ObjectInputStream(bis);
      return (TxDataKey) in.readObject();
    } catch (Exception e) {
      log.error("Can't deserialize message key", e);
    }
    return null;
  }

  public void close() {
  }
}
