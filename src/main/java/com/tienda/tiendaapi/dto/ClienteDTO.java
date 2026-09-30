package com.tienda.tiendaapi.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ClienteDTO {
    private Long id;
    @NotBlank
    private String nombre;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String direccion;
    @NotBlank
    private String nif;
}
