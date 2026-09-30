package com.tienda.tiendaapi.dto;
import com.tienda.tiendaapi.domain.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class CambioEstadoDTO {
    @NotNull
    private EstadoPedido nuevoEstado;
}
