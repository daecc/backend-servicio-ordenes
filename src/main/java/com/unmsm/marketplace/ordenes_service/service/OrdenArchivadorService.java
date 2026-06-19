package com.unmsm.marketplace.ordenes_service.service;

import com.unmsm.marketplace.ordenes_service.client.AnalyticsClient;
import com.unmsm.marketplace.ordenes_service.dto.AnalyticsEvent;
import com.unmsm.marketplace.ordenes_service.model.OrdenMaestra;
import com.unmsm.marketplace.ordenes_service.model.SubOrden;
import com.unmsm.marketplace.ordenes_service.repository.OrdenMaestraRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrdenArchivadorService {

    private final OrdenMaestraRepository ordenMaestraRepository;
    private final AnalyticsClient analyticsClient;

    @Value("${analytics.api.key}")
    private String analyticsApiKey;

    public OrdenArchivadorService(OrdenMaestraRepository ordenMaestraRepository, AnalyticsClient analyticsClient) {
        this.ordenMaestraRepository = ordenMaestraRepository;
        this.analyticsClient = analyticsClient;
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

            try {
                analyticsClient.sendEvent(analyticsApiKey, new AnalyticsEvent(
                    UUID.randomUUID().toString(),
                    "ORDER_ARCHIVED",
                    "ordenes-service",
                    "orden_maestra",
                    String.valueOf(orden.getIdOMaestra()),
                    orden.getSubOrdenes().stream()
                        .map(sub -> String.valueOf(sub.getIdVendedor()))
                        .distinct()
                        .toList(),
                    Instant.now().toString(),
                    Map.of(
                        "id_orden", orden.getIdOMaestra(),
                        "fecha_archivado", orden.getFechaArchivado().toString()
                    )
                ));
            } catch (Exception e) {
                System.err.println("[ANALYTICS] Error enviando ORDER_ARCHIVED: " + e.getMessage());
            }
        }
        ordenMaestraRepository.saveAll(paraArchivar);
        System.out.println("Archivador: " + paraArchivar.size() + " ordenes desactivadas (mas de 30 dias)");
    }
}
