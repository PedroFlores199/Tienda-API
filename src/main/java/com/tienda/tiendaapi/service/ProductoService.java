package com.tienda.tiendaapi.service;

import com.tienda.tiendaapi.domain.Producto;
import com.tienda.tiendaapi.dto.ProductoDTO;
import com.tienda.tiendaapi.exception.ResourceNotFoundException;
import com.tienda.tiendaapi.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;

    private ProductoDTO mapToDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setPrecio(p.getPrecio());
        dto.setTipoIva(p.getTipoIva());
        dto.setStock(p.getStock());
        return dto;
    }

    private Producto mapToEntity(ProductoDTO dto) {
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setPrecio(dto.getPrecio());
        p.setTipoIva(dto.getTipoIva());
        p.setStock(dto.getStock());
        return p;
    }

    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductoDTO findById(Long id) {
        return productoRepository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    @Transactional
    public ProductoDTO create(ProductoDTO dto) {
        Producto entity = mapToEntity(dto);
        return mapToDTO(productoRepository.save(entity));
    }

    @Transactional
    public ProductoDTO update(Long id, ProductoDTO dto) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        p.setNombre(dto.getNombre());
        p.setPrecio(dto.getPrecio());
        p.setTipoIva(dto.getTipoIva());
        p.setStock(dto.getStock());
        return mapToDTO(productoRepository.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }
}
