package com.zaid.examen.util.snowflake;

import com.zaid.examen.dto.CustomerMessageDto;
import com.zaid.examen.dto.AddressDto;
import com.zaid.examen.dto.CustomerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SnowflakeClient {

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

    private String jdbcUrl() {
        return "jdbc:snowflake://" + url +
                "/?warehouse=" + warehouse +
                "&db=" + database +
                "&schema=" + schema +
                "&role=" + role;
    }

    public List<CustomerDto> findCustomers(int page, int size) throws Exception {
        int offset = page * size;

        String sql = """
            SELECT C_CUSTOMER_SK, C_FIRST_NAME, C_LAST_NAME, C_EMAIL_ADDRESS
            FROM CUSTOMER
            ORDER BY C_CUSTOMER_SK
            LIMIT %d OFFSET %d
        """.formatted(size, offset);

        List<CustomerDto> customers = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(jdbcUrl(), user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CustomerDto c = new CustomerDto();
                c.setId(rs.getLong("C_CUSTOMER_SK"));
                c.setFirstName(rs.getString("C_FIRST_NAME"));
                c.setLastName(rs.getString("C_LAST_NAME"));
                c.setEmail(rs.getString("C_EMAIL_ADDRESS"));
                customers.add(c);
            }
        }
        return customers;
    }

    public CustomerMessageDto findCustomerMessageById(long id) throws Exception {
        String sql = """
            SELECT
                C.C_CUSTOMER_SK,
                C.C_FIRST_NAME,
                C.C_LAST_NAME,
                C.C_BIRTH_DAY,
                C.C_BIRTH_MONTH,
                C.C_BIRTH_YEAR,
                C.C_EMAIL_ADDRESS,
                A.CA_STREET_NAME,
                A.CA_CITY,
                A.CA_STATE,
                A.CA_COUNTRY,
                A.CA_STREET_NUMBER
            FROM CUSTOMER C
            LEFT JOIN CUSTOMER_ADDRESS A
                ON C.C_CURRENT_ADDR_SK = A.CA_ADDRESS_SK
            WHERE C.C_CUSTOMER_SK = %d
        """.formatted(id);

        try (Connection conn = DriverManager.getConnection(jdbcUrl(), user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.next()) return null;

            CustomerMessageDto msg = new CustomerMessageDto();
            msg.setCustomerId(rs.getLong("C_CUSTOMER_SK"));
            msg.setFirstName(rs.getString("C_FIRST_NAME"));
            msg.setLastName(rs.getString("C_LAST_NAME"));
            msg.setBirthDay(getNullableInt(rs, "C_BIRTH_DAY"));
            msg.setBirthMonth(getNullableInt(rs, "C_BIRTH_MONTH"));
            msg.setBirthYear(getNullableInt(rs, "C_BIRTH_YEAR"));
            msg.setEmail(rs.getString("C_EMAIL_ADDRESS"));

            AddressDto a = new AddressDto();
            a.setStreet(rs.getString("CA_STREET_NAME"));
            a.setCity(rs.getString("CA_CITY"));
            a.setState(rs.getString("CA_STATE"));
            a.setCountry(rs.getString("CA_COUNTRY"));
            a.setStreet_number(rs.getString("CA_STREET_NUMBER"));
            msg.setAddress(a);

            return msg;
        }
    }

    private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }
}
