# Arquitectura de ClimateAtlas

## 1. Capas del backend

ClimateAtlas utiliza una arquitectura de tres capas: `app`, `negocio` y `repository`.

```text
┌──────────────────────────────────────────────────┐
│ APP                                              │
│ Controllers · DTO · Application Services        │
│ Configuración · Spring Batch · Scheduling      │
└──────────────────────┬───────────────────────────┘
                       │ invoca casos de uso
                       ▼
┌──────────────────────────────────────────────────┐
│ NEGOCIO                                          │
│ Modelos puros · Servicios · Reglas              │
│ Puertos: CityRepository, WeatherProvider, etc.  │
└──────────────────────┬───────────────────────────┘
                       ▲ implementa puertos
                       │
┌──────────────────────┴───────────────────────────┐
│ REPOSITORY                                       │
│ Adaptadores JPA · Spring Data · SQLite           │
│ Adaptador HTTP de Open-Meteo                     │
└──────────────────────────────────────────────────┘
```

### app — aplicación

- `app.web`: controladores REST.
- `app.dto`: contratos JSON de entrada y salida.
- `app.application`: coordinación de casos de uso y conversión de modelos a DTO.
- `app.batch`: Job, Step, Reader, Processor, Writer, servicio de inicio y scheduler.
- `app.config`: composición de dependencias, CORS y datos iniciales.
- `app.exception`: traducción de errores a HTTP.

Los controladores no ejecutan SQL ni contienen reglas de dominio.

### negocio — dominio y reglas

- `negocio.model`: modelos Java puros `City`, `WeatherReading` y `WeatherStatistics`.
- `negocio.service`: reglas del catálogo, historial y estadísticas.
- `negocio.port`: contratos que negocio necesita para persistir y consultar clima.
- `negocio.exception`: excepciones del dominio.

Negocio no importa Spring, Hibernate, Jakarta Persistence, entidades JPA ni DTO HTTP.

### repository — persistencia e integraciones

- `repository.jpa.entity`: entidades de base de datos.
- `repository.jpa`: interfaces Spring Data.
- `repository`: adaptadores que implementan los puertos y traducen modelos a entidades.
- `repository.openmeteo`: adaptador HTTP que implementa `WeatherProvider`.

La tecnología SQLite, las consultas JPA y RestClient quedan aisladas aquí.

## 2. Regla de dependencias

```text
app ───────────────> negocio
repository ────────> negocio
app configura repository como implementación de los puertos
negocio ───────────> no depende de app ni repository
```

## 3. Flujo REST

1. Angular envía una petición a `/api`.
2. `app.web` recibe la ruta y valida DTO.
3. `app.application` coordina el caso de uso.
4. `negocio.service` ejecuta las reglas.
5. El servicio usa interfaces definidas en `negocio.port`.
6. Un adaptador de `repository` traduce el modelo a JPA y ejecuta la consulta.
7. App transforma el resultado a DTO y el controlador devuelve JSON.

## 4. Flujo Spring Batch

1. `WeatherUpdateScheduler` inicia el Job periódicamente.
2. `WeatherJobService` llama a `JobOperator`.
3. `EnabledCityItemReader` consulta el puerto `CityRepository`.
4. `WeatherItemProcessor` invoca `WeatherProvider`.
5. `OpenMeteoWeatherProvider` consulta Open-Meteo y traduce su JSON a `WeatherData`.
6. El Processor crea un modelo `WeatherReading`.
7. `WeatherItemWriter` usa el puerto `WeatherReadingRepository`.
8. El adaptador JPA guarda la lectura en SQLite.
9. JobRepository JDBC registra instancias, ejecuciones y checkpoints en las tablas `BATCH_*`.

## 5. Modelos y entidades

`negocio.model.City` expresa la ciudad dentro del dominio. `repository.jpa.entity.CityEntity` expresa su representación persistente. El adaptador traduce entre ambas.

El mismo criterio se aplica a WeatherReading. Cambiar Hibernate o SQLite no obliga a modificar las reglas de negocio.

## 6. Verificación

GitHub Actions ejecuta build, pruebas unitarias, prueba de arranque con SQLite y compilación de Angular. Las consultas meteorológicas en vivo dependen de Open-Meteo.
