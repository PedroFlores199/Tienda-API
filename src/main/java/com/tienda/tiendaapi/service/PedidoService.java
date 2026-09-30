package com.tienda.tiendaapi.service;

import com.tienda.tiendaapi.domain.*;
import com.tienda.tiendaapi.dto.*;
import com.tienda.tiendaapi.exception.BusinessLogicException;
import com.tienda.tiendaapi.exception.ResourceNotFoundException;
import com.tienda.tiendaapi.repository.ClienteRepository;
import com.tienda.tiendaapi.repository.PedidoRepository;
import com.tienda.tiendaapi.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final FacturaService facturaService; // We will create this

    @Transactional
    public PedidoDTO crearPedido(CreatePedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        for (CreateLineaPedidoDTO l : dto.getLineas()) {
            Producto producto = productoRepository.findById(l.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + l.getProductoId()));

            if (producto.getStock() < l.getCantidad()) {
                throw new BusinessLogicException("No hay stock suficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - l.getCantidad());
            productoRepository.save(producto);

            LineaPedido linea = new LineaPedido();
            linea.setProducto(producto);
            linea.setCantidad(l.getCantidad());
            linea.setPrecioUnitario(producto.getPrecio());
            linea.setTipoIva(producto.getTipoIva());

            BigDecimal cantidad = new BigDecimal(l.getCantidad());
            BigDecimal importeBase = producto.getPrecio().multiply(cantidad);
            BigDecimal importeIva = importeBase.multiply(producto.getTipoIva().getPorcentaje()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal importeTotal = importeBase.add(importeIva);

            linea.setImporteBase(importeBase);
            linea.setImporteIva(importeIva);
            linea.setImporteTotal(importeTotal);

            pedido.addLinea(linea);
        }

        calcularTotales(pedido);

        Pedido saved = pedidoRepository.save(pedido);
        return mapToDTO(saved);
    }

    private void calcularTotales(Pedido pedido) {
        BigDecimal totalBase = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (LineaPedido l : pedido.getLineas()) {
            totalBase = totalBase.add(l.getImporteBase());
            totalIva = totalIva.add(l.getImporteIva());
            total = total.add(l.getImporteTotal());
        }

        pedido.setTotalBase(totalBase);
        pedido.setTotalIva(totalIva);
        pedido.setTotal(total);
    }

    @Transactional
    public PedidoDTO cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        EstadoPedido estadoActual = pedido.getEstado();

        if (nuevoEstado == EstadoPedido.CANCELADO) {
            if (estadoActual != EstadoPedido.CREADO && estadoActual != EstadoPedido.PAGADO) {
                throw new BusinessLogicException("Solo se puede cancelar un pedido CREADO o PAGADO");
            }
            // Devolver stock
            for (LineaPedido linea : pedido.getLineas()) {
                Producto p = linea.getProducto();
                p.setStock(p.getStock() + linea.getCantidad());
                productoRepository.save(p);
            }
        } else if (nuevoEstado == EstadoPedido.PAGADO) {
            if (estadoActual != EstadoPedido.CREADO) throw new BusinessLogicException("Transición inválida a PAGADO");
        } else if (nuevoEstado == EstadoPedido.ENVIADO) {
            if (estadoActual != EstadoPedido.PAGADO) throw new BusinessLogicException("Transición inválida a ENVIADO");
        } else if (nuevoEstado == EstadoPedido.ENTREGADO) {
            if (estadoActual != EstadoPedido.ENVIADO) throw new BusinessLogicException("Transición inválida a ENTREGADO");
            // Generar factura (this might need to be called after save or during)
            facturaService.generarFactura(pedido);
        } else {
            throw new BusinessLogicException("Transición no permitida");
        }

        pedido.setEstado(nuevoEstado);
        return mapToDTO(pedidoRepository.save(pedido));
    }

    public Page<PedidoDTO> listarPedidos(Long clienteId, EstadoPedido estado, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        Specification<Pedido> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (clienteId != null) predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
            if (estado != null) predicates.add(cb.equal(root.get("estado"), estado));
            if (desde != null) predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCreacion"), desde));
            if (hasta != null) predicates.add(cb.lessThanOrEqualTo(root.get("fechaCreacion"), hasta));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return pedidoRepository.findAll(spec, pageable).map(this::mapToDTO);
    }

    public PedidoDTO mapToDTO(Pedido p) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(p.getId());
        dto.setClienteId(p.getCliente().getId());
        dto.setEstado(p.getEstado());
        dto.setFechaCreacion(p.getFechaCreacion());
        dto.setTotalBase(p.getTotalBase());
        dto.setTotalIva(p.getTotalIva());
        dto.setTotal(p.getTotal());
        if (p.getLineas() != null) {
            dto.setLineas(p.getLineas().stream().map(l -> {
                LineaPedidoDTO ldto = new LineaPedidoDTO();
                ldto.setId(l.getId());
                ldto.setProductoId(l.getProducto().getId());
                ldto.setCantidad(l.getCantidad());
                ldto.setPrecioUnitario(l.getPrecioUnitario());
                ldto.setTipoIva(l.getTipoIva());
                ldto.setImporteBase(l.getImporteBase());
                ldto.setImporteIva(l.getImporteIva());
                ldto.setImporteTotal(l.getImporteTotal());
                return ldto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
