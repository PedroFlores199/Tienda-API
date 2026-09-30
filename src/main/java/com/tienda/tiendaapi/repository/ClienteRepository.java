package com.tienda.tiendaapi.repository;
import com.tienda.tiendaapi.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
