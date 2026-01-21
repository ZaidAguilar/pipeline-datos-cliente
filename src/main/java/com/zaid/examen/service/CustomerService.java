package com.zaid.examen.service;

import com.zaid.examen.dto.CustomerMessageDto;
import com.zaid.examen.dto.CustomerDto;
import com.zaid.examen.util.kafka.producer.CustomerProducer;
import com.zaid.examen.util.mongo.model.CustomerDocument;
import com.zaid.examen.util.mongo.repository.CustomerRepository;
import com.zaid.examen.util.snowflake.SnowflakeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final SnowflakeClient snowflakeClient;
    private final CustomerProducer producer;
    private final CustomerRepository repository;

    public List<CustomerDto> getCustomers(int page, int size) throws Exception {
        return snowflakeClient.findCustomers(page, size);
    }

    public boolean fetchAndSendToKafka(long id) throws Exception {
        CustomerMessageDto msg = snowflakeClient.findCustomerMessageById(id);
        if (msg == null) return false;
        producer.send(msg);
        return true;
    }

    public List<CustomerDocument> getMongoCustomers() {
        return repository.findAll();
    }
}
