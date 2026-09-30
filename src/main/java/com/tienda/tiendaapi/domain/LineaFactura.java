package com.tienda.tiendaapi.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lineas_factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineaFactura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @Column(nullable = false)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIva tipoIva;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importeBase;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importeIva;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importeTotal;
}
