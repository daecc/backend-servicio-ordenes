package com.unmsm.marketplace.ordenes_service.dto;

import java.math.BigDecimal;

public record OrdenItemRequestDTO(
    String idProducto,
    Integer cantidad,
    BigDecimal precioUnitario
) {}