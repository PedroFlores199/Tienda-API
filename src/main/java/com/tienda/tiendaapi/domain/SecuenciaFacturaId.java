package com.tienda.tiendaapi.domain;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SecuenciaFacturaId implements Serializable {
    private String serie;
    private Integer anio;
}
