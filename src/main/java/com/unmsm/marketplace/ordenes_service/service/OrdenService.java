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

// NUEVO: Importaciones necesarias para el Round-Robin y Feign

import com.unmsm.marketplace.ordenes_service.dto.SellerDataDTO;
import com.unmsm.marketplace.ordenes_service.model.ControlRoundRobin;
import com.unmsm.marketplace.ordenes_service.repository.ControlRoundRobinRepository;
import com.unmsm.marketplace.ordenes_service.client.VendorStaffClient;
import com.unmsm.marketplace.ordenes_service.dto.StaffResponseWrapperDTO;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // NUEVO: Para manejar el Optional del Repositorio

@Service
public class OrdenService {

    private final OrdenMaestraRepository ordenMaestraRepository;
    private final SubOrdenRepository subOrdenRepository;
    private final ControlRoundRobinRepository rrRepository;
    private final VendorStaffClient vendorStaffClient;
    
    @Value("${vendor.service.secret}")
    private String vendorSecretKey;
    

    // EL CONSTRUCTOR CORREGIDO: Ahora incluye todas las dependencias 'final'
    public OrdenService(
            OrdenMaestraRepository ordenMaestraRepository, 
            SubOrdenRepository subOrdenRepository,
            ControlRoundRobinRepository rrRepository,
            VendorStaffClient vendorStaffClient) {
        this.ordenMaestraRepository = ordenMaestraRepository;
        this.subOrdenRepository = subOrdenRepository;
        this.rrRepository = rrRepository;
        this.vendorStaffClient = vendorStaffClient;
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
            
            // NUEVO: El idSeller ya no viene del JSON, lo calculamos con Round-Robin
            Long idSellerAsignado = calcularSiguienteSellerRoundRobin(subDto.idVendedor());
            subOrden.setIdSeller(idSellerAsignado);
            
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
                sub.getIdSeller(),
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

    @Transactional(readOnly = true)
    public List<SubOrdenResponseDTO> obtenerOrdenesPorVendedor(Long idVendedor) {
        List<SubOrden> subOrdenes = subOrdenRepository.findByIdVendedor(idVendedor);

        return subOrdenes.stream().map(sub -> new SubOrdenResponseDTO(
            sub.getIdSOrden(),
            sub.getOrdenMaestra().getIdOMaestra(),
            sub.getIdSeller(),
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
            sub.getIdSeller(),
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
        SubOrden subOrden = subOrdenRepository.findById(idSubOrden)
            .orElseThrow(() -> new RuntimeException("SubOrden no encontrada"));
        
        subOrden.setEstadoParcialVendedor(nuevoEstado);
        
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
        
        int nuevoEstadoGlobal = 1; 
        if (todosEntregados) {
            nuevoEstadoGlobal = 5; 
        } else if (algunEntregado) {
            nuevoEstadoGlobal = 4; 
        } else if (algunDespachado) {
            nuevoEstadoGlobal = 3; 
        } else if (algunPreparacion) {
            nuevoEstadoGlobal = 2; 
        }
        
        ordenMaestra.setEstadoGlobal(nuevoEstadoGlobal);
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
                sub.getIdSeller(),
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

    // =======================================================================
    // Método privado para ejecutar la lógica de balanceo Round-Robin
    // =======================================================================
    private Long calcularSiguienteSellerRoundRobin(Long vendorId) {
        try {
            // 1. Traer el objeto contenedor desde el microservicio
            StaffResponseWrapperDTO response = vendorStaffClient.obtenerStaffPorVendor(vendorSecretKey, vendorId);

            if (response == null || response.data() == null || response.data().isEmpty()) {
                throw new RuntimeException("No hay sellers disponibles para el vendor " + vendorId);
            }

            // Extraer la lista de empleados reales desde la propiedad 'data' del record
            List<SellerDataDTO> staff = response.data();

            // 2. Buscar el estado del balanceo actual en la base de datos
            ControlRoundRobin control = rrRepository.findById(vendorId).orElse(null);

            Long nuevoIdSellerAsignado;

            if (control == null || control.getUltimoIdSellerAsignado() == null) {
                // CASO A: Primera vez que se procesa una orden para esta tienda
                // Se asigna el primer empleado de la lista usando staff_id()
                nuevoIdSellerAsignado = staff.get(0).staff_id();
                
                if (control == null) {
                    control = new ControlRoundRobin();
                    control.setIdVendedor(vendorId);
                }
            } else {
                // CASO B: Ya existen asignaciones previas. Buscar la posición del último asignado.
                Long ultimoId = control.getUltimoIdSellerAsignado();
                int indiceActual = -1;

                for (int i = 0; i < staff.size(); i++) {
                    if (staff.get(i).staff_id().equals(ultimoId)) {
                        indiceActual = i;
                        break;
                    }
                }

                // Calcular la siguiente posición matemática usando el operador módulo %
                int siguienteIndice = (indiceActual + 1) % staff.size();
                nuevoIdSellerAsignado = staff.get(siguienteIndice).staff_id();
            }

            // 3. Persistir el ID del empleado que tomó esta orden para el siguiente turno
            control.setUltimoIdSellerAsignado(nuevoIdSellerAsignado);
            rrRepository.save(control);

            // 4. Retornar el ID para que sea guardado en la entidad SubOrden
            return nuevoIdSellerAsignado;

        } catch (Exception e) {
            System.err.println("Error en la lógica de Round-Robin o comunicación: " + e.getMessage());
            throw new RuntimeException("Error asignando sub-orden: " + e.getMessage());
        }
    }
}