package com.bbva.ejdp.dto.deposit.enums;

public enum BusinessExceptionEJDP {
    // Validaciones (solo en transacción)
    MANDATORY_FIELD_MISSING("EJDP00000001", false, "Faltan campos obligatorios"),
    INVALID_ACCOUNT_NUMBER_FORMAT("EJDP00000002", false, "Número de cuenta inválido"),
    INVALID_CUSTOMER_NAME("EJDP00000007", false, "Nombre de cliente inválido"),
    INVALID_AMOUNT("EJDP00000008", false, "El monto es inválido"),
    INVALID_INTEREST_RATE("EJDP00000009", false, "La tasa de interés debe estar entre 0 y 100"),
    INVALID_TERM_DAYS("EJDP00000010", false, "El número de días debe ser mayor que cero"),

    // Técnicos (en librería)
    DUPLICATE_ACCOUNT_NUMBER("EJDP00000003", true, "El número de cuenta ya existe"),
    ERROR_CREATING_CUSTOMER("EJDP00000004", true, "Error al crear el cliente"),
    ERROR_CREATING_DEPOSIT("EJDP00000005", true, "Error al registrar el depósito"),
    ERROR_FETCHING_DEPOSITS("EJDP00000006", false, "Error al consultar los depósitos");

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
