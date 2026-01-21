package com.zaid.examen.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
public class SnowflakeHealthIndicator implements HealthIndicator {

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

    @Override
    public Health health() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl(), user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {

            rs.next();
            int one = rs.getInt(1);

            return Health.up()
                    .withDetail("snowflake", "reachable")
                    .withDetail("probe", one)
                    .build();

        } catch (Exception e) {
            log.error("Snowflake health check failed", e);
            return Health.down(e)
                    .withDetail("snowflake", "unreachable")
                    .build();
        }
    }
}
