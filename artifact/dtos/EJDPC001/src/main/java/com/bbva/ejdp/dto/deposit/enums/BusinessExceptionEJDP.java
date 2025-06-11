package com.bbva.ejdp.dto.deposit.enums;

public enum BusinessExceptionEJDP {
    // Validaciones de negocio (transacción)
    MANDATORY_FIELD_MISSING("EJDP00000001", false, "Faltan campos obligatorios"),
    INVALID_ACCOUNT_NUMBER_FORMAT("EJDP00000002", false, "Número de cuenta inválido"),
    INVALID_CUSTOMER_NAME("EJDP00000003", false, "Nombre de cliente inválido"),
    INVALID_AMOUNT("EJDP00000004", false, "El monto es inválido"),
    INVALID_INTEREST_RATE("EJDP00000005", false, "La tasa de interés debe estar entre 0 y 100"),
    INVALID_TERM_DAYS("EJDP00000006", false, "El número de días debe ser mayor que cero"),

    // Errores de negocio (transaccionales con rollback)
    DUPLICATE_ACCOUNT_NUMBER("EJDP00000007", true, "El número de cuenta ya existe"),
    ERROR_CREATING_DEPOSIT("EJDP00000008", true, "Error al registrar el depósito"),
    INCOMPLETE_DEPOSIT_DATA("EJDP00000009", true, "Los datos del depósito registrado están incompletos"),

    // Errores de consulta (sin rollback)
    ERROR_FETCHING_DEPOSITS("EJDP00000010", false, "Error al consultar los depósitos"),
    NO_DEPOSITS_FOUND("EJDP00000011", false, "No se encontraron depósitos"),

    // Errores de conectividad y timeout (sin rollback - reintentos automáticos)
    DEPOSIT_API_TIMEOUT("EJDP00000012", false, "Timeout al consultar la API de depósitos"),
    DEPOSIT_API_UNAVAILABLE("EJDP00000013", false, "La API de depósitos no está disponible"),

    // Errores de cliente (4xx) - sin rollback, problema en request
    DEPOSIT_API_CLIENT_ERROR("EJDP00000014", false, "Error en los datos enviados a la API de depósitos"),

    // Errores de servidor (5xx) - con rollback, problema interno del servicio
    DEPOSIT_API_SERVER_ERROR("EJDP00000015", true, "Error interno del servidor de depósitos");

    private final String code;
    private final Boolean hasRollback;
    private final String message;

    BusinessExceptionEJDP(String code, Boolean hasRollback, String message) {
        this.code = code;
        this.hasRollback = hasRollback;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public Boolean getHasRollback() {
        return hasRollback;
    }

    public String getMessage() {
        return message;
    }
}
