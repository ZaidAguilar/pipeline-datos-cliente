package com.zaid.examen.service;

import com.zaid.examen.dto.CustomerDto;
import com.zaid.examen.dto.CustomerMessageDto;
import com.zaid.examen.util.kafka.producer.CustomerProducer;
import com.zaid.examen.util.mongo.model.CustomerDocument;
import com.zaid.examen.util.mongo.repository.CustomerRepository;
import com.zaid.examen.util.snowflake.SnowflakeClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private SnowflakeClient snowflakeClient;
    @Mock private CustomerProducer producer;
    @Mock private CustomerRepository repository;

    @InjectMocks private CustomerService service;

    @Test
    void getCustomers_debeDelegarASnowflakeClient() throws Exception {
        List<CustomerDto> expected = List.of(new CustomerDto());
        when(snowflakeClient.findCustomers(0, 10)).thenReturn(expected);

        List<CustomerDto> result = service.getCustomers(0, 10);

        assertThat(result).isSameAs(expected);
        verify(snowflakeClient).findCustomers(0, 10);
        verifyNoMoreInteractions(snowflakeClient, producer, repository);
    }

    @Test
    void fetchAndSendToKafka_siNoExisteEnSnowflake_regresaFalse_yNoEnvia() throws Exception {
        when(snowflakeClient.findCustomerMessageById(123L)).thenReturn(null);

        boolean sent = service.fetchAndSendToKafka(123L);

        assertThat(sent).isFalse();
        verify(snowflakeClient).findCustomerMessageById(123L);
        verifyNoInteractions(producer);
    }

    @Test
    void fetchAndSendToKafka_siExiste_enviaAKafka_yRegresaTrue() throws Exception {
        CustomerMessageDto msg = new CustomerMessageDto();
        msg.setCustomerId(123L);
        when(snowflakeClient.findCustomerMessageById(123L)).thenReturn(msg);

        boolean sent = service.fetchAndSendToKafka(123L);

        assertThat(sent).isTrue();
        verify(snowflakeClient).findCustomerMessageById(123L);
        verify(producer).send(msg);
    }

    @Test
    void getMongoCustomers_debeDelegarAlRepository() {
        List<CustomerDocument> expected = List.of(new CustomerDocument());
        when(repository.findAll()).thenReturn(expected);

        List<CustomerDocument> result = service.getMongoCustomers();

        assertThat(result).isSameAs(expected);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }
}
