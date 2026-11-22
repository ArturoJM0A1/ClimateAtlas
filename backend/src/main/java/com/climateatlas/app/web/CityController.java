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

    public CityController(
            CityApplicationService cityApplicationService,
            WeatherApplicationService weatherApplicationService) {
        this.cityApplicationService = cityApplicationService;
        this.weatherApplicationService = weatherApplicationService;
    }

    @GetMapping
    public List<CityResponse> findAll() {
        return cityApplicationService.findAll();
    }

    @GetMapping("/{id}")
    public CityResponse findById(@PathVariable Long id) {
        return cityApplicationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CityResponse> create(@Valid @RequestBody CityRequest request) {
        CityResponse created = cityApplicationService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public CityResponse update(@PathVariable Long id, @Valid @RequestBody CityRequest request) {
        return cityApplicationService.update(id, request);
    }

    @PatchMapping("/{id}/enabled")
    public CityResponse updateEnabled(
            @PathVariable Long id, @Valid @RequestBody EnabledRequest request) {
        return cityApplicationService.updateEnabled(id, request.enabled());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cityApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

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
