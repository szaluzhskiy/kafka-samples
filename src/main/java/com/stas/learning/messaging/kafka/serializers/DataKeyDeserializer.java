package com.stas.learning.messaging.kafka.serializers;

import com.stas.learning.messaging.kafka.domain.DataKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;

public class DataKeyDeserializer implements Deserializer<DataKey> {

  public void configure(Map<String, ?> map, boolean b) {

  }

  public DataKey deserialize(String s, byte[] bytes) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInput in = null;

      in = new ObjectInputStream(bis);

      return (DataKey) in.readObject();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void close() {

  }
}
