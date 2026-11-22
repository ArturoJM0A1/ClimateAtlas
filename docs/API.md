# API REST de ClimateAtlas

Base URL local: `http://localhost:8080`. Cuerpos y respuestas JSON. Las fechas del historial utilizan ISO local `yyyy-MM-ddTHH:mm:ss`.

## Ciudades

### GET /api/cities

Devuelve ciudades ordenadas por nombre. Incluye `currentWeather` si ya existe una lectura.

Respuesta 200 (ejemplo de estructura; valores meteorológicos ilustrativos, no precargados):

```json
[
  {
    "id": 1,
    "name": "Monterrey",
    "state": "Nuevo León",
    "latitude": 25.6866,
    "longitude": -100.3161,
    "enabled": true,
    "currentWeather": null
  }
]
```

### GET /api/cities/{id}

Obtiene ciudad. Respuestas: 200 o 404.

### POST /api/cities

Registra una ciudad; inicia habilitada.

Solicitud:

```json
{
  "name": "León",
  "state": "Guanajuato",
  "latitude": 21.1221,
  "longitude": -101.68
}
```

Respuestas: 201 Created con cabecera Location; 400 por validación; 409 si se viola UNIQUE.

### PUT /api/cities/{id}

Reemplaza los datos editables. Usa el mismo JSON de POST. Respuestas: 200, 400, 404 o 409.

### PATCH /api/cities/{id}/enabled

Actualiza únicamente la habilitación.

```json
{ "enabled": false }
```

Respuestas: 200, 400 o 404.

### DELETE /api/cities/{id}

Elimina la ciudad y sus lecturas relacionadas. Respuestas: 204 o 404.

## Historial

### GET /api/cities/{id}/weather/history

Query parameters opcionales:

- `from`: fecha y hora inicial ISO, inclusiva.
- `to`: fecha y hora final ISO, inclusiva.

Ejemplo:

```text
GET /api/cities/1/weather/history?from=2026-11-15T00:00:00&to=2026-11-22T23:59:59
```

Sin fechas se consultan los últimos siete días. Si no hay datos se devuelve `[]`. Respuestas: 200, 400 por rango inválido, 404 si la ciudad no existe.

## Meteorología general

### GET /api/weather/statistics

Respuesta 200; los valores se calculan desde la base local:

```json
{
  "totalCities": 7,
  "enabledCities": 7,
  "totalReadings": 0,
  "averageCurrentTemperature": null
}
```

### POST /api/weather/refresh

Inicia el Job de Spring Batch manualmente. No requiere body. Respuesta 202 Accepted:

```json
{
  "executionId": 4,
  "jobName": "weatherRefreshJob",
  "status": "COMPLETED",
  "startTime": "2026-11-22T10:02:00",
  "endTime": "2026-11-22T10:02:08"
}
```

Los valores dependen de la ejecución real; el ejemplo documenta la forma del contrato.

## Errores

Ejemplo 404:

```json
{
  "timestamp": "2026-11-22T10:04:00-06:00",
  "status": 404,
  "error": "Not Found",
  "message": "No existe la ciudad con id 999",
  "path": "/api/cities/999"
}
```

| HTTP | Significado |
|---|---|
| 200 | Consulta o actualización correcta |
| 201 | Ciudad creada |
| 202 | Job aceptado/iniciado |
| 204 | Ciudad eliminada |
| 400 | DTO inválido o rango de fechas incorrecto |
| 404 | Ciudad inexistente |
| 409 | Restricción de integridad |
| 503 | Proveedor meteorológico o inicio del proceso no disponible |
