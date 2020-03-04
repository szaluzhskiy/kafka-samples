package com.stas.learning.messaging.kafka.serializers;

import com.stas.learning.messaging.kafka.domain.DataChild;
import com.stas.learning.messaging.kafka.domain.DataParent;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class DataJsonSerializer extends JsonSerializer<DataParent> {

}
