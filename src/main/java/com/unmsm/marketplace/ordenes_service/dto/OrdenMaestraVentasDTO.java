package com.unmsm.marketplace.ordenes_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenMaestraVentasDTO(
    Long idOMaestra,
    String clienteNombre,
    String clienteDni,
    String metodoPago,
    Integer estadoGlobal,
    BigDecimal montoTotalMaestro,
    LocalDateTime fechaCreacion,
    List<SubOrdenVentasDTO> subOrdenes
) {}
