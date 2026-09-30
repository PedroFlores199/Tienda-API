package com.tienda.tiendaapi.service;

import com.tienda.tiendaapi.domain.*;
import com.tienda.tiendaapi.dto.*;
import com.tienda.tiendaapi.repository.FacturaRepository;
import com.tienda.tiendaapi.repository.SecuenciaFacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FacturaService {
    private final FacturaRepository facturaRepository;
    private final SecuenciaFacturaRepository secuenciaFacturaRepository;
    private final MailService mailService;

    @Transactional(propagation = Propagation.MANDATORY)
    public void generarFactura(Pedido pedido) {
        LocalDateTime now = LocalDateTime.now();
        int anio = now.getYear();
        String serie = "A";

        SecuenciaFactura sec = secuenciaFacturaRepository.findByIdWithLock(serie, anio)
                .orElse(null);
        if (sec == null) {
            sec = new SecuenciaFactura(serie, anio, 1L);
        }
        Long numero = sec.getSiguienteValor();
        sec.setSiguienteValor(numero + 1);
        secuenciaFacturaRepository.save(sec);

        String numeroFactura = String.format("%04d-%s-%06d", anio, serie, numero);

        Factura factura = new Factura();
        factura.setNumeroFactura(numeroFactura);
        factura.setPedido(pedido);
        factura.setFechaEmision(now);
        factura.setNombreCliente(pedido.getCliente().getNombre());
        factura.setNifCliente(pedido.getCliente().getNif());
        factura.setDireccionCliente(pedido.getCliente().getDireccion());
        factura.setEmailCliente(pedido.getCliente().getEmail());
        factura.setTotalBase(pedido.getTotalBase());
        factura.setTotalIva(pedido.getTotalIva());
        factura.setTotal(pedido.getTotal());

        for (LineaPedido lp : pedido.getLineas()) {
            LineaFactura lf = new LineaFactura();
            lf.setNombreProducto(lp.getProducto().getNombre());
            lf.setCantidad(lp.getCantidad());
            lf.setPrecioUnitario(lp.getPrecioUnitario());
            lf.setTipoIva(lp.getTipoIva());
            lf.setImporteBase(lp.getImporteBase());
            lf.setImporteIva(lp.getImporteIva());
            lf.setImporteTotal(lp.getImporteTotal());
            factura.addLinea(lf);
        }

        facturaRepository.save(factura);
        mailService.enviarFactura(factura);
    }

    public Page<FacturaDTO> listarFacturas(String clienteNombre, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        Specification<Factura> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (clienteNombre != null) predicates.add(cb.like(cb.lower(root.get("nombreCliente")), "%" + clienteNombre.toLowerCase() + "%"));
            if (desde != null) predicates.add(cb.greaterThanOrEqualTo(root.get("fechaEmision"), desde));
            if (hasta != null) predicates.add(cb.lessThanOrEqualTo(root.get("fechaEmision"), hasta));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return facturaRepository.findAll(spec, pageable).map(this::mapToDTO);
    }

    public List<Factura> getFacturasPorMes(int anio, int mes) {
        LocalDateTime start = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusNanos(1);
        return facturaRepository.findByFechaEmisionBetween(start, end);
    }

    public FacturaDTO mapToDTO(Factura f) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(f.getId());
        dto.setNumeroFactura(f.getNumeroFactura());
        dto.setPedidoId(f.getPedido() != null ? f.getPedido().getId() : null);
        dto.setFechaEmision(f.getFechaEmision());
        dto.setNombreCliente(f.getNombreCliente());
        dto.setNifCliente(f.getNifCliente());
        dto.setDireccionCliente(f.getDireccionCliente());
        dto.setTotalBase(f.getTotalBase());
        dto.setTotalIva(f.getTotalIva());
        dto.setTotal(f.getTotal());
        if (f.getLineas() != null) {
            dto.setLineas(f.getLineas().stream().map(l -> {
                LineaFacturaDTO ldto = new LineaFacturaDTO();
                ldto.setId(l.getId());
                ldto.setNombreProducto(l.getNombreProducto());
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
