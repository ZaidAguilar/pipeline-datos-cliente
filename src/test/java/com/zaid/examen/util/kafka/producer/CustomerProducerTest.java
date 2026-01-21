package com.zaid.examen.util.kafka.producer;

import com.zaid.examen.dto.CustomerMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerProducerTest {

    @Mock
    private KafkaTemplate<String, CustomerMessageDto> kafkaTemplate;

    @InjectMocks
    private CustomerProducer producer;

    @BeforeEach
    void setup() {
        TestUtils.setField(producer, "topic", "customer-topic");
    }

    @Test
    void send_debeEnviarMensajeAlTopicoConKeyCustomerId() {
        CustomerMessageDto msg = new CustomerMessageDto();
        msg.setCustomerId(123L);
        msg.setFirstName("Jane");

        producer.send(msg);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CustomerMessageDto> msgCaptor =
                ArgumentCaptor.forClass(CustomerMessageDto.class);

        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                msgCaptor.capture()
        );

        assertThat(topicCaptor.getValue()).isEqualTo("customer-topic");
        assertThat(keyCaptor.getValue()).isEqualTo("123");
        assertThat(msgCaptor.getValue()).isSameAs(msg);
    }

    @Test
    void send_conCustomerIdCero_debeUsarKeyCero() {
        CustomerMessageDto msg = new CustomerMessageDto();
        msg.setCustomerId(0L);

        producer.send(msg);

        verify(kafkaTemplate).send("customer-topic", "0", msg);
    }
}