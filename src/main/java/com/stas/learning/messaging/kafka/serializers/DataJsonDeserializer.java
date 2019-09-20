package com.stas.learning.messaging.kafka.serializers;

import com.stas.learning.messaging.kafka.domain.DataParent;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class DataJsonDeserializer extends JsonDeserializer<DataParent> {

}
