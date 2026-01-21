package com.zaid.examen.util.kafka.producer;

import com.zaid.examen.dto.CustomerMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerProducer {

    private final KafkaTemplate<String, CustomerMessageDto> kafkaTemplate;

    @Value("${kafka.topic}")
    private String topic;

    public void send(CustomerMessageDto message) {
        String key = String.valueOf(message.getCustomerId());
        log.info("Sending to Kafka topic={} key={}", topic, key);
        kafkaTemplate.send(topic, key, message);
    }
}
