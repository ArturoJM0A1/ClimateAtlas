# Arquitectura de ClimateAtlas

## 1. Vista general

ClimateAtlas se divide en dos procesos independientes:

- **Backend**: API REST Java con Spring Boot. Integra SQLite, Open-Meteo, Spring Batch y Spring Scheduling.
- **Frontend**: aplicación Angular. Consulta la API, dibuja el mapa y representa el historial.

El navegador no consulta Open-Meteo directamente. Toda lectura meteorológica pasa por el backend para mantener la persistencia y la regla de unicidad en un solo lugar.

```text
┌──────────────────────────────┐
│ Angular                      │
│ Dashboard / mapa / historial │
│ Router / HttpClient / RxJS   │
└──────────────┬───────────────┘
               │ HTTP /api
               ▼
┌──────────────────────────────┐
│ Spring Boot                  │
│ Controllers → Services       │
│       → Spring Data JPA      │
│       → Hibernate            │
└──────────────┬───────────────┘
               │ JDBC
               ▼
        ┌────────────────┐
        │ SQLite         │
        │ cities         │
        │ weather_readings│
        │ BATCH_*        │
        └────────────────┘

Spring Scheduling ──inicia──> Spring Batch Job
                                  │
                           Reader / Processor
                                  │
                            Open-Meteo HTTP
                                  │
                                Writer
                                  │
                                SQLite
```

## 2. Backend por responsabilidades

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Web | `web` | Recibir HTTP, leer parámetros y devolver DTO. |
| Servicio | `service` | Coordinar reglas de negocio y consultas. |
| Persistencia | `repository` | Consultas derivadas y CRUD JPA. |
| Modelo | `domain` | Entidades que Hibernate almacena. |
| Contratos HTTP | `dto` | Tipos de entrada y salida de la API. |
| Batch | `batch` | Reader, Processor, Writer, Job y Step. |
| Configuración | `config` | Cliente HTTP, CORS y ciudades iniciales. |
| Errores | `exception` | Excepciones de dominio y respuestas de error. |

Los controladores no construyen SQL ni llaman directamente a Open-Meteo. Los repositorios no deciden códigos HTTP.

## 3. Flujo: Angular → API

1. Angular Router selecciona Dashboard o CityDetail.
2. El componente llama a un servicio Angular.
3. El servicio utiliza HttpClient y devuelve un Observable.
4. El componente o el servicio de estado consume ese Observable.
5. La solicitud llega a un controlador Spring MVC.
6. Spring valida el DTO y entrega el trabajo al servicio.
7. El servicio consulta el repositorio.
8. El controlador devuelve DTO como JSON.

En desarrollo, `frontend/proxy.conf.json` reenvía `/api` a `http://localhost:8080`.

## 4. Flujo: Open-Meteo → SQLite

1. `WeatherUpdateScheduler` se activa según `climate.weather.fixed-delay-ms`.
2. El scheduler llama a `WeatherJobService.start()`.
3. `JobOperator` inicia `weatherRefreshJob` con un parámetro identificador.
4. `enabledCityReader` pagina las ciudades habilitadas desde `CityRepository`.
5. `WeatherItemProcessor` solicita temperatura, humedad relativa, viento y hora de medición.
6. `OpenMeteoClient` llama a `https://api.open-meteo.com/v1/forecast`.
7. Jackson convierte el JSON a records Java.
8. El Processor crea un `WeatherReading`. Si el proveedor falla para una ciudad, registra el error y devuelve null para filtrar ese elemento.
9. `WeatherItemWriter` busca la combinación ciudad + fecha. Actualiza la fila si ya existe o crea una nueva.
10. La clave UNIQUE de SQLite impide duplicados aunque dos escrituras coincidan.

La escritura se agrupa en chunks. El tamaño del bloque se configura mediante `WEATHER_BATCH_CHUNK_SIZE`.

## 5. Scheduling y Batch tienen responsabilidades distintas

**Spring Scheduling** responde “¿cuándo se inicia?”. El método `@Scheduled` invoca el servicio que arranca el Job.

**Spring Batch** responde “¿cómo se procesa el conjunto?”. Administra Reader → Processor → Writer, chunks, estados y metadatos.

El endpoint `POST /api/weather/refresh` inicia el mismo Job manualmente. No existe una segunda implementación de la lógica meteorológica.

## 6. SQLite

El archivo predeterminado está en `backend/data/climateatlas.db`. Hibernate gestiona `cities` y `weather_readings`; Spring Batch inicializa sus tablas de metadatos `BATCH_*` en el mismo archivo.

La aplicación usa `org.hibernate.community.dialect.SQLiteDialect` y el driver JDBC de Xerial. Las claves foráneas se activan en cada conexión.

La carpeta `backend/data` se versiona solo mediante `.gitkeep`; la base de datos está excluida con `.gitignore`.

## 7. Angular y estado

`DashboardStateService` contiene Subjects privados y publica observables de solo lectura: `cities$`, `selectedCity$`, `loading$` y `error$`.

El Dashboard combina el Observable de ciudades con los cambios de búsqueda y filtro. Los hijos reciben datos mediante Inputs y notifican selección mediante Outputs.

Flujo de selección:

`CityList / WeatherMap → EventEmitter → Dashboard → DashboardStateService → BehaviorSubject → componentes suscritos`.

## 8. Límites actuales

- El historial empieza a acumularse al ejecutar el Job. No se genera histórico ficticio.
- Las lecturas dependen de Open-Meteo y la conexión de red.
- El mapa usa mosaicos públicos de OpenStreetMap y requiere Internet.
- La API no incorpora autenticación; está pensada para aprendizaje local.
- Se configuran timeouts HTTP, pero no reintentos automáticos.
