# ClimateAtlas

Aplicación web para consultar el tiempo de ciudades de México, conservar lecturas en SQLite y explorar su ubicación e historial desde un mapa.

> Proyecto educativo. La primera versión implementa una arquitectura sencilla y explícita: Angular consume una API REST; Spring Boot consulta Open-Meteo y persiste las lecturas; Spring Batch procesa las ciudades en bloques; Spring Scheduling inicia el trabajo periódicamente.

## Tecnologías

| Área | Tecnología |
|---|---|
| Backend | Java 25, Spring Boot 4.2.0, Spring Web MVC, Spring Data JPA, Hibernate, Spring Batch 6.1.0, Spring Scheduling, Maven |
| Base de datos | SQLite mediante Xerial JDBC |
| Frontend | Angular 22, TypeScript 6, RxJS 7, Angular Router, HttpClient |
| Mapa | Leaflet 1.9 |
| Gráficas | Chart.js 4 |
| Datos meteorológicos | Open-Meteo |

Las versiones principales se fijan en los archivos de construcción. Spring Boot administra las versiones compatibles de sus módulos Spring. Referencias oficiales: [Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html), [Spring Batch](https://docs.spring.io/spring-batch/reference/index.html), [Angular: versiones](https://angular.dev/reference/versions).

## Requisitos previos

- Git.
- JDK 25 (comprueba con `java -version`).
- Apache Maven 3.6.3 o posterior (comprueba con `mvn -version`).
- Node.js 26 LTS y npm (comprueba con `node -v` y `npm -v`).
- Conexión a Internet para consultar Open-Meteo y descargar los mosaicos del mapa.

No se necesita instalar SQLite como servidor. El controlador JDBC crea y utiliza un archivo local.

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/ArturoJM0A1/ClimateAtlas.git
cd ClimateAtlas
```

### 2. Iniciar el backend

Desde la raíz del repositorio:

```bash
cd backend
mvn clean test
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`. El archivo de base de datos se crea en `backend/data/climateatlas.db`. La carpeta se crea automáticamente al iniciar y no debe versionarse.

Configuración principal en `backend/src/main/resources/application.yml`:

- `CLIMATE_DB_PATH`: ruta del archivo SQLite.
- `WEATHER_REFRESH_DELAY_MS`: intervalo de actualización automática en milisegundos.
- `WEATHER_BATCH_CHUNK_SIZE`: cantidad de ciudades procesadas por bloque.
- `SERVER_PORT`: puerto HTTP del backend.

Los valores por defecto permiten ejecutar el proyecto localmente sin crear un archivo `.env`.

### 3. Iniciar el frontend

En otra terminal, desde la raíz del repositorio:

```bash
cd frontend
npm install
npm start
```

Abre [http://localhost:4200](http://localhost:4200). El proxy de desarrollo dirige las peticiones `/api` a `http://localhost:8080`.

También puedes compilar el frontend:

```bash
npm run build
```

## Cómo comprobar el funcionamiento

1. Confirma que Spring Boot indique que la aplicación arrancó en el puerto 8080.
2. Consulta `http://localhost:8080/api/cities`. Deben aparecer las siete ciudades iniciales.
3. Consulta `http://localhost:8080/api/weather/statistics`.
4. Ejecuta una actualización manual:

```bash
curl -X POST http://localhost:8080/api/weather/refresh
```

5. Consulta una ciudad y sus lecturas:

```bash
curl http://localhost:8080/api/cities/1
curl "http://localhost:8080/api/cities/1/weather/history"
```

6. Abre el frontend y selecciona una ciudad en la lista o en el mapa.

La primera consulta a Open-Meteo requiere conexión a Internet. Si no se ha podido obtener una lectura, la interfaz muestra un estado sin datos; no se generan temperaturas ficticias.

## Spring Batch y Scheduling

La actualización meteorológica se procesa como un Job de Spring Batch:

1. **Reader:** obtiene las ciudades habilitadas desde SQLite.
2. **Processor:** consulta Open-Meteo y convierte su respuesta en una lectura meteorológica.
3. **Writer:** inserta o actualiza la lectura, respetando la unicidad de ciudad y fecha de medición.
4. **JobRepository:** conserva los metadatos de las ejecuciones de Spring Batch en el mismo archivo SQLite.

Spring Scheduling dispara el Job con la periodicidad configurable. El endpoint `POST /api/weather/refresh` permite iniciarlo manualmente. Un fallo de una ciudad se registra sin convertirlo en un dato meteorológico inventado.

## Documentación

- [Arquitectura](docs/ARQUITECTURA.md)
- [Guía de estudio](docs/GUIA_DE_ESTUDIO.md)
- [API REST](docs/API.md)
- [Base de datos](docs/BASE_DE_DATOS.md)

## Estructura

```text
ClimateAtlas/
├── backend/       # API, persistencia, cliente Open-Meteo y Spring Batch
├── frontend/      # Angular, mapa, filtros y gráficas
└── docs/          # Arquitectura y documentación de estudio
```

## Notas y limitaciones

- Open-Meteo es un servicio externo: su disponibilidad y límites de uso no dependen de ClimateAtlas.
- El historial comienza a acumularse desde las ejecuciones que se realicen después de instalar el proyecto. No se fabrican mediciones históricas.
- La base de datos local se excluye del control de versiones.
- Las pruebas automatizadas deben ejecutarse con `mvn test` y, para el frontend, `npm test` cuando se haya configurado un navegador de pruebas.
