package com.stas.learning.messaging.transaction.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TxDataKey implements Serializable {
  private String version;
  private String name;
}
