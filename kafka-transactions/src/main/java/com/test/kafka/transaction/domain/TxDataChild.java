package com.test.kafka.transaction.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonTypeName("child")
public class TxDataChild extends TxDataParent implements Serializable {

  @JsonProperty
  public String childValue;

  public TxDataChild() {
    super();
  }

  public TxDataChild(String parentValue, String childValue) {
    super(parentValue);
    this.childValue = childValue;
  }
}
