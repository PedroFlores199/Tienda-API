package com.tienda.tiendaapi.controller;

import com.tienda.tiendaapi.domain.EstadoPedido;
import com.tienda.tiendaapi.dto.CambioEstadoDTO;
import com.tienda.tiendaapi.dto.CreatePedidoDTO;
import com.tienda.tiendaapi.dto.PedidoDTO;
import com.tienda.tiendaapi.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDTO create(@Valid @RequestBody CreatePedidoDTO dto) {
        return pedidoService.crearPedido(dto);
    }

    @PutMapping("/{id}/estado")
    public PedidoDTO cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoDTO dto) {
        return pedidoService.cambiarEstado(id, dto.getNuevoEstado());
    }

    @GetMapping
    public Page<PedidoDTO> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            Pageable pageable) {
        return pedidoService.listarPedidos(clienteId, estado, desde, hasta, pageable);
    }
}
