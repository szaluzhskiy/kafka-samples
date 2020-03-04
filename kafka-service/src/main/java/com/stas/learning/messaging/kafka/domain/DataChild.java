package com.stas.learning.messaging.kafka.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonTypeName("child")
public class DataChild extends DataParent implements Serializable {

  @JsonProperty
  public String childValue;

  public DataChild() {
    super();
  }

  public DataChild(String parentValue, String childValue) {
    super(parentValue);
    this.childValue = childValue;
  }
}
