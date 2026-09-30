package com.tienda.tiendaapi.domain;

import java.math.BigDecimal;

public enum TipoIva {
    GENERAL(new BigDecimal("0.21")),
    REDUCIDO(new BigDecimal("0.10")),
    SUPERREDUCIDO(new BigDecimal("0.04"));

    private final BigDecimal porcentaje;

    TipoIva(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }
}
