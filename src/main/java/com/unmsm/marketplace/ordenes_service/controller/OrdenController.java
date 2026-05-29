package com.unmsm.marketplace.ordenes_service.controller;

import com.unmsm.marketplace.ordenes_service.dto.OrdenMaestraRequestDTO;
import com.unmsm.marketplace.ordenes_service.dto.OrdenMaestraResponseDTO;
import com.unmsm.marketplace.ordenes_service.dto.SubOrdenResponseDTO;
import com.unmsm.marketplace.ordenes_service.service.OrdenService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ordenes")
@CrossOrigin(origins = "*") 
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> procesarCheckout(@RequestBody OrdenMaestraRequestDTO request) {
        ordenService.crearOrdenDesdeCheckout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("¡Orden procesada y dividida correctamente en la Base de Datos!");
    }
    
    @GetMapping 
    public ResponseEntity<List<OrdenMaestraResponseDTO>> listarTodasLasOrdenes() {
        List<OrdenMaestraResponseDTO> ordenes = ordenService.obtenerTodasLasOrdenes();
        return ResponseEntity.ok(ordenes);
    }
    
    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<SubOrdenResponseDTO>> listarPorVendedor(@PathVariable Long idVendedor) {
    List<SubOrdenResponseDTO> subOrdenes = ordenService.obtenerOrdenesPorVendedor(idVendedor);
    
    if (subOrdenes.isEmpty()) {
        return ResponseEntity.noContent().build(); // Devuelve 204 si el vendedor no tiene ventas aún
    }
    
    return ResponseEntity.ok(subOrdenes); // Devuelve 200 con la lista en formato JSON
}
    
    
}