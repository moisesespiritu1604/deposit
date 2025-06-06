package com.bbva.ejdp.dto.deposit.enums;

public enum BusinessExceptionEJDP {
    // Validaciones (solo en transacción)
    MANDATORY_FIELD_MISSING("EJDP00000001", false, "Faltan campos obligatorios"),
    INVALID_ACCOUNT_NUMBER_FORMAT("EJDP00000002", false, "Número de cuenta inválido"),
    INVALID_CUSTOMER_NAME("EJDP00000003", false, "Nombre de cliente inválido"),
    INVALID_AMOUNT("EJDP00000004", false, "El monto es inválido"),
    INVALID_INTEREST_RATE("EJDP00000005", false, "La tasa de interés debe estar entre 0 y 100"),
    INVALID_TERM_DAYS("EJDP00000006", false, "El número de días debe ser mayor que cero"),

    // Técnicos (en librería)
    DUPLICATE_ACCOUNT_NUMBER("EJDP00000007", true, "El número de cuenta ya existe"),
    ERROR_CREATING_DEPOSIT("EJDP00000008", true, "Error al registrar el depósito"),
    ERROR_FETCHING_DEPOSITS("EJDP00000009", false, "Error al consultar los depósitos"),
    NO_DEPOSITS_FOUND("EJDP00000010", false, "No se encontraron depósitos");

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
