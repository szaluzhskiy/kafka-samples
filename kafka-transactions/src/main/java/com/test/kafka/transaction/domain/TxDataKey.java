package com.test.kafka.transaction.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TxDataKey implements Serializable {
  private String version;
  private String name;
}
