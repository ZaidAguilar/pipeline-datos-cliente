package com.zaid.examen.controller;

import com.zaid.examen.dto.CustomerDto;
import com.zaid.examen.dto.ApiResponseDto;
import com.zaid.examen.dto.factory.ApiResponses;
import com.zaid.examen.enums.Catalogo;
import com.zaid.examen.service.CustomerService;
import com.zaid.examen.util.mongo.model.CustomerDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    /** 1) GET /api/customers?page=0&size=10 */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CustomerDto>>> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {

        if (page < 0 || size <= 0 || size > 200) {
            return ApiResponses.badRequest("EXAMEN.CUSTOMERS.LIST", -1,
                    Catalogo.mensaje400.msg(), null);
        }

        List<CustomerDto> data = service.getCustomers(page, size);

        return ApiResponses.ok("EXAMEN.CUSTOMERS.LIST", 1,
                Catalogo.mensaje200.msg(), data);
    }

    /** 2) GET /api/customers/fetch/{id} */
    @GetMapping("/fetch/{id}")
    public ResponseEntity<ApiResponseDto<Void>> fetchCustomer(@PathVariable long id) throws Exception {

        boolean sent = service.fetchAndSendToKafka(id);

        if (!sent) {
            return ApiResponses.notFound("EXAMEN.CUSTOMERS.FETCH", -1,
                    Catalogo.mensaje404.msg(), null);
        }

        return ApiResponses.ok("EXAMEN.CUSTOMERS.FETCH", 1,
                Catalogo.MSG_CUSTOMER_SENT.msg(), null);
    }

    /** 3) GET /api/customers/mongo */
    @GetMapping("/mongo")
    public ResponseEntity<ApiResponseDto<List<CustomerDocument>>> getMongoCustomers() {
        List<CustomerDocument> data = service.getMongoCustomers();

        return ApiResponses.ok("EXAMEN.CUSTOMERS.MONGO", 1,
                Catalogo.mensaje200.msg(), data);
    }
}
