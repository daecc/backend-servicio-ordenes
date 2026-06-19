# ordenes-service - Microservicio de Ordenes

Microservicio del Marketplace para gestion de ordenes maestras, subordenes por vendedor y asignacion Round-Robin de sellers.

## Arquitectura

```
[Frontend Superadmin / Frontend Vendor Admin]
              |
              v POST /api/v1/ordenes/checkout
   +--------------------------------------------+
   |            ordenes-service                  |
   |   (Java 17, Spring Boot 3.2.5)             |
   |                                            |
   |   +------------------+                     |
   |   |  OrdenService    |                     |
   |   +--------+---------+                     |
   |            |                               |
   |            v                               |
   |   +------------------+                     |
   |   | Round-Robin      |---GET /staff------> | Microservicio Gestion Vendedores
   |   | Seller Assigner  |<---staff list------ | (vendor-service)
   |   +--------+---------+                     |
   |            |                               |
   |            v                               |
   |   +------------------+                     |
   |   | PostgreSQL (Neon)|                     |
   |   +--------+---------+                     |
   |            |                               |
   |            v                               |
   |   +------------------+                     |
   |   | Feign Client     |---POST /notificar-> | Microservicio Ventas
   |   | SalesService     |                     | (sales-service)
   |   +------------------+                     |
   |            |                               |
   |            v                               |
   |   +------------------+                     |
   |   | OrdenArchivador  | (tarea @Scheduled)  |
   |   | 3:00 AM y 4:00 AM|                     |
   |   +------------------+                     |
   +--------------------------------------------+
```

## Endpoints

| Metodo | Ruta | Descripcion | Consumido por | Filtro |
|--------|------|-------------|---------------|--------|
| POST | `/api/v1/ordenes/checkout` | Procesar checkout (recibe orden, asigna sellers via RR, notifica a Ventas) | Frontend Superadmin | - |
| GET | `/api/v1/ordenes` | Listar todas las ordenes maestras | Frontend Superadmin | Todas |
| GET | `/api/v1/ordenes/vendedor/{idVendedor}` | Buscar subordenes por ID de vendedor | Frontend Admin Vendedor | Solo activas |
| GET | `/api/v1/ordenes/admin/vendedor/{idVendedor}` | Buscar subordenes por ID de vendedor (historial completo) | Frontend Superadmin | Todas |
| GET | `/api/v1/ordenes/vendedor/nombre/{nombre}` | Buscar subordenes por nombre de vendedor | Frontend Superadmin | Todas |
| PUT | `/api/v1/ordenes/suborden/{idSubOrden}/estado/{nuevoEstado}` | Actualizar estado logistico de suborden | Frontend Admin Vendedor | - |
| GET | `/api/v1/ordenes/cliente/{dni}` | Buscar ordenes por DNI del cliente | Frontend Cliente / Superadmin | Solo activas |
| GET | `/api/v1/ordenes/ventas/cliente/{dni}` | Buscar ordenes por DNI para el microservicio de Ventas | Microservicio Ventas | Solo activas |
| GET | `/swagger-ui.html` | Documentacion Swagger UI | Desarrolladores | - |
| GET | `/v3/api-docs` | OpenAPI spec en JSON | Desarrolladores | - |

## Flujo de una orden

1. El frontend envia un POST a `/api/v1/ordenes/checkout` con la orden maestra y sus subordenes
2. El servicio recibe la orden y para cada suborden:
   - Consulta al microservicio de **Gestion de Vendedores** (`GET /api/internal/staff`) para obtener los sellers disponibles
   - Aplica **Round-Robin** para asignar el siguiente seller disponible
   - Guarda la asignacion en la tabla `control_round_robin`
3. Persiste la orden completa en PostgreSQL
4. Notifica al microservicio de **Ventas** via `POST /api/internal/ordenes/notificar` con la orden creada y los sellers asignados

## Archivado automatico (Soft Delete)

El servicio incluye un archivador que se ejecuta diariamente via tareas programadas (`@Scheduled`):

| Horario | Accion |
|---------|--------|
| **3:00 AM** | Las ordenes completadas (estado 5) sin fecha de archivo reciben `fecha_archivado = ahora` |
| **4:00 AM** | Las ordenes con `fecha_archivado` mayor a 30 dias se marcan como `activo = false` (tanto la orden maestra como sus subordenes) |

Una vez archivada, no se puede modificar el estado de la orden (la API rechaza el cambio con error).

### Columnas agregadas a la BD

| Tabla | Columna | Tipo | Default |
|-------|---------|------|---------|
| `orden_maestra` | `activo` | boolean | true |
| `orden_maestra` | `fecha_archivado` | timestamp | null |
| `sub_orden` | `activo` | boolean | true |

## Estados de orden

| Estado | Descripcion |
|--------|-------------|
| 1 | PENDIENTE |
| 2 | PREPARANDO |
| 3 | DESPACHADO |
| 4 | ENTREGADO |
| 5 | COMPLETADO |

## Dependencias externas

| Servicio | URL | Uso |
|----------|-----|-----|
| PostgreSQL (Neon) | `ep-polished-lake-aqo6d7n4.c-8.us-east-1.aws.neon.tech` | Base de datos |
| Gestion Vendedores | `https://vendor-service-production-5b54.up.railway.app` | Obtener staff para Round-Robin |
| Ventas | configurable via `sales.service.url` | Notificar orden procesada |

## Ejecucion local

```bash
./mvnw spring-boot:run
```

El servicio arranca en `http://localhost:8081`.

Swagger UI: `http://localhost:8081/swagger-ui.html`

## CI/CD

El pipeline de GitHub Actions ejecuta:
1. **Build** - compilacion con Maven
2. **Test** - ejecucion de pruebas
3. **Deploy** - despliegue a Railway (solo en push a main)

Requiere el secret `RAILWAY_TOKEN` configurado en el repositorio.
