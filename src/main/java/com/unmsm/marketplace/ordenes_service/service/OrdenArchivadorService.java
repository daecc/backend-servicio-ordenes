package com.unmsm.marketplace.ordenes_service.service;

import com.unmsm.marketplace.ordenes_service.model.OrdenMaestra;
import com.unmsm.marketplace.ordenes_service.model.SubOrden;
import com.unmsm.marketplace.ordenes_service.repository.OrdenMaestraRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenArchivadorService {

    private final OrdenMaestraRepository ordenMaestraRepository;

    public OrdenArchivadorService(OrdenMaestraRepository ordenMaestraRepository) {
        this.ordenMaestraRepository = ordenMaestraRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void archivarOrdenesCompletadas() {
        List<OrdenMaestra> completadas = ordenMaestraRepository
            .findByEstadoGlobalAndActivoTrueAndFechaArchivadoIsNull(5);

        LocalDateTime ahora = LocalDateTime.now();
        for (OrdenMaestra orden : completadas) {
            orden.setFechaArchivado(ahora);
        }
        ordenMaestraRepository.saveAll(completadas);
        System.out.println("Archivador: " + completadas.size() + " ordenes marcadas con fecha de archivo");
    }

    @Transactional
    @Scheduled(cron = "0 0 4 * * ?")
    public void desactivarOrdenesArchivadas() {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        List<OrdenMaestra> paraArchivar = ordenMaestraRepository
            .findByActivoTrueAndFechaArchivadoBefore(hace30Dias);

        for (OrdenMaestra orden : paraArchivar) {
            orden.setActivo(false);
            for (SubOrden sub : orden.getSubOrdenes()) {
                sub.setActivo(false);
            }
        }
        ordenMaestraRepository.saveAll(paraArchivar);
        System.out.println("Archivador: " + paraArchivar.size() + " ordenes desactivadas (mas de 30 dias)");
    }
}
