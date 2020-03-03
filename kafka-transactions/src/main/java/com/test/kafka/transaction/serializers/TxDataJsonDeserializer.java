package com.test.kafka.transaction.serializers;

import com.test.kafka.transaction.domain.TxDataParent;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class TxDataJsonDeserializer extends JsonDeserializer<TxDataParent> {

}
