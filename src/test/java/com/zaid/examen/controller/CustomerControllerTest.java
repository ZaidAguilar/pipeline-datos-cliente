package com.zaid.examen.controller;

import com.zaid.examen.dto.CustomerDto;
import com.zaid.examen.service.CustomerService;
import com.zaid.examen.util.mongo.model.CustomerDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private CustomerService service;

    @Test
    void getCustomers_parametrosInvalidos_pageNegativo_debeRegresar400() throws Exception {
        mvc.perform(get("/api/customers")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigo").value("400.EXAMEN.CUSTOMERS.LIST.-1"));
        verifyNoInteractions(service);
    }

    @Test
    void getCustomers_parametrosInvalidos_sizeMayorA200_debeRegresar400() throws Exception {
        mvc.perform(get("/api/customers")
                        .param("page", "0")
                        .param("size", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("400.EXAMEN.CUSTOMERS.LIST.-1"));
        verifyNoInteractions(service);
    }

    @Test
    void getCustomers_ok_debeRegresar200_yLista() throws Exception {
        CustomerDto c = new CustomerDto();
        c.setId(1L);
        c.setFirstName("A");
        c.setLastName("B");
        c.setEmail("a@b.com");

        when(service.getCustomers(0, 10)).thenReturn(List.of(c));

        mvc.perform(get("/api/customers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("200.EXAMEN.CUSTOMERS.LIST.1"))
                .andExpect(jsonPath("$.respuesta[0].id").value(1));

        verify(service).getCustomers(0, 10);
    }

    @Test
    void fetchCustomer_siNoExiste_debeRegresar404() throws Exception {
        when(service.fetchAndSendToKafka(99L)).thenReturn(false);

        mvc.perform(get("/api/customers/fetch/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("404.EXAMEN.CUSTOMERS.FETCH.-1"));

        verify(service).fetchAndSendToKafka(99L);
    }

    @Test
    void fetchCustomer_ok_debeRegresar200() throws Exception {
        when(service.fetchAndSendToKafka(10L)).thenReturn(true);

        mvc.perform(get("/api/customers/fetch/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("200.EXAMEN.CUSTOMERS.FETCH.1"));

        verify(service).fetchAndSendToKafka(10L);
    }

    @Test
    void getMongoCustomers_ok_debeRegresar200_yDatos() throws Exception {
        CustomerDocument doc = new CustomerDocument();
        doc.setCustomerId(1L);

        when(service.getMongoCustomers()).thenReturn(List.of(doc));

        mvc.perform(get("/api/customers/mongo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("200.EXAMEN.CUSTOMERS.MONGO.1"))
                .andExpect(jsonPath("$.respuesta[0].customerId").value(1));

        verify(service).getMongoCustomers();
    }
}
