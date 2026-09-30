package com.tienda.tiendaapi.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class FacturaDTO {
    private Long id;
    private String numeroFactura;
    private Long pedidoId;
    private LocalDateTime fechaEmision;
    private String nombreCliente;
    private String nifCliente;
    private String direccionCliente;
    private List<LineaFacturaDTO> lineas;
    private BigDecimal totalBase;
    private BigDecimal totalIva;
    private BigDecimal total;
}
