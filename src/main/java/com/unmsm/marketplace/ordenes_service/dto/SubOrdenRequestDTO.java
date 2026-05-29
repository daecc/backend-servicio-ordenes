package com.unmsm.marketplace.ordenes_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record SubOrdenRequestDTO(
    Long idVendedor,
    String nombreVendedor,
    String direccionEnvio,
    String distritoEnvio,
    String metodoEnvio,
    String telefonoContacto,
    BigDecimal montoSubTotalVendedor,
    List<OrdenItemRequestDTO> items
) {}