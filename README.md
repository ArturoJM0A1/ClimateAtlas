# ClimateAtlas

Aplicación web para consultar el clima de ciudades mexicanas, almacenar lecturas meteorológicas en SQLite y explorarlas mediante un mapa e historial.

## Tecnologías

| Área | Tecnología |
|---|---|
| Backend | Java 25, Spring Boot 4.2.0 |
| Arquitectura backend | Capas `app`, `negocio` y `repository` |
| Procesamiento | Spring Batch 6.1.0 con JobRepository JDBC y Spring Scheduling |
| Persistencia | Spring Data JPA, Hibernate y SQLite |
| Frontend | Angular 22, TypeScript 6 y RxJS 7 |
| Mapa y gráficas | Leaflet 1.9 y Chart.js 4 |
| Datos | Open-Meteo |

## Arquitectura del backend

| Capa | Responsabilidad |
|---|---|
| `app` | Controladores REST, DTO, casos de uso, configuración y coordinación de Batch |
| `negocio` | Modelos puros, reglas de dominio, servicios y puertos |
| `repository` | Adaptadores de persistencia, entidades JPA, Spring Data e integración Open-Meteo |

Dependencias: `app → negocio` y `repository → negocio`. Negocio no depende de Spring ni JPA.

Detalles: [backend/README.md](backend/README.md) y [docs/ARQUITECTURA.md](docs/ARQUITECTURA.md).

## Requisitos previos

- Git.
- JDK 25.
- Maven 3.6.3 o posterior.
- Node.js 26 LTS y npm.
- Internet para Open-Meteo y los mosaicos del mapa.

SQLite se almacena en un archivo local y no requiere un servidor independiente.

## Instalación

### 1. Clonar

```bash
git clone https://github.com/ArturoJM0A1/ClimateAtlas.git
cd ClimateAtlas
```

### 2. Backend

```bash
cd backend
mvn clean verify
mvn spring-boot:run
```

API: `http://localhost:8080`.

### 3. Frontend

En otra terminal desde la raíz:

```bash
cd frontend
npm install
npm start
```

Aplicación: [http://localhost:4200](http://localhost:4200).

## Spring Scheduling + Spring Batch

Scheduling decide cuándo inicia el proceso. Batch coordina Reader, Processor y Writer. Estos componentes trabajan con modelos y puertos de negocio, no con entidades JPA. El JobRepository JDBC conserva los metadatos en el mismo SQLite que las mediciones.

El endpoint `POST /api/weather/refresh` inicia manualmente el mismo Job.

## Variables de configuración

| Variable | Predeterminado | Uso |
|---|---|---|
| `SERVER_PORT` | 8080 | Puerto HTTP |
| `CLIMATE_DB_PATH` | ./data/climateatlas.db | Archivo SQLite |
| `WEATHER_REFRESH_DELAY_MS` | 3600000 | Intervalo de actualización |
| `WEATHER_INITIAL_DELAY_MS` | 15000 | Espera inicial |
| `WEATHER_BATCH_CHUNK_SIZE` | 5 | Elementos por chunk |

## Documentación

- [Arquitectura](docs/ARQUITECTURA.md)
- [Guía de estudio](docs/GUIA_DE_ESTUDIO.md)
- [API REST](docs/API.md)
- [Base de datos](docs/BASE_DE_DATOS.md)

## Limitaciones

- El historial se acumula desde que se ejecuta el Job; no se crean mediciones históricas ficticias.
- Open-Meteo y las teselas del mapa requieren Internet.
- No hay autenticación ni endurecimiento para exposición pública.
- El backend dispone de pruebas unitarias y de integración; Angular se valida con build de producción.
