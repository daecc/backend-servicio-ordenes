package com.unmsm.marketplace.ordenes_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record SubOrdenVentasDTO(
    Long idSOrden,
    String nombreVendedor,
    Integer estadoParcialVendedor,
    BigDecimal montoSubTotalVendedor,
    List<OrdenItemVentasDTO> items
) {}
