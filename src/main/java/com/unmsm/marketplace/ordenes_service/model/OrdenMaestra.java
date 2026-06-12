package com.unmsm.marketplace.ordenes_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orden_maestra")
public class OrdenMaestra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_omaestra")
    private Long idOMaestra;

    @Column(name = "cliente_nombre")
    private String clienteNombre;

    @Column(name = "cliente_dni")
    private String clienteDni;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "estado_global")
    private Integer estadoGlobal;

    @Column(name = "monto_total_maestro")
    private BigDecimal montoTotalMaestro;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "ordenMaestra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubOrden> subOrdenes;
}
