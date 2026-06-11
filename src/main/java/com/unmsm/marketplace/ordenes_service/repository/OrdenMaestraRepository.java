
package com.unmsm.marketplace.ordenes_service.repository;

import com.unmsm.marketplace.ordenes_service.model.OrdenMaestra;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OrdenMaestraRepository extends JpaRepository<OrdenMaestra, Long> {
    List<OrdenMaestra> findByClienteDni(String clienteDni);
}
