
package com.unmsm.marketplace.ordenes_service.repository;
import com.unmsm.marketplace.ordenes_service.model.ControlRoundRobin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlRoundRobinRepository extends JpaRepository<ControlRoundRobin, Long> {
    // No necesitas escribir métodos extra. 
    // JpaRepository ya te da findById(idVendedor) y save(entidad)
}