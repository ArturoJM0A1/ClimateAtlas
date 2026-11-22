# Guía de estudio de ClimateAtlas

La guía sigue las tres capas reales del backend: `app`, `negocio` y `repository`.

## 1. Entender las capas

### app

Abre `backend/src/main/java/com/climateatlas/app/web/CityController.java` y `app/application/CityApplicationService.java`.

El controlador traduce HTTP a llamadas de aplicación. El servicio de aplicación coordina el caso de uso y convierte modelos a DTO. App conoce la API, pero no decide reglas de dominio ni construye SQL.

### negocio

Abre `negocio/model/City.java`, `negocio/port/CityRepository.java` y `negocio/service/CityService.java`.

El modelo representa el dominio. El puerto declara qué necesita el negocio para persistir. El servicio ejecuta las reglas y depende solo del contrato. No hay anotaciones JPA ni imports de Spring en esta capa.

### repository

Abre `repository/CityRepositoryAdapter.java`, `repository/jpa/entity/CityEntity.java` y `repository/jpa/SpringDataCityJpaRepository.java`.

El adaptador implementa el puerto, convierte `City` a `CityEntity` y delega consultas a Spring Data. Repository conoce Hibernate y SQLite; negocio no.

## 2. Regla de dependencias

- app depende de negocio.
- repository depende de los puertos y modelos de negocio.
- app conecta las implementaciones de repository mediante configuración.
- negocio no depende de app ni repository.

Así puedes probar los servicios del dominio con mocks sin iniciar Spring ni una base de datos.

## 3. Java y orientación a objetos

### Clases, objetos y constructores

`negocio/model/City.java` declara una clase. Su constructor inicializa los datos de una instancia.

### Encapsulamiento

Los campos son privados. `updateDetails` y `setEnabled` expresan cambios permitidos sin permitir que otras capas modifiquen los atributos libremente.

### Interfaces y polimorfismo

`negocio.port.CityRepository` es un contrato. `repository.CityRepositoryAdapter` es su implementación. Negocio puede recibir otra implementación sin cambiar su servicio.

### Records

`negocio/model/WeatherStatistics.java` y los DTO de `app/dto` usan records. El primero expresa un resultado del dominio; los DTO expresan contratos HTTP.

### Optional, Streams y listas

`WeatherService` usa Optional cuando una ciudad aún no tiene lectura. Los Streams transforman colecciones de modelos sin exponer entidades JPA.

## 4. Spring Boot y REST

Abre `app/web/CityController.java`:
- GET obtiene recursos.
- POST crea.
- PUT actualiza datos editables.
- PATCH modifica una propiedad.
- DELETE elimina.

`@PathVariable` obtiene identificadores desde la ruta. `@RequestParam` lee filtros. `@RequestBody` deserializa JSON. `@Valid` ejecuta Bean Validation sobre el DTO.

`ApiExceptionHandler` convierte excepciones de negocio y persistencia a HTTP.

## 5. JPA y SQLite

Abre `repository/jpa/entity/CityEntity.java` y `WeatherReadingEntity.java`.

Las anotaciones `@Entity`, `@Id`, `@ManyToOne`, `@OneToMany` y `@JoinColumn` pertenecen a repository porque describen persistencia.

`SpringDataCityJpaRepository` extiende `JpaRepository`. Los adaptadores hacen el mapeo entre entidades JPA y modelos de negocio.

## 6. Puertos y adaptadores

`negocio.port.WeatherProvider` declara cómo solicitar datos meteorológicos, sin especificar HTTP.

`repository.openmeteo.OpenMeteoWeatherProvider` implementa ese contrato con RestClient.

El mismo patrón se aplica a CityRepository y WeatherReadingRepository.

## 7. Spring Batch

Abre los archivos de `app/batch`:
- Job: proceso completo.
- Step: fase.
- Reader: lee ciudades habilitadas.
- Processor: consulta el proveedor y transforma.
- Writer: guarda lecturas usando un puerto.
- JobRepository: persiste estados y checkpoints.

El Reader implementa ItemStreamReader y conserva el índice en ExecutionContext. El chunk se configura mediante `WEATHER_BATCH_CHUNK_SIZE`.

## 8. Angular y RxJS

Abre `frontend/src/app/services/dashboard-state.service.ts` y `pages/dashboard/dashboard.component.ts`.

BehaviorSubject conserva el último valor; combineLatest combina fuentes; map transforma emisiones; HttpClient devuelve Observables para solicitudes HTTP.

Leaflet administra el mapa y Chart.js representa las temperaturas.

## 9. Orden recomendado

1. `negocio/model`.
2. `negocio/port`.
3. `negocio/service`.
4. `repository/jpa/entity`.
5. `repository/jpa` y adaptadores.
6. `app/application`.
7. `app/web` y DTO.
8. `app/batch`.
9. Frontend.

Ejecuta `mvn clean verify` en backend y `npm run build` en frontend.
