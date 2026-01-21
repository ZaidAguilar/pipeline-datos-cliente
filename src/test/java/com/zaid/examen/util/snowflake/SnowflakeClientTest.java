package com.zaid.examen.util.snowflake;

import com.zaid.examen.dto.CustomerDto;
import com.zaid.examen.dto.CustomerMessageDto;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SnowflakeClientTest {

    @Test
    void findCustomers_debeEjecutarQuery_yMapearResultados() throws Exception {
        SnowflakeClient client = new SnowflakeClient();
        setField(client, "url", "x");
        setField(client, "user", "u");
        setField(client, "password", "p");
        setField(client, "warehouse", "w");
        setField(client, "database", "d");
        setField(client, "schema", "s");
        setField(client, "role", "r");

        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getLong("C_CUSTOMER_SK")).thenReturn(1L);
        when(rs.getString("C_FIRST_NAME")).thenReturn("Ana");
        when(rs.getString("C_LAST_NAME")).thenReturn("Lopez");
        when(rs.getString("C_EMAIL_ADDRESS")).thenReturn("ana@x.com");

        try (MockedStatic<DriverManager> dm = mockStatic(DriverManager.class)) {
            dm.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(conn);

            List<CustomerDto> out = client.findCustomers(0, 10);

            assertThat(out).hasSize(1);
            assertThat(out.get(0).getId()).isEqualTo(1L);
            assertThat(out.get(0).getFirstName()).isEqualTo("Ana");
            verify(stmt).executeQuery(contains("LIMIT 10 OFFSET 0"));
        }
    }

    @Test
    void findCustomerMessageById_siNoHayFila_regresaNull() throws Exception {
        SnowflakeClient client = new SnowflakeClient();
        setField(client, "url", "x");
        setField(client, "user", "u");
        setField(client, "password", "p");
        setField(client, "warehouse", "w");
        setField(client, "database", "d");
        setField(client, "schema", "s");
        setField(client, "role", "r");

        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DriverManager> dm = mockStatic(DriverManager.class)) {
            dm.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(conn);

            CustomerMessageDto out = client.findCustomerMessageById(999L);

            assertThat(out).isNull();
        }
    }

    @Test
    void findCustomerMessageById_siHayFila_mapeaCampos_yManejaNullables() throws Exception {
        SnowflakeClient client = new SnowflakeClient();
        setField(client, "url", "x");
        setField(client, "user", "u");
        setField(client, "password", "p");
        setField(client, "warehouse", "w");
        setField(client, "database", "d");
        setField(client, "schema", "s");
        setField(client, "role", "r");

        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        when(rs.next()).thenReturn(true);

        when(rs.getLong("C_CUSTOMER_SK")).thenReturn(10L);
        when(rs.getString("C_FIRST_NAME")).thenReturn("Ana");
        when(rs.getString("C_LAST_NAME")).thenReturn("Lopez");
        when(rs.getString("C_EMAIL_ADDRESS")).thenReturn("ana@x.com");

        when(rs.getInt("C_BIRTH_DAY")).thenReturn(0);
        when(rs.wasNull()).thenReturn(true);

        when(rs.wasNull()).thenReturn(true, false, false);
        when(rs.getInt("C_BIRTH_MONTH")).thenReturn(2);
        when(rs.getInt("C_BIRTH_YEAR")).thenReturn(1999);

        when(rs.getString("CA_STREET_NAME")).thenReturn("Main");
        when(rs.getString("CA_CITY")).thenReturn("GDL");
        when(rs.getString("CA_STATE")).thenReturn("JAL");
        when(rs.getString("CA_COUNTRY")).thenReturn("MX");

        try (MockedStatic<DriverManager> dm = mockStatic(DriverManager.class)) {
            dm.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(conn);

            CustomerMessageDto out = client.findCustomerMessageById(10L);

            assertThat(out).isNotNull();
            assertThat(out.getCustomerId()).isEqualTo(10L);
            assertThat(out.getBirthDay()).isNull();
            assertThat(out.getBirthMonth()).isEqualTo(2);
            assertThat(out.getBirthYear()).isEqualTo(1999);
            assertThat(out.getAddress()).isNotNull();
            assertThat(out.getAddress().getCity()).isEqualTo("GDL");

            verify(stmt).executeQuery(contains("WHERE C.C_CUSTOMER_SK = 10"));
        }
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}
