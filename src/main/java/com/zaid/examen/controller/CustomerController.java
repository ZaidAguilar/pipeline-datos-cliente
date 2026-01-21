package com.zaid.examen.controller;

import com.zaid.examen.dto.CustomerDto;
import com.zaid.examen.dto.ApiResponseDto;
import com.zaid.examen.dto.CustomerListRequestDto;
import com.zaid.examen.dto.MensajeDto;
import com.zaid.examen.dto.factory.ApiResponses;
import com.zaid.examen.enums.Catalogo;
import com.zaid.examen.service.CustomerService;
import com.zaid.examen.util.mongo.model.CustomerDocument;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    //inclui esta opcion por que en el examen viene que el endpoint debe ser get, y get no recibe
    //body por definicion, pero igual inclui la version post con los parametros en json, ya que
    //es como normalmente lo manejo yo
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CustomerDto>>> getCustomers(
            @RequestParam() int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        log.info("Incoming request: /api/customers");
        if (page < 0 || size <= 0 || size > 200) {
            log.error("The page/size number should be between 1 and 200, and not a negative number.");
            return ApiResponses.badRequest("EXAMEN.CUSTOMERS.LIST", -1,
                    Catalogo.mensaje400.msg(), null);
        }

        List<CustomerDto> data = service.getCustomers(page, size);
        log.info("Request processed correctly");
        return ApiResponses.ok("EXAMEN.CUSTOMERS.LIST", 1,
                Catalogo.mensaje200.msg(), data);
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<List<CustomerDto>>> getCustomers(
            @Valid @RequestBody CustomerListRequestDto request
    ) throws Exception {
        log.info("Incoming request: /api/customers");
        if (request.getPage() < 0 || request.getPageSize() <= 0 || request.getPageSize() > 200) {
            log.error("The page/size number should be between 1 and 200, and not a negative number.");
            return ApiResponses.badRequest("EXAMEN.CUSTOMERS.LIST", -1,
                    Catalogo.mensaje400.msg(), null);
        }

        List<CustomerDto> data = service.getCustomers(request.getPage(), request.getPageSize());
        log.info("Request processed correctly");
        return ApiResponses.ok("EXAMEN.CUSTOMERS.LIST", 1,
                Catalogo.mensaje200.msg(), data);
    }

    //Lo mismo que en el endpoint anterior, añadi la version post con el json correspondiente
    @GetMapping("/fetch/{id}")
    public ResponseEntity<ApiResponseDto<Void>> fetchCustomer(@PathVariable long id) throws Exception {
        log.info("Incoming request: /api/customers/fetch");
        boolean sent = service.fetchAndSendToKafka(id);

        if (!sent) {
            log.error("The id does not exist");
            return ApiResponses.notFound("EXAMEN.CUSTOMERS.FETCH", -1,
                    Catalogo.mensaje404.msg(), null);
        }
        log.info("Request processed correctly");
        return ApiResponses.ok("EXAMEN.CUSTOMERS.FETCH", 1,
                Catalogo.MSG_CUSTOMER_SENT.msg(), null);
    }

    @PostMapping("/fetch")
    public ResponseEntity<ApiResponseDto<Void>> fetchCustomer(@Valid @RequestBody CustomerDto customerDto) throws Exception {
        log.info("Incoming request: /api/customers/fetch");
        if (customerDto.getId()==0) {
            log.error("The id is not present in the request");
            return ApiResponses.badRequest("EXAMEN.CUSTOMERS.FETCH", -1,
                    Catalogo.mensaje400.msg(), null);
        }
        boolean sent = service.fetchAndSendToKafka(customerDto.getId());

        if (!sent) {
            log.error("The id does not exist");
            return ApiResponses.notFound("EXAMEN.CUSTOMERS.FETCH", -1,
                    Catalogo.mensaje404.msg(), null);
        }
        log.info("Request processed correctly");
        return ApiResponses.ok("EXAMEN.CUSTOMERS.FETCH", 1,
                Catalogo.MSG_CUSTOMER_SENT.msg(), null);
    }

    @GetMapping("/mongo")
    public ResponseEntity<ApiResponseDto<List<CustomerDocument>>> getMongoCustomers() {
        log.info("Incoming request: /api/customers/mongo");
        List<CustomerDocument> data = service.getMongoCustomers();
        log.info("Request processed correctly");
        return ApiResponses.ok("EXAMEN.CUSTOMERS.MONGO", 1,
                Catalogo.mensaje200.msg(), data);
    }
}
