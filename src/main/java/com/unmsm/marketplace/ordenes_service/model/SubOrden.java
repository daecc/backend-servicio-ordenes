
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
@Table(name = "sub_orden")
public class SubOrden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   
    @Column(name = "id_sorden")
    private Long idSOrden;
    
    @Column(name = "id_seller")
    private Long idSeller;

    @Column(name = "id_vendedor")
    private Long idVendedor;
    
    @Column(name = "NombreVendedor")
    private String nombreVendedor;
    
    @Column(name = "id_liquid_vendedor")
    private Long idLiquidVendedor;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Column(name = "distrito_envio")
    private String distritoEnvio;

    @Column(name = "metodo_envio")
    private String metodoEnvio;

    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    @Column(name = "estado_parcial_vendedor")
    private Integer estadoParcialVendedor;

    @Column(name = "monto_subtotal_vendedor")
    private BigDecimal montoSubTotalVendedor;

    @Column(name = "fecha_creacion_sub")
    private LocalDateTime fechaCreacionSub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_omaestra", nullable = false)
    private OrdenMaestra ordenMaestra;

    @OneToMany(mappedBy = "subOrden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenItem> ordenItems;
    // Añade esto al final de SubOrden.java
    @OneToMany(mappedBy = "subOrden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenHistorial> historiales;

  
}
