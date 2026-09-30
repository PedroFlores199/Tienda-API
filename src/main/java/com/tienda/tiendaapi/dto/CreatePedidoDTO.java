package com.tienda.tiendaapi.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
@Data
public class CreatePedidoDTO {
    @NotNull
    private Long clienteId;
    @NotEmpty
    @Valid
    private List<CreateLineaPedidoDTO> lineas;
}
