package com.test.kafka.transaction.serializers;

import com.test.kafka.transaction.domain.TxDataKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;

public class TxDataKeyDeserializer implements Deserializer<TxDataKey> {

  public void configure(Map<String, ?> map, boolean b) {

  }

  public TxDataKey deserialize(String s, byte[] bytes) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInput in = null;

      in = new ObjectInputStream(bis);

      return (TxDataKey) in.readObject();
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
