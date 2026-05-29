package com.unmsm.marketplace.ordenes_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrdenMaestraRequestDTO(
    String clienteNombre,
    String clienteDni,
    String metodoPago,
    BigDecimal montoTotalMaestro,
    List<SubOrdenRequestDTO> subOrdenes
) {}