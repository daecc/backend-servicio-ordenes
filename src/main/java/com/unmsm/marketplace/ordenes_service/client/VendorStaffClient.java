package com.unmsm.marketplace.ordenes_service.client;

import com.unmsm.marketplace.ordenes_service.dto.SellerDataDTO;
import com.unmsm.marketplace.ordenes_service.dto.StaffResponseWrapperDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "vendor-staff-service", url = "https://vendor-service-production-5b54.up.railway.app")
public interface VendorStaffClient {

    @GetMapping("/api/internal/staff")
    StaffResponseWrapperDTO obtenerStaffPorVendor( // <-- AHORA DEVUELVE EL ENVOLTORIO
        @RequestHeader("x-service-secret") String secretKey, 
        @RequestParam("vendor_id") Long vendorId 
    );
}