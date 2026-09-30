package com.tienda.tiendaapi.repository;
import com.tienda.tiendaapi.domain.SecuenciaFactura;
import com.tienda.tiendaapi.domain.SecuenciaFacturaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface SecuenciaFacturaRepository extends JpaRepository<SecuenciaFactura, SecuenciaFacturaId> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SecuenciaFactura s WHERE s.serie = :serie AND s.anio = :anio")
    Optional<SecuenciaFactura> findByIdWithLock(@Param("serie") String serie, @Param("anio") Integer anio);
}
