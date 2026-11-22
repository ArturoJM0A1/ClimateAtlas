package com.climateatlas.app.web;

import com.climateatlas.app.application.CityApplicationService;
import com.climateatlas.app.application.WeatherApplicationService;
import com.climateatlas.app.dto.CityRequest;
import com.climateatlas.app.dto.CityResponse;
import com.climateatlas.app.dto.EnabledRequest;
import com.climateatlas.app.dto.WeatherReadingResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/** Adaptador REST: traduce solicitudes HTTP a casos de uso de la capa app. */
@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CityApplicationService cityApplicationService;
    private final WeatherApplicationService weatherApplicationService;

    /**
     * Constructor: recibe los servicios de aplicación. El controlador no implementa reglas de negocio ni accede directamente a repositorios.
     */
    public CityController(
            CityApplicationService cityApplicationService,
            WeatherApplicationService weatherApplicationService) {
        this.cityApplicationService = cityApplicationService;
        this.weatherApplicationService = weatherApplicationService;
    }

    /**
     * Atiende GET /api/cities y devuelve el catálogo de ciudades en formato de respuesta.
     */
    @GetMapping
    public List<CityResponse> findAll() {
        return cityApplicationService.findAll();
    }

    /**
     * Atiende GET /api/cities/{id} y devuelve la ciudad identificada por la variable de ruta.
     */
    @GetMapping("/{id}")
    public CityResponse findById(@PathVariable Long id) {
        return cityApplicationService.findById(id);
    }

    /**
     * Atiende POST /api/cities. Valida el cuerpo, crea la ciudad y responde HTTP 201 Created; la cabecera Location señala la URL del recurso creado.
     */
    @PostMapping
    public ResponseEntity<CityResponse> create(@Valid @RequestBody CityRequest request) {
        CityResponse created = cityApplicationService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Atiende PUT /api/cities/{id}. Reemplaza los datos editables de la ciudad usando el DTO validado.
     */
    @PutMapping("/{id}")
    public CityResponse update(@PathVariable Long id, @Valid @RequestBody CityRequest request) {
        return cityApplicationService.update(id, request);
    }

    /**
     * Atiende PATCH /api/cities/{id}/enabled y modifica solo el indicador enabled.
     */
    @PatchMapping("/{id}/enabled")
    public CityResponse updateEnabled(
            @PathVariable Long id, @Valid @RequestBody EnabledRequest request) {
        return cityApplicationService.updateEnabled(id, request.enabled());
    }

    /**
     * Atiende DELETE /api/cities/{id}. Elimina el recurso y responde HTTP 204 No Content cuando la operación termina correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cityApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atiende la consulta del historial meteorológico de una ciudad. Si no se envían fechas, utiliza por defecto los últimos siete días; si solo se envía un límite, calcula el otro.
     */
    @GetMapping("/{id}/weather/history")
    public List<WeatherReadingResponse> history(
            @PathVariable Long id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        LocalDateTime end = to == null ? LocalDateTime.now().plusMinutes(1) : to;
        LocalDateTime start = from == null ? end.minusDays(7) : from;
        return weatherApplicationService.history(id, start, end);
    }
}
