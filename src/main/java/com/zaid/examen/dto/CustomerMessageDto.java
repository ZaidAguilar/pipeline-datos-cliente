package com.zaid.examen.dto;

import lombok.Data;

@Data
public class CustomerMessageDto {
    private long customerId;
    private String firstName;
    private String lastName;
    private Integer birthDay;
    private Integer birthMonth;
    private Integer birthYear;
    private String email;
    private AddressDto address;
}
