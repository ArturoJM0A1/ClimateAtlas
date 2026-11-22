# Guía de estudio de ClimateAtlas

Esta guía explica conceptos mediante archivos reales. Ábrelos mientras lees.

## 1. Java y estructura del lenguaje

### Clases, objetos y constructores

Empieza por `backend/src/main/java/com/climateatlas/domain/City.java` y `WeatherReading.java`.

Una clase declara un tipo; un objeto es una instancia. El constructor recibe los datos para formar un estado inicial. Los campos son privados: el resto del sistema no modifica libremente cada atributo. Los métodos `updateDetails` y `setEnabled` expresan cambios permitidos.

Los constructores protegidos sin argumentos permiten a JPA reconstruir entidades desde filas de la base de datos.

### Modificadores de acceso

- `private`: el miembro solo es accesible desde su propia clase.
- `public`: forma parte de la API visible desde otros paquetes.
- `protected`: permite acceso desde el paquete y subclases.

Expón el contrato mínimo que permita colaborar a las clases.

### Records y DTO

Abre `dto/CityRequest.java` y `dto/WeatherReadingResponse.java`. Un record declara un conjunto fijo de componentes y genera constructor, accessors y métodos de valor.

Un DTO transporta información. No equivale a una entidad JPA: la entidad tiene identidad y ciclo de persistencia; el DTO es un contrato de entrada o salida HTTP.

### Interfaces y polimorfismo

`repository/CityRepository.java` declara operaciones. Spring Data crea una implementación en ejecución.

`batch/WeatherItemProcessor.java` y `WeatherItemWriter.java` implementan interfaces de Spring Batch. El framework puede invocarlos sin depender de una clase concreta específica.

Eso es polimorfismo: una referencia con el tipo de una interfaz puede contener objetos de distintas clases que respetan ese contrato.

### Collections y Streams

En `InitialCitiesConfiguration.java`, `List.of(...)` crea una lista inmutable.

En `CityService.java`, `stream()`, `map()` y `toList()` transforman entidades en DTO. Un Stream describe operaciones sobre una secuencia; no es una colección que almacene por sí sola el resultado.

En `WeatherService.java`, `Optional` representa que una consulta puede no encontrar una fila. `Optional::stream` permite excluir los resultados vacíos al calcular promedios.

### Excepciones

Abre `exception/ApiExceptionHandler.java` y `service/OpenMeteoClient.java`.

Una excepción interrumpe el flujo normal y transfiere el control a un manejador. No captures excepciones para ocultarlas. El cliente externo traduce fallos de red o deserialización a `WeatherProviderException`; el manejador web convierte los errores conocidos a estados HTTP.

## 2. Spring Boot y REST

### Inyección de dependencias

`CityService` recibe `CityRepository` y `WeatherReadingRepository` por constructor. No los crea con `new`.

Spring construye los objetos e inyecta sus dependencias. Así pueden sustituirse los repositorios por mocks en pruebas.

### Controller, Service y Repository

- `CityController`: rutas HTTP, métodos, parámetros y respuestas.
- `CityService`: reglas de negocio del catálogo.
- `CityRepository`: persistencia.
- `City`: modelo persistente.
- `CityRequest` / `CityResponse`: contrato HTTP.

### Verbos HTTP

En `web/CityController.java`:

- GET obtiene recursos.
- POST crea recursos.
- PUT reemplaza los campos editables.
- PATCH modifica una parte, como `enabled`.
- DELETE elimina un recurso.

Un endpoint es la combinación de método y ruta. Por ejemplo, `PATCH /api/cities/3/enabled` modifica la habilitación de la ciudad 3.

### Parámetros, body y códigos

`@PathVariable` toma el id desde la ruta. `@RequestParam` lee filtros de historial como `from` y `to`. `@RequestBody` deserializa JSON a un DTO.

Códigos HTTP usados: 200 OK, 201 Created, 202 Accepted, 204 No Content, 400 Bad Request, 404 Not Found, 409 Conflict y 503 Service Unavailable.

### Validación

`CityRequest` usa Bean Validation: `@NotBlank`, `@Size`, `@DecimalMin` y `@DecimalMax`. El controlador agrega `@Valid`. Si falla la validación se responde 400.

## 3. JPA, Hibernate y SQLite

Abre `domain/City.java` y `domain/WeatherReading.java`.

JPA define anotaciones y contratos de persistencia. Hibernate implementa esos contratos y genera SQL. SQLite ejecuta el SQL y guarda las filas en el archivo.

`@Entity` marca la clase persistente. `@Id` marca la clave primaria. `@GeneratedValue` delega la generación del identificador. `@Column(nullable=false)` expresa NOT NULL.

La relación One-to-Many está en City mediante `@OneToMany(mappedBy="city")`. La relación Many-to-One está en WeatherReading mediante `@ManyToOne` y `@JoinColumn(name="city_id")`.

La foreign key vincula `weather_readings.city_id` con `cities.id`. La restricción UNIQUE `city_id + measured_at` impide lecturas duplicadas del mismo instante.

### Spring Data JPA Repository

`CityRepository` extiende `JpaRepository<City, Long>`. Métodos como `findAllByOrderByNameAsc` y `existsByNameIgnoreCaseAndStateIgnoreCase` son consultas derivadas: Spring Data interpreta su nombre y construye la consulta.

## 4. HTTP y Open-Meteo

Abre `config/RestClientConfiguration.java` y `service/OpenMeteoClient.java`.

RestClient construye una petición GET. Los query parameters incluyen latitud, longitud, campos actuales y zona horaria. `retrieve().body(...)` ejecuta la solicitud y solicita a Jackson convertir JSON al record indicado.

El JSON externo no se persiste como entidad directamente. Primero se deserializa en `OpenMeteoResponse`; el processor construye la entidad interna `WeatherReading`.

Los timeouts limitan la espera por conexión y respuesta. Una respuesta incompleta se trata como error, no como una medición válida.

## 5. Spring Batch

Abre:

- `batch/WeatherBatchConfiguration.java`
- `batch/WeatherItemProcessor.java`
- `batch/WeatherItemWriter.java`
- `service/WeatherJobService.java`
- `batch/WeatherUpdateScheduler.java`

Un Job representa el proceso completo. Un Step representa una fase. Un Step chunk-oriented agrupa elementos hasta alcanzar el tamaño de bloque y los escribe dentro de una transacción.

- Reader: obtiene ciudades habilitadas de forma paginada.
- Processor: consulta Open-Meteo y transforma City en WeatherReading.
- Writer: persiste el bloque.
- JobRepository: registra instancias, ejecuciones, pasos y contextos.

En Spring Batch 6 se utiliza `JobOperator.start(Job, JobParameters)`. El parámetro `requestedAt` distingue las instancias de cada actualización.

Cuando Processor devuelve null, Batch filtra ese elemento. ClimateAtlas lo usa para que un fallo de una ciudad no interrumpa las demás.

### Scheduling no reemplaza Batch

`@Scheduled` determina cuándo se inicia el Job. Batch procesa y registra el trabajo. Son responsabilidades diferentes.

## 6. Angular y TypeScript

Abre `frontend/src/main.ts`, `app/app.config.ts` y `app/app.routes.ts`.

La aplicación inicia con `bootstrapApplication`. Los componentes son standalone y declaran dependencias en `imports`.

En `app.routes.ts`, Angular Router asocia URL y componentes. El segmento `:id` representa un parámetro de ruta.

En `models/weather.models.ts`, las interfaces describen la forma de las respuestas. Las interfaces desaparecen al compilar JavaScript; ayudan a verificar tipos durante desarrollo.

### Binding y comunicación

En `city-list.component.ts`:

- `@Input()` recibe la lista de ciudades.
- `@Output()` emite la ciudad seleccionada.
- `EventEmitter` comunica el evento al componente padre.
- `*ngFor` repite elementos.
- `*ngIf` muestra contenido condicional.
- `{{ ... }}` interpola valores.

El Dashboard conecta el evento del hijo con el servicio de estado.

### Servicios e inyección

`CityApiService` y `WeatherApiService` encapsulan HttpClient. `providedIn: 'root'` registra los servicios en el inyector raíz.

## 7. RxJS y estado compartido

Abre `services/dashboard-state.service.ts` y `pages/dashboard/dashboard.component.ts`.

Un Observable representa una secuencia de valores en el tiempo. Una Promise resuelve un único valor o error. HttpClient devuelve Observables; la petición HTTP comienza al suscribirse.

- `BehaviorSubject`: conserva último valor y lo emite a suscriptores nuevos.
- `asObservable()`: expone el flujo sin publicar `next`.
- `combineLatest`: combina últimas emisiones.
- `map`: transforma valores.
- `tap`: realiza efectos laterales.
- `catchError`: recupera de un error con otro Observable.
- `switchMap`: cambia al Observable interno más reciente.

Los componentes consumen los flujos con `async` pipe o solicitudes HTTP finitas. En flujos que no terminan, utiliza `takeUntilDestroyed` o `async` para evitar fugas de memoria.

## 8. Leaflet y Chart.js

`weather-map.component.ts` crea `L.Map`, añade la capa de teselas y conserva marcadores en LayerGroup. Cada marcador usa latitud y longitud de la ciudad. Los intervalos de temperatura están definidos en `temperatureColor`.

`weather-chart.component.ts` crea una gráfica de línea con Chart.js. Antes de recrearla llama a `destroy()` para limpiar listeners y referencias.

## 9. Pruebas y lectura recomendada

`backend/src/test/java/com/climateatlas/service/CityServiceTest.java` usa JUnit y Mockito. Los repositorios son mocks, por lo que estas pruebas no necesitan SQLite ni Open-Meteo.

Orden sugerido:

1. `domain/City.java` y `WeatherReading.java`.
2. Repositorios JPA.
3. DTO de entrada y salida.
4. `CityService.java` y `CityController.java`.
5. `OpenMeteoClient.java`.
6. Archivos de `batch/` y `WeatherJobService.java`.
7. Modelos y servicios Angular.
8. Dashboard, lista, mapa, filtros y gráfica.

Ejecuta `mvn test` desde backend y `npm run build` desde frontend.
