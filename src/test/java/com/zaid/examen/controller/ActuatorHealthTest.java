package com.zaid.examen.controller;

import com.zaid.examen.controller.SnowflakeHealthIndicator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "management.endpoints.web.exposure.include=health",
        "management.endpoint.health.show-components=always",
        "management.endpoint.health.show-details=always"
})
@AutoConfigureMockMvc
class ActuatorHealthTest {

    private final MockMvc mockMvc;

    ActuatorHealthTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @MockBean
    private SnowflakeHealthIndicator snowflakeHealthIndicator;

    @Test
    void actuatorHealth_up_includesSnowflakeUp() throws Exception {
        when(snowflakeHealthIndicator.health())
                .thenReturn(Health.up().withDetail("snowflake", "reachable").build());

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.snowflake.status").value("UP"));
    }

    @Test
    void actuatorHealth_down_whenSnowflakeDown() throws Exception {
        when(snowflakeHealthIndicator.health())
                .thenReturn(Health.down().withDetail("snowflake", "unreachable").build());

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable()) // Actuator responde 503 cuando status DOWN
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.components.snowflake.status").value("DOWN"));
    }
}
