# Base de datos SQLite

## Archivo

La conexión predeterminada utiliza `backend/data/climateatlas.db`. No hace falta instalar un servidor: Xerial JDBC abre el archivo.

Hibernate usa `spring.jpa.hibernate.ddl-auto=update` para crear o actualizar las tablas de entidades. El starter `spring-boot-starter-batch-jdbc` habilita el JobRepository JDBC; Spring Boot inicializa las tablas de metadatos de Spring Batch en el mismo DataSource SQLite.

## Tabla cities

| Columna | Tipo conceptual | Restricción | Significado |
|---|---|---|---|
| id | INTEGER | PK autogenerada | Identificador |
| name | VARCHAR(100) | NOT NULL | Nombre de ciudad |
| state | VARCHAR(100) | NOT NULL | Estado |
| latitude | DOUBLE | NOT NULL | Latitud decimal |
| longitude | DOUBLE | NOT NULL | Longitud decimal |
| enabled | BOOLEAN | NOT NULL | Participa en las actualizaciones |

Restricciones: UNIQUE(name, state) y un índice `idx_city_enabled` para el filtro por habilitación.

## Tabla weather_readings

| Columna | Tipo conceptual | Restricción | Significado |
|---|---|---|---|
| id | INTEGER | PK autogenerada | Identificador |
| city_id | INTEGER | FK NOT NULL | Ciudad relacionada |
| temperature | DOUBLE | NOT NULL | °C |
| humidity | DOUBLE | NOT NULL | Humedad relativa, % |
| wind_speed | DOUBLE | NOT NULL | km/h |
| measured_at | TIMESTAMP | NOT NULL | Instante de medición |
| queried_at | TIMESTAMP | NOT NULL | Instante de consulta del backend |

Restricciones: FK `city_id → cities.id`; UNIQUE(city_id, measured_at); índice `idx_weather_city_measured_at`.

## Relaciones

`City` representa el lado One-to-Many mediante `@OneToMany(mappedBy="city")`.

Cada `WeatherReading` pertenece a una ciudad mediante `@ManyToOne` y `@JoinColumn(name="city_id")`. El lado dueño es WeatherReading porque contiene la foreign key.

La cascada REMOVE de City elimina las lecturas relacionadas al borrar la ciudad.

## SQL equivalente para estudiar el modelo

El DDL exacto se genera al iniciar Hibernate. La estructura conceptual es:

```sql
CREATE TABLE cities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    enabled BOOLEAN NOT NULL,
    CONSTRAINT uk_city_name_state UNIQUE (name, state)
);

CREATE INDEX idx_city_enabled ON cities(enabled);

CREATE TABLE weather_readings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    city_id INTEGER NOT NULL,
    temperature DOUBLE NOT NULL,
    humidity DOUBLE NOT NULL,
    wind_speed DOUBLE NOT NULL,
    measured_at TIMESTAMP NOT NULL,
    queried_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_weather_city FOREIGN KEY (city_id) REFERENCES cities(id),
    CONSTRAINT uk_weather_city_measured_at UNIQUE (city_id, measured_at)
);

CREATE INDEX idx_weather_city_measured_at
    ON weather_readings(city_id, measured_at);
```

## Metadatos persistidos de Spring Batch

El JobRepository JDBC mantiene tablas de control en el mismo archivo:

- `BATCH_JOB_INSTANCE`: instancia lógica de un Job.
- `BATCH_JOB_EXECUTION`: intentos y estados.
- `BATCH_JOB_EXECUTION_PARAMS`: parámetros de ejecución.
- `BATCH_STEP_EXECUTION`: estado y métricas por Step.
- `BATCH_JOB_EXECUTION_CONTEXT` y `BATCH_STEP_EXECUTION_CONTEXT`: contexto necesario para controlar y reiniciar procesos.

Estas tablas no contienen datos meteorológicos; conservan la información de control de Batch. La inicialización se habilita en `application.yml` con `spring.batch.jdbc.initialize-schema: always` y usa el script SQLite incluido en Spring Batch.

## Consultas SQL de ejemplo

```sql
-- Ciudades habilitadas
SELECT id, name, state
FROM cities
WHERE enabled = 1
ORDER BY name;

-- Últimas lecturas de una ciudad
SELECT temperature, humidity, wind_speed, measured_at, queried_at
FROM weather_readings
WHERE city_id = 1
ORDER BY measured_at DESC;

-- Total de mediciones por ciudad
SELECT c.name, COUNT(w.id) AS reading_count
FROM cities c
LEFT JOIN weather_readings w ON w.city_id = c.id
GROUP BY c.id, c.name
ORDER BY c.name;

-- Esta consulta debe devolver cero filas
SELECT city_id, measured_at, COUNT(*) AS occurrences
FROM weather_readings
GROUP BY city_id, measured_at
HAVING COUNT(*) > 1;
```

## Inspección local

Con el ejecutable `sqlite3`:

```bash
sqlite3 backend/data/climateatlas.db
.tables
.schema cities
.schema weather_readings
SELECT * FROM cities;
```

El archivo local se excluye del control de versiones.
