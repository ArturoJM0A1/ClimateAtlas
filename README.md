# ClimateAtlas

Aplicación web para consultar temperaturas de ciudades mexicanas, almacenar lecturas en SQLite y explorarlas mediante un mapa y gráficas históricas.

El proyecto tiene un enfoque educativo: las clases principales incluyen comentarios explicativos en español y la documentación describe los conceptos dentro de los archivos reales.

## Tecnologías

| Área | Tecnología |
|---|---|
| Backend | Java 25, Spring Boot 4.2.0, Spring Web MVC, Spring Data JPA, Hibernate |
| Procesamiento | Spring Batch 6.1.0 con JobRepository JDBC y Spring Scheduling |
| Base de datos | SQLite (Xerial JDBC) |
| Frontend | Angular 22, TypeScript 6, RxJS 7, Angular Router, HttpClient |
| Mapa | Leaflet 1.9 |
| Gráficas | Chart.js 4 |
| Datos meteorológicos | Open-Meteo |

Spring Boot 4.2.0 administra las versiones compatibles de Spring Batch 6.1.0 y Spring Data. Para persistir los metadatos de ejecución de Batch, el backend utiliza **`spring-boot-starter-batch-jdbc`**, no el starter Batch en memoria. Así las tablas `BATCH_*` se guardan en el mismo SQLite que las lecturas meteorológicas.

Consulta las [versiones de Angular](https://angular.dev/reference/versions), los [requisitos de Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html) y la [documentación de Spring Batch](https://docs.spring.io/spring-batch/reference/index.html).

## Requisitos previos

- Git.
- JDK 25.
- Apache Maven 3.6.3 o posterior.
- Node.js 26 LTS y npm.
- Internet para Open-Meteo y las teselas de OpenStreetMap.

SQLite no necesita un servidor independiente: la aplicación utiliza un archivo local.

## Instalación

### 1. Clonar

```bash
git clone https://github.com/ArturoJM0A1/ClimateAtlas.git
cd ClimateAtlas
```

### 2. Backend

Desde la raíz:

```bash
cd backend
mvn clean test
mvn spring-boot:run
```

API: `http://localhost:8080`.

Archivo local: `backend/data/climateatlas.db`.

### 3. Frontend

En otra terminal, desde la raíz:

```bash
cd frontend
npm install
npm start
```

Aplicación: [http://localhost:4200](http://localhost:4200).

El proxy de desarrollo envía las rutas `/api` al backend local.

### 4. Configuración opcional

En `backend/src/main/resources/application.yml`:

| Variable | Predeterminado | Uso |
|---|---|---|
| `SERVER_PORT` | 8080 | Puerto HTTP |
| `CLIMATE_DB_PATH` | ./data/climateatlas.db | Archivo SQLite |
| `WEATHER_REFRESH_DELAY_MS` | 3600000 | Intervalo entre actualizaciones programadas |
| `WEATHER_INITIAL_DELAY_MS` | 15000 | Espera antes de la primera ejecución |
| `WEATHER_BATCH_CHUNK_SIZE` | 5 | Elementos por transacción de Batch |

No necesitas crear un archivo `.env`; las variables se pueden definir en el entorno de la terminal.

## Comprobar el funcionamiento

Con el backend iniciado:

```bash
curl http://localhost:8080/api/cities
curl http://localhost:8080/api/weather/statistics
curl -X POST http://localhost:8080/api/weather/refresh
curl "http://localhost:8080/api/cities/1/weather/history"
```

La API inserta siete ciudades iniciales si el catálogo está vacío. La primera lectura meteorológica requiere ejecutar el Job y tener conexión con Open-Meteo.

Para compilar Angular en modo producción:

```bash
cd frontend
npm run build
```

## Spring Scheduling + Spring Batch

Scheduling decide cuándo se inicia la actualización. Batch define cómo se procesa:

1. **Reader:** pagina las ciudades habilitadas.
2. **Processor:** consulta Open-Meteo y transforma cada respuesta.
3. **Writer:** inserta o actualiza una lectura.
4. **JobRepository JDBC:** conserva instancias, parámetros, ejecuciones, pasos y contextos en tablas `BATCH_*` de SQLite.

El endpoint `POST /api/weather/refresh` arranca el mismo Job manualmente. Ambos mecanismos comparten el procesamiento y la persistencia.

## Documentación

- [Arquitectura](docs/ARQUITECTURA.md)
- [Guía de estudio](docs/GUIA_DE_ESTUDIO.md)
- [API REST](docs/API.md)
- [Base de datos](docs/BASE_DE_DATOS.md)

## Estructura principal

```text
ClimateAtlas/
├── backend/
│   ├── src/main/java/com/climateatlas/
│   │   ├── batch/
│   │   ├── config/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── service/
│   │   └── web/
│   ├── src/main/resources/application.yml
│   └── src/test/
├── frontend/src/app/
│   ├── components/
│   ├── models/
│   ├── pages/
│   └── services/
├── docs/
└── .github/workflows/build.yml
```

## Limitaciones conocidas

- El historial empieza a formarse después de instalar y ejecutar ClimateAtlas. No se generan datos meteorológicos ficticios ni se importa historial anterior.
- El proveedor y los mosaicos del mapa dependen de Internet.
- La aplicación no incorpora autenticación ni está endurecida para exponerse públicamente.
- Las pruebas automatizadas del backend están en `backend/src/test`; el frontend dispone de una compilación de producción, pero todavía no incluye un conjunto de pruebas unitarias.
- La base de datos SQLite es local y se excluye de Git.
