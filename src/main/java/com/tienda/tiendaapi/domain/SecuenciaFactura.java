package com.tienda.tiendaapi.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "secuencias_factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(SecuenciaFacturaId.class)
public class SecuenciaFactura {
    @Id
    @Column(nullable = false)
    private String serie;

    @Id
    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Long siguienteValor;
}
