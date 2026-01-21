package com.zaid.examen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {
    private String codigo;   // La estructura que sigue este segmento es Codigo HTTP . Nombre de aplicacion . Codigo interno
    private String id;       // Es el UUID de la peticion tambien llamado requestId
    private String mensaje;  // Su valor depende del codigo HTTP es solo para mas formato
    private T respuesta;     // Objeto con la respuesta
}