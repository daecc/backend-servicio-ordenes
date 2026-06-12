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
@Table(name = "orden_historial")
public class OrdenHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ohistorial")
    private Long idOHistorial;

    @Column(name = "id_usuario_accion")
    private Long idUsuarioAccion;

    @Column(name = "campo_modificado")
    private String campoModificado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    // Relación: Muchos historiales pertenecen a una Sub Orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sorden", nullable = false)
    private SubOrden subOrden;
}
