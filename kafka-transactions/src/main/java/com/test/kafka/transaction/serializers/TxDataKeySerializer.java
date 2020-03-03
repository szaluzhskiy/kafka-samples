package com.test.kafka.transaction.serializers;

import com.test.kafka.transaction.domain.TxDataKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import org.apache.kafka.common.serialization.Serializer;

public class TxDataKeySerializer implements Serializer<TxDataKey> {

  public void configure(Map<String, ?> map, boolean b) {
  }

  public byte[] serialize(String s, TxDataKey dataKey) {
    try {
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
      objectStream.writeObject(dataKey);
      objectStream.flush();
      objectStream.close();
      return byteStream.toByteArray();
    }
    catch (IOException e) {
      throw new IllegalStateException("Can't serialize object: " + dataKey, e);
    }
  }

  public void close() {

  }
}
