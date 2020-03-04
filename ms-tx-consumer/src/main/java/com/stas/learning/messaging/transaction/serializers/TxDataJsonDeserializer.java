package com.stas.learning.messaging.transaction.serializers;

import com.stas.learning.messaging.transaction.domain.TxDataParent;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class TxDataJsonDeserializer extends JsonDeserializer<TxDataParent> {

}
