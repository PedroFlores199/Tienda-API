package com.tienda.tiendaapi.controller;

import com.tienda.tiendaapi.dto.ProductoDTO;
import com.tienda.tiendaapi.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public List<ProductoDTO> getAll() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public ProductoDTO getById(@PathVariable Long id) {
        return productoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO create(@Valid @RequestBody ProductoDTO dto) {
        return productoService.create(dto);
    }

    @PutMapping("/{id}")
    public ProductoDTO update(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return productoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productoService.delete(id);
    }
}
