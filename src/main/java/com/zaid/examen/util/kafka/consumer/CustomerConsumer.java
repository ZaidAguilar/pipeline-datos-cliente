package com.zaid.examen.util.kafka.consumer;

import com.zaid.examen.dto.CustomerMessageDto;
import com.zaid.examen.util.mongo.model.CustomerDocument;
import com.zaid.examen.util.mongo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerConsumer {

    private final CustomerRepository repository;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CustomerMessageDto msg) {
        try {
            log.info("Kafka consumed customerId={}", msg.getCustomerId());

            CustomerDocument doc = new CustomerDocument();
            doc.setCustomerId(msg.getCustomerId());
            doc.setFirstName(msg.getFirstName());
            doc.setLastName(msg.getLastName());
            doc.setBirthDay(msg.getBirthDay());
            doc.setBirthMonth(msg.getBirthMonth());
            doc.setBirthYear(msg.getBirthYear());
            doc.setEmail(msg.getEmail());
            doc.setAddress(msg.getAddress());
            doc.setReceivedAt(Instant.now());

            repository.save(doc);
            log.info("Saved to MongoDB customer_data customerId={}", msg.getCustomerId());
        }catch (Exception ex) {
            log.error("Failed saving customerId={} to Mongo", msg.getCustomerId(), ex);
            throw ex;
        }


    }
}
