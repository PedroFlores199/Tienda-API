package com.tienda.tiendaapi.repository;
import com.tienda.tiendaapi.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
