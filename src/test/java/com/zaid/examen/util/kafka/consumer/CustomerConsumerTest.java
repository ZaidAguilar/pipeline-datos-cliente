package com.zaid.examen.util.kafka.consumer;

import com.zaid.examen.dto.AddressDto;
import com.zaid.examen.dto.CustomerMessageDto;
import com.zaid.examen.util.mongo.model.CustomerDocument;
import com.zaid.examen.util.mongo.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerConsumerTest {

    @Mock private CustomerRepository repository;
    @InjectMocks private CustomerConsumer consumer;

    @Captor private ArgumentCaptor<CustomerDocument> docCaptor;

    @Test
    void consume_debeMapearYGuardarEnMongo() {
        CustomerMessageDto msg = new CustomerMessageDto();
        msg.setCustomerId(10L);
        msg.setFirstName("Ana");
        msg.setLastName("Lopez");
        msg.setBirthDay(1);
        msg.setBirthMonth(2);
        msg.setBirthYear(1999);
        msg.setEmail("ana@x.com");

        AddressDto a = new AddressDto();
        a.setStreet("Main");
        a.setCity("GDL");
        a.setState("JAL");
        a.setCountry("MX");
        msg.setAddress(a);

        consumer.consume(msg);

        verify(repository).save(docCaptor.capture());
        CustomerDocument saved = docCaptor.getValue();

        assertThat(saved.getCustomerId()).isEqualTo(10L);
        assertThat(saved.getFirstName()).isEqualTo("Ana");
        assertThat(saved.getLastName()).isEqualTo("Lopez");
        assertThat(saved.getBirthDay()).isEqualTo(1);
        assertThat(saved.getBirthMonth()).isEqualTo(2);
        assertThat(saved.getBirthYear()).isEqualTo(1999);
        assertThat(saved.getEmail()).isEqualTo("ana@x.com");
        assertThat(saved.getAddress()).isNotNull();
        assertThat(saved.getReceivedAt()).isNotNull();
        assertThat(saved.getReceivedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void consume_siRepositoryFalla_debeLanzarExcepcion() {
        CustomerMessageDto msg = new CustomerMessageDto();
        msg.setCustomerId(99L);

        when(repository.save(any())).thenThrow(new RuntimeException("mongo down"));

        assertThatThrownBy(() -> consumer.consume(msg))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("mongo down");

        verify(repository).save(any(CustomerDocument.class));
    }
}
