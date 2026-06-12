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
    
    
    @GetMapping("/vendedor/nombre/{nombre}")
    public ResponseEntity<List<SubOrdenResponseDTO>> obtenerOrdenesPorNombreVendedor(@PathVariable String nombre) {
    List<SubOrdenResponseDTO> ordenes = ordenService.buscarPorNombreVendedor(nombre);
    
    if (ordenes.isEmpty()) {
        return ResponseEntity.noContent().build(); // Devuelve 204
    }
    
    return ResponseEntity.ok(ordenes);
    }
    
    
    @PutMapping("/suborden/{idSubOrden}/estado/{nuevoEstado}")
    public ResponseEntity<Void> cambiarEstadoSubOrden(
        @PathVariable Long idSubOrden, 
        @PathVariable Integer nuevoEstado) {
    
    // IMPORTANTE: Asegúrate de usar el nombre de tu variable inyectada (ej. subOrdenService)
    ordenService.actualizarEstadoLogistico(idSubOrden, nuevoEstado);
    
    return ResponseEntity.ok().build(); // Devuelve un 200 OK sin cuerpo
    }
    
    
    @GetMapping("/cliente/{dni}")
    public ResponseEntity<List<OrdenMaestraResponseDTO>> buscarOrdenesPorDni(@PathVariable String dni) {
        List<OrdenMaestraResponseDTO> ordenes = ordenService.obtenerOrdenesPorDni(dni);
        
        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build(); // Devuelve 204 si el DNI no tiene compras
        }
        
        return ResponseEntity.ok(ordenes);
    }
    
    
}