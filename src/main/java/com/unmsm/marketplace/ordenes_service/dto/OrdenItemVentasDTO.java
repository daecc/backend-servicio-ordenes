package com.unmsm.marketplace.ordenes_service.dto;

import java.math.BigDecimal;

public record OrdenItemVentasDTO(
    Long idOItem,
    String idProducto,
    Integer cantidad,
    BigDecimal precioUnitario
) {}
