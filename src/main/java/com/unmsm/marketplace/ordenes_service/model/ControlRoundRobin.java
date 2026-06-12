
package com.unmsm.marketplace.ordenes_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "control_round_robin")
public class ControlRoundRobin {

    @Id
    @Column(name = "id_vendedor")
    private Long idVendedor; // El ID de "Electro Hogar Perú" (ej: 59)

    @Column(name = "ultimo_id_seller_asignado")
    private Long ultimoIdSellerAsignado; // El ID de "Juan Pérez" (ej: 57)
}