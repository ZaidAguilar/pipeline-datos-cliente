package com.zaid.examen.util.mongo.model;

import com.zaid.examen.dto.AddressDto;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "customer_data")
public class CustomerDocument {

    private String id;
    private long customerId;
    private String firstName;
    private String lastName;
    private Integer birthDay;
    private Integer birthMonth;
    private Integer birthYear;
    private String email;
    private AddressDto address;
    private Instant receivedAt;
}
