package com.unmsm.marketplace.ordenes_service.service;

import com.unmsm.marketplace.ordenes_service.dto.OrdenItemRequestDTO;
import com.unmsm.marketplace.ordenes_service.dto.OrdenItemResponseDTO;
import com.unmsm.marketplace.ordenes_service.dto.OrdenMaestraRequestDTO;
import com.unmsm.marketplace.ordenes_service.dto.OrdenMaestraResponseDTO;
import com.unmsm.marketplace.ordenes_service.dto.SubOrdenRequestDTO;
import com.unmsm.marketplace.ordenes_service.dto.SubOrdenResponseDTO;
import com.unmsm.marketplace.ordenes_service.model.OrdenItem;
import com.unmsm.marketplace.ordenes_service.model.OrdenMaestra;
import com.unmsm.marketplace.ordenes_service.model.SubOrden;
import com.unmsm.marketplace.ordenes_service.repository.OrdenMaestraRepository;
import com.unmsm.marketplace.ordenes_service.repository.SubOrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdenService {

    private final OrdenMaestraRepository ordenMaestraRepository;
    private final SubOrdenRepository subOrdenRepository;

    // Inyección de dependencias por constructor de ambos repositorios
    public OrdenService(OrdenMaestraRepository ordenMaestraRepository, SubOrdenRepository subOrdenRepository) {
        this.ordenMaestraRepository = ordenMaestraRepository;
        this.subOrdenRepository = subOrdenRepository;
    }

    @Transactional
    public void crearOrdenDesdeCheckout(OrdenMaestraRequestDTO dto) {
        OrdenMaestra ordenMaestra = new OrdenMaestra();
        ordenMaestra.setClienteNombre(dto.clienteNombre());
        ordenMaestra.setClienteDni(dto.clienteDni());
        ordenMaestra.setMetodoPago(dto.metodoPago());
        ordenMaestra.setMontoTotalMaestro(dto.montoTotalMaestro());
        ordenMaestra.setEstadoGlobal(1); // 1 = PENDIENTE
        ordenMaestra.setFechaCreacion(LocalDateTime.now());
        ordenMaestra.setSubOrdenes(new ArrayList<>());

        for (SubOrdenRequestDTO subDto : dto.subOrdenes()) {
            SubOrden subOrden = new SubOrden();
            subOrden.setIdVendedor(subDto.idVendedor());
            subOrden.setNombreVendedor(subDto.nombreVendedor());
            subOrden.setDireccionEnvio(subDto.direccionEnvio());
            subOrden.setDistritoEnvio(subDto.distritoEnvio());
            subOrden.setMetodoEnvio(subDto.metodoEnvio());
            subOrden.setTelefonoContacto(subDto.telefonoContacto());
            subOrden.setMontoSubTotalVendedor(subDto.montoSubTotalVendedor());
            subOrden.setEstadoParcialVendedor(1); // 1 = PENDIENTE
            subOrden.setFechaCreacionSub(LocalDateTime.now());
            subOrden.setOrdenItems(new ArrayList<>());
            
            subOrden.setOrdenMaestra(ordenMaestra); 

            for (OrdenItemRequestDTO itemDto : subDto.items()) {
                OrdenItem item = new OrdenItem();
                item.setIdProducto(itemDto.idProducto());
                item.setCantidad(itemDto.cantidad());
                item.setPrecioUnitario(itemDto.precioUnitario());
                item.setEstadoItem(1); // 1 = ACTIVO
                
                item.setSubOrden(subOrden);
                subOrden.getOrdenItems().add(item);
            }
            ordenMaestra.getSubOrdenes().add(subOrden);
        }
        ordenMaestraRepository.save(ordenMaestra);
    }
    
    @Transactional(readOnly = true)
    public List<OrdenMaestraResponseDTO> obtenerTodasLasOrdenes() {
        List<OrdenMaestra> ordenes = ordenMaestraRepository.findAll();
        
        return ordenes.stream().map(orden -> new OrdenMaestraResponseDTO(
            orden.getIdOMaestra(),
            orden.getClienteNombre(),
            orden.getClienteDni(),
            orden.getMetodoPago(),
            orden.getEstadoGlobal(),
            orden.getMontoTotalMaestro(),
            orden.getFechaCreacion(),
            orden.getSubOrdenes().stream().map(sub -> new SubOrdenResponseDTO(
                sub.getIdSOrden(),
                sub.getOrdenMaestra().getIdOMaestra(),
                sub.getIdVendedor(),
                sub.getNombreVendedor(),
                sub.getDireccionEnvio(),
                sub.getDistritoEnvio(),
                sub.getMetodoEnvio(),
                sub.getTelefonoContacto(),
                sub.getEstadoParcialVendedor(),
                sub.getMontoSubTotalVendedor(),
                sub.getFechaCreacionSub(),
                sub.getOrdenItems().stream().map(item -> new OrdenItemResponseDTO(
                    item.getIdOItem(),
                    item.getIdProducto(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getEstadoItem()
                )).toList()
            )).toList()
        )).toList();
    }

    /**
     * Obtiene exclusivamente las sub-órdenes que corresponden a un vendedor específico.
     * Mapea directamente la entidad SubOrden a su SubOrdenResponseDTO correspondiente.
     */
    @Transactional(readOnly = true)
    public List<SubOrdenResponseDTO> obtenerOrdenesPorVendedor(Long idVendedor) {
        List<SubOrden> subOrdenes = subOrdenRepository.findByIdVendedor(idVendedor);

        return subOrdenes.stream().map(sub -> new SubOrdenResponseDTO(
            sub.getIdSOrden(),
            sub.getOrdenMaestra().getIdOMaestra(),
            sub.getIdVendedor(),
            sub.getNombreVendedor(),
            sub.getDireccionEnvio(),
            sub.getDistritoEnvio(),
            sub.getMetodoEnvio(),
            sub.getTelefonoContacto(),
            sub.getEstadoParcialVendedor(),
            sub.getMontoSubTotalVendedor(),
            sub.getFechaCreacionSub(),
            sub.getOrdenItems().stream().map(item -> new OrdenItemResponseDTO(
                item.getIdOItem(),
                item.getIdProducto(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getEstadoItem()
            )).toList()
        )).toList();
    }
    
    @Transactional(readOnly = true)
    public List<SubOrdenResponseDTO> buscarPorNombreVendedor(String nombreVendedor) {
        List<SubOrden> subOrdenes = subOrdenRepository.findByNombreVendedorContainingIgnoreCase(nombreVendedor);

        return subOrdenes.stream().map(sub -> new SubOrdenResponseDTO(
            sub.getIdSOrden(),
            sub.getOrdenMaestra().getIdOMaestra(),
            sub.getIdVendedor(),
            sub.getNombreVendedor(),
            sub.getDireccionEnvio(),
            sub.getDistritoEnvio(),
            sub.getMetodoEnvio(),
            sub.getTelefonoContacto(),
            sub.getEstadoParcialVendedor(),
            sub.getMontoSubTotalVendedor(),
            sub.getFechaCreacionSub(),
            sub.getOrdenItems().stream().map(item -> new OrdenItemResponseDTO(
                item.getIdOItem(),
                item.getIdProducto(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getEstadoItem()
            )).toList()
        )).toList();
    }
    
    
    @Transactional
    public void actualizarEstadoLogistico(Long idSubOrden, Integer nuevoEstado) {
    // 1. Buscamos la sub-orden en la Base de Datos
    SubOrden subOrden = subOrdenRepository.findById(idSubOrden)
        .orElseThrow(() -> new RuntimeException("SubOrden no encontrada"));
    
    // 2. Actualizamos el estado de la sub-orden elegida
    subOrden.setEstadoParcialVendedor(nuevoEstado);
    
    // 3. Obtenemos la Orden Maestra y todas sus sub-órdenes para recalcular el estado global
    OrdenMaestra ordenMaestra = subOrden.getOrdenMaestra();
    List<SubOrden> todasLasSubOrdenes = ordenMaestra.getSubOrdenes();
    
    boolean todosEntregados = true;
    boolean algunEntregado = false;
    boolean algunDespachado = false;
    boolean algunPreparacion = false;
    
    for (SubOrden sub : todasLasSubOrdenes) {
        int estado = sub.getEstadoParcialVendedor();
        if (estado != 4) todosEntregados = false;
        if (estado == 4) algunEntregado = true;
        if (estado == 3) algunDespachado = true;
        if (estado == 2) algunPreparacion = true;
    }
    
    int nuevoEstadoGlobal = 1; // 1 = PENDIENTE por defecto
    if (todosEntregados) {
        nuevoEstadoGlobal = 5; // 5 = ENTREGADA (Todo se entregó)
    } else if (algunEntregado) {
        nuevoEstadoGlobal = 4; // 4 = PARCIAL. ENTREGADA
    } else if (algunDespachado) {
        nuevoEstadoGlobal = 3; // 3 = PARCIAL. DESPACHADA
    } else if (algunPreparacion) {
        nuevoEstadoGlobal = 2; // 2 = EN PREPARACIÓN
    }
    
    // 4. Actualizamos el estado global en la Orden Maestra
    ordenMaestra.setEstadoGlobal(nuevoEstadoGlobal);
    
    // La anotación @Transactional se encarga de hacer el UPDATE automático en PostgreSQL
    }
    
    
    @Transactional(readOnly = true)
    public List<OrdenMaestraResponseDTO> obtenerOrdenesPorDni(String dni) {
        List<OrdenMaestra> ordenes = ordenMaestraRepository.findByClienteDni(dni);
        
        return ordenes.stream().map(orden -> new OrdenMaestraResponseDTO(
            orden.getIdOMaestra(),
            orden.getClienteNombre(),
            orden.getClienteDni(),
            orden.getMetodoPago(),
            orden.getEstadoGlobal(),
            orden.getMontoTotalMaestro(),
            orden.getFechaCreacion(),
            orden.getSubOrdenes().stream().map(sub -> new SubOrdenResponseDTO(
                sub.getIdSOrden(),
                sub.getOrdenMaestra().getIdOMaestra(),
                sub.getIdVendedor(),
                sub.getNombreVendedor(),
                sub.getDireccionEnvio(),
                sub.getDistritoEnvio(),
                sub.getMetodoEnvio(),
                sub.getTelefonoContacto(),
                sub.getEstadoParcialVendedor(),
                sub.getMontoSubTotalVendedor(),
                sub.getFechaCreacionSub(),
                sub.getOrdenItems().stream().map(item -> new OrdenItemResponseDTO(
                    item.getIdOItem(),
                    item.getIdProducto(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getEstadoItem()
                )).toList()
            )).toList()
        )).toList();
    }
    
}