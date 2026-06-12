package com.unmsm.marketplace.ordenes_service.dto;

import java.util.List;

// Este record imita exactamente la estructura del JSON de tu compañero
public record StaffResponseWrapperDTO(
    boolean success,
    List<SellerDataDTO> data
) {}