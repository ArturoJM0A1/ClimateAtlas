# Backend — ClimateAtlas

El backend utiliza tres capas explícitas: `app`, `negocio` y `repository`.

## app — entrada y coordinación

Contiene:
- `app.web`: controladores REST.
- `app.dto`: contratos JSON y validación.
- `app.application`: casos de uso de aplicación y conversión de modelos a DTO.
- `app.batch`: configuración y coordinación de Spring Batch, Reader, Processor, Writer y Scheduling.
- `app.config`: composición de dependencias, CORS y datos iniciales.
- `app.exception`: traducción de excepciones a HTTP.

App no consulta entidades JPA directamente ni contiene SQL.

## negocio — dominio y reglas

Contiene:
- `negocio.model`: modelos Java puros.
- `negocio.service`: reglas del catálogo, historial y estadísticas.
- `negocio.port`: interfaces de persistencia y de acceso al proveedor meteorológico.
- `negocio.exception`: excepciones del dominio.

Negocio no importa Spring, Jakarta Persistence, Hibernate, DTO ni controladores.

## repository — persistencia y adaptadores

Contiene:
- `repository.jpa.entity`: entidades JPA.
- `repository.jpa`: interfaces Spring Data internas.
- `repository`: adaptadores que implementan los puertos de negocio.
- `repository.openmeteo`: cliente externo que implementa `WeatherProvider`.

Aquí quedan SQLite, Hibernate, JDBC y RestClient.

## Dependencias permitidas

```text
app ───────────────> negocio
repository ────────> negocio
app configura repository como implementación de los puertos
negocio ───────────> no depende de app ni repository
```

## Ejecutar

Desde esta carpeta:

```bash
mvn clean verify
mvn spring-boot:run
```

API local: `http://localhost:8080`. La base de datos se crea en `data/climateatlas.db`.
