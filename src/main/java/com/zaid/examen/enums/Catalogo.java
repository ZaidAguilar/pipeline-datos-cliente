package com.zaid.examen.enums;

public enum Catalogo {
    mensaje200("La petición se ha procesado exitosamente."),
    mensaje400("Petición procesada con error, favor de validar."),
    mensaje500("Error desconocido del servidor, contacte a soporte."),
    MSG_CUSTOMER_SENT("Registro enviado con Kafka.."),
    mensaje404("Registro no encontrado, favor de validar."),;

    private final String message;

    Catalogo(String message) {
        this.message = message;
    }

    public String msg() {
        return message;
    }
}
