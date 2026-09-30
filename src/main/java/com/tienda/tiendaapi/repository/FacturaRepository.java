package com.tienda.tiendaapi.repository;
import com.tienda.tiendaapi.domain.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.time.LocalDateTime;
import java.util.List;
public interface FacturaRepository extends JpaRepository<Factura, Long>, JpaSpecificationExecutor<Factura> {
    List<Factura> findByFechaEmisionBetween(LocalDateTime start, LocalDateTime end);
}
