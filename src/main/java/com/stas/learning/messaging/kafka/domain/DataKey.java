package com.stas.learning.messaging.kafka.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataKey implements Serializable {
  private String version;
  private String name;
}
