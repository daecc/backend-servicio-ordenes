
package com.unmsm.marketplace.ordenes_service.repository;

import com.unmsm.marketplace.ordenes_service.model.SubOrden;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubOrdenRepository extends JpaRepository<SubOrden, Long>{
    // Spring Boot genera el "SELECT * FROM sub_ordenes WHERE id_vendedor = ?" automáticamente
    List<SubOrden> findByIdVendedor(Long idVendedor);
    List<SubOrden> findByNombreVendedorContainingIgnoreCase(String nombre);
}