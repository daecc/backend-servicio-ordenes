package com.unmsm.marketplace.ordenes_service.dto;

public record SellerDataDTO(
    Long staff_id,
    Long user_id,
    Long vendor_id,
    Integer role_id,
    String first_name,
    String last_name,
    String email,
    String personal_phone,
    String status,
    String availability_status,
    String created_at,
    String updated_at
) {}