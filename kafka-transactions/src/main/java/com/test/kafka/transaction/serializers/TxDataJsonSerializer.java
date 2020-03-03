package com.test.kafka.transaction.serializers;

import com.test.kafka.transaction.domain.TxDataParent;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class TxDataJsonSerializer extends JsonSerializer<TxDataParent> {

}
