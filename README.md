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
   +--------------------------------------------+
```

## Endpoints

| Metodo | Ruta | Descripcion | Consumido por |
|--------|------|-------------|---------------|
| POST | `/api/v1/ordenes/checkout` | Procesar checkout (recibe orden, asigna sellers via RR, notifica a Ventas) | Frontend Superadmin |
| GET | `/api/v1/ordenes` | Listar todas las ordenes maestras | Frontend Superadmin |
| GET | `/api/v1/ordenes/vendedor/{idVendedor}` | Buscar subordenes por ID de vendedor | Frontend Vendor Admin |
| GET | `/api/v1/ordenes/vendedor/nombre/{nombre}` | Buscar subordenes por nombre de vendedor | Frontend Vendor Admin |
| PUT | `/api/v1/ordenes/suborden/{idSubOrden}/estado/{nuevoEstado}` | Actualizar estado logistico de suborden | Frontend Vendor Admin |
| GET | `/api/v1/ordenes/cliente/{dni}` | Buscar ordenes por DNI del cliente | Frontend Superadmin |
| GET | `/swagger-ui.html` | Documentacion Swagger UI | Desarrolladores |
| GET | `/v3/api-docs` | OpenAPI spec en JSON | Desarrolladores |

## Flujo de una orden

1. El frontend envia un POST a `/api/v1/ordenes/checkout` con la orden maestra y sus subordenes
2. El servicio recibe la orden y para cada suborden:
   - Consulta al microservicio de **Gestion de Vendedores** (`GET /api/internal/staff`) para obtener los sellers disponibles
   - Aplica **Round-Robin** para asignar el siguiente seller disponible
   - Guarda la asignacion en la tabla `control_round_robin`
3. Persiste la orden completa en PostgreSQL
4. Notifica al microservicio de **Ventas** via `POST /api/internal/ordenes/notificar` con la orden creada y los sellers asignados

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
