package com.zaid.examen.dto.factory;

import com.zaid.examen.dto.ApiResponseDto;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class ApiResponses {

    private ApiResponses() {}

    public static <T> ResponseEntity<ApiResponseDto<T>> ok(String appEndpoint, int internalCode, String mensaje, T body) {
        return build(HttpStatus.OK, appEndpoint, internalCode, mensaje, body);
    }

    public static <T> ResponseEntity<ApiResponseDto<T>> badRequest(String appEndpoint, int internalCode, String mensaje, T body) {
        return build(HttpStatus.BAD_REQUEST, appEndpoint, internalCode, mensaje, body);
    }

    public static <T> ResponseEntity<ApiResponseDto<T>> serverError(String appEndpoint, int internalCode, String mensaje, T body) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, appEndpoint, internalCode, mensaje, body);
    }

    public static <T> ResponseEntity<ApiResponseDto<T>> notFound(String appEndpoint, int internalCode, String mensaje, T body) {
        return build(HttpStatus.NOT_FOUND, appEndpoint, internalCode, mensaje, body);
    }

    private static String currentRequestId() {
        String rid = MDC.get("requestId");
        return (rid == null || rid.isBlank()) ? UUID.randomUUID().toString() : rid;
    }

    public static <T> ResponseEntity<ApiResponseDto<T>> build(HttpStatus status, String appEndpoint, int internalCode, String mensaje, T body) {
        String codigo = status.value() + "." + appEndpoint + "." + internalCode;

        ApiResponseDto<T> resp = ApiResponseDto.<T>builder()
                .codigo(codigo)
                .id(currentRequestId())     // 👈 MISMO id que logs
                .mensaje(mensaje)
                .respuesta(body)
                .build();

        return ResponseEntity.status(status).body(resp);
    }
}
