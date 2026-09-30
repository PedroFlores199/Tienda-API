package com.tienda.tiendaapi.dto;
import com.tienda.tiendaapi.domain.TipoIva;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class ProductoDTO {
    private Long id;
    @NotBlank
    private String nombre;
    @NotNull
    @Min(0)
    private BigDecimal precio;
    @NotNull
    private TipoIva tipoIva;
    @NotNull
    @Min(0)
    private Integer stock;
}
