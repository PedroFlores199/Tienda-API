package com.tienda.tiendaapi.controller;

import com.tienda.tiendaapi.dto.ClienteDTO;
import com.tienda.tiendaapi.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @GetMapping
    public List<ClienteDTO> getAll() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    public ClienteDTO getById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDTO create(@Valid @RequestBody ClienteDTO dto) {
        return clienteService.create(dto);
    }

    @PutMapping("/{id}")
    public ClienteDTO update(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        return clienteService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clienteService.delete(id);
    }
}
