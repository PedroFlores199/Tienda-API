package com.tienda.tiendaapi.dto;
import com.tienda.tiendaapi.domain.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private EstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private List<LineaPedidoDTO> lineas;
    private BigDecimal totalBase;
    private BigDecimal totalIva;
    private BigDecimal total;
}
