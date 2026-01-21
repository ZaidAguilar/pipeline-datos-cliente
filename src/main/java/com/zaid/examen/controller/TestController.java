package com.zaid.examen.controller;

import com.zaid.examen.dto.ApiResponseDto;
import com.zaid.examen.dto.MensajeDto;
import com.zaid.examen.dto.factory.ApiResponses;
import com.zaid.examen.dto.AddressDto;
import com.zaid.examen.dto.CustomerDto;
import com.zaid.examen.enums.Catalogo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@Slf4j
@RestController
public class TestController {

    private static final String CODE_BASE = "EXAMEN.SNOWFLAKE.HEALTH";

    @Value("${snowflake.url}")
    private String url;

    @Value("${snowflake.user}")
    private String user;

    @Value("${snowflake.password}")
    private String password;

    @Value("${snowflake.warehouse}")
    private String warehouse;

    @Value("${snowflake.database}")
    private String database;

    @Value("${snowflake.schema}")
    private String schema;

    @Value("${snowflake.role}")
    private String role;

    @GetMapping("/health/snowflake")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> testSnowflake() {
        log.info("Incoming request: /health/snowflake");

        String jdbcUrl = "jdbc:snowflake://" + url +
                "/?warehouse=" + warehouse +
                "&db=" + database +
                "&schema=" + schema +
                "&role=" + role;

        Map<String, Object> response = new HashMap<>();

        try {

            try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM CUSTOMER")) {

                rs.next();
                response.put("customerCount", rs.getLong(1));
            }

            List<CustomerDto> customers = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("""
                         SELECT C_CUSTOMER_SK, C_FIRST_NAME, C_LAST_NAME, C_EMAIL_ADDRESS
                         FROM CUSTOMER
                         LIMIT 10
                         """)) {

                while (rs.next()) {
                    CustomerDto c = new CustomerDto();
                    c.setId(rs.getLong("C_CUSTOMER_SK"));
                    c.setFirstName(rs.getString("C_FIRST_NAME"));
                    c.setLastName(rs.getString("C_LAST_NAME"));
                    c.setEmail(rs.getString("C_EMAIL_ADDRESS"));
                    customers.add(c);
                }
            }
            response.put("customers", customers);

            List<AddressDto> addresses = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("""
                         SELECT CA_STREET_NAME, CA_CITY, CA_STATE, CA_COUNTRY
                         FROM CUSTOMER_ADDRESS
                         LIMIT 10
                         """)) {

                while (rs.next()) {
                    AddressDto a = new AddressDto();
                    a.setStreet(rs.getString("CA_STREET_NAME"));
                    a.setCity(rs.getString("CA_CITY"));
                    a.setState(rs.getString("CA_STATE"));
                    a.setCountry(rs.getString("CA_COUNTRY"));
                    addresses.add(a);
                }
            }
            response.put("addresses", addresses);

            log.info("Snowflake health OK. customerCount={}", response.get("customerCount"));
            return ApiResponses.ok(CODE_BASE, 1, Catalogo.mensaje200.msg(), response);

        } catch (IllegalArgumentException e) {
            log.warn("Bad request in snowflake health: {}", e.getMessage());
            MensajeDto mensajeDto = new MensajeDto();
            mensajeDto.setMensaje(e.getMessage());
            response.put("resultado",mensajeDto);
            return ApiResponses.badRequest(CODE_BASE, -1, Catalogo.mensaje400.msg(), response);

        } catch (Exception e) {
            log.error("Snowflake health failed", e);
            MensajeDto mensajeDto = new MensajeDto();
            mensajeDto.setMensaje("Snowflake health check failed: "+e.getMessage());
            response.put("resultado",mensajeDto);
            return ApiResponses.serverError(CODE_BASE, -2, Catalogo.mensaje500.msg(), response);
        }
    }
}
