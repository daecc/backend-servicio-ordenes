package com.unmsm.marketplace.ordenes_service.client;

import com.unmsm.marketplace.ordenes_service.dto.OrdenMaestraVentasDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sales-service", url = "${sales.service.url}")
public interface SalesServiceClient {

    @PostMapping("/api/internal/ordenes/notificar")
    void notificarOrdenCreada(@RequestBody OrdenMaestraVentasDTO orden);
}
