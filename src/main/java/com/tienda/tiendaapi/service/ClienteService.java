package com.tienda.tiendaapi.service;

import com.tienda.tiendaapi.domain.Cliente;
import com.tienda.tiendaapi.dto.ClienteDTO;
import com.tienda.tiendaapi.exception.ResourceNotFoundException;
import com.tienda.tiendaapi.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    private ClienteDTO mapToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        dto.setDireccion(cliente.getDireccion());
        dto.setNif(cliente.getNif());
        return dto;
    }

    private Cliente mapToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setDireccion(dto.getDireccion());
        cliente.setNif(dto.getNif());
        return cliente;
    }

    public List<ClienteDTO> findAll() {
        return clienteRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ClienteDTO findById(Long id) {
        return clienteRepository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    @Transactional
    public ClienteDTO create(ClienteDTO dto) {
        Cliente entity = mapToEntity(dto);
        return mapToDTO(clienteRepository.save(entity));
    }

    @Transactional
    public ClienteDTO update(Long id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setDireccion(dto.getDireccion());
        cliente.setNif(dto.getNif());
        return mapToDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado");
        }
        clienteRepository.deleteById(id);
    }
}
