package com.tienda.tiendaapi.dto;
import com.tienda.tiendaapi.domain.TipoIva;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class LineaFacturaDTO {
    private Long id;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private TipoIva tipoIva;
    private BigDecimal importeBase;
    private BigDecimal importeIva;
    private BigDecimal importeTotal;
}
