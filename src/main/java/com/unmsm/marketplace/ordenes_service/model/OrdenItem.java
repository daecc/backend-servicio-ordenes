package com.unmsm.marketplace.ordenes_service.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orden_item")
public class OrdenItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oitem")
    private Long idOItem;

    @Column(name = "id_producto")
    private String idProducto; 

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @Column(name = "estado_item")
    private Integer estadoItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sorden", nullable = false)
    private SubOrden subOrden;
}
