package com.unmsm.marketplace.ordenes_service.controller;

import com.unmsm.marketplace.ordenes_service.dto.OrdenMaestraRequestDTO;
import com.unmsm.marketplace.ordenes_service.dto.OrdenMaestraResponseDTO;
import com.unmsm.marketplace.ordenes_service.dto.OrdenMaestraVentasDTO;
import com.unmsm.marketplace.ordenes_service.dto.SubOrdenResponseDTO;
import com.unmsm.marketplace.ordenes_service.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ordenes")
@CrossOrigin(origins = "*")
@Tag(name = "Ordenes", description = "Endpoints para gestion de ordenes del marketplace")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }
    
    
    @PostMapping("/checkout")
    @Operation(summary = "Procesar checkout", description = "Recibe una orden maestra, asigna vendedores via Round-Robin y notifica al microservicio de Ventas")
    @ApiResponse(responseCode = "201", description = "Orden creada exitosamente")
    public ResponseEntity<String> procesarCheckout(@RequestBody OrdenMaestraRequestDTO request) {
        ordenService.crearOrdenDesdeCheckout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Orden procesada y dividida correctamente en la Base de Datos");
    }
    
    
    @GetMapping
    @Operation(summary = "Listar todas las ordenes maestras", description = "Para el superadmin, muestra todas las ordenes sin filtro")
    public ResponseEntity<List<OrdenMaestraResponseDTO>> listarTodasLasOrdenes() {
        List<OrdenMaestraResponseDTO> ordenes = ordenService.obtenerTodasLasOrdenes();
        return ResponseEntity.ok(ordenes);
    }
    
    
    @GetMapping("/vendedor/{idVendedor}")
    @Operation(summary = "Buscar subordenes por ID de vendedor (solo activas)", description = "Para el frontend del admin vendedor que inicio sesion")
    public ResponseEntity<List<SubOrdenResponseDTO>> listarPorVendedor(@PathVariable Long idVendedor) {
    List<SubOrdenResponseDTO> subOrdenes = ordenService.obtenerOrdenesPorVendedor(idVendedor);
    
    if (subOrdenes.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    
    return ResponseEntity.ok(subOrdenes);
    }
    
    
    @GetMapping("/admin/vendedor/{idVendedor}")
    @Operation(summary = "Buscar subordenes por ID de vendedor (todas)", description = "Para el buscador del superadmin, muestra historial completo")
    public ResponseEntity<List<SubOrdenResponseDTO>> listarPorVendedorAdmin(@PathVariable Long idVendedor) {
    List<SubOrdenResponseDTO> subOrdenes = ordenService.obtenerTodasLasOrdenesPorVendedor(idVendedor);
    
    if (subOrdenes.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    
    return ResponseEntity.ok(subOrdenes);
    }
    
    
    @GetMapping("/vendedor/nombre/{nombre}")
    @Operation(summary = "Buscar subordenes por nombre de vendedor", description = "Busqueda case-insensitive")
    public ResponseEntity<List<SubOrdenResponseDTO>> obtenerOrdenesPorNombreVendedor(@PathVariable String nombre) {
    List<SubOrdenResponseDTO> ordenes = ordenService.buscarPorNombreVendedor(nombre);
    
    if (ordenes.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    
    return ResponseEntity.ok(ordenes);
    }
    
    
    @PutMapping("/suborden/{idSubOrden}/estado/{nuevoEstado}")
    @Operation(summary = "Actualizar estado logistico de una suborden")
    public ResponseEntity<Void> cambiarEstadoSubOrden(
        @PathVariable Long idSubOrden, 
        @PathVariable Integer nuevoEstado) {
    
    ordenService.actualizarEstadoLogistico(idSubOrden, nuevoEstado);
    
    return ResponseEntity.ok().build();
    }
    
    
    @GetMapping("/ventas/cliente/{dni}")
    @Operation(summary = "Buscar ordenes por DNI para el microservicio de Ventas", description = "Endpoint exclusivo para Ventas, retorna solo los datos necesarios para que el cliente vea sus pedidos")
    public ResponseEntity<List<OrdenMaestraVentasDTO>> buscarOrdenesPorDniParaVentas(@PathVariable String dni) {
        List<OrdenMaestraVentasDTO> ordenes = ordenService.obtenerOrdenesPorDniParaVentas(dni);
        
        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(ordenes);
    }
    
    
    @GetMapping("/cliente/{dni}")
    @Operation(summary = "Buscar ordenes por DNI del cliente")
    public ResponseEntity<List<OrdenMaestraResponseDTO>> buscarOrdenesPorDni(@PathVariable String dni) {
        List<OrdenMaestraResponseDTO> ordenes = ordenService.obtenerOrdenesPorDni(dni);
        
        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(ordenes);
    }
    
}