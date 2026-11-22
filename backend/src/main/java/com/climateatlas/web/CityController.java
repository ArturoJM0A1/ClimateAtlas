package com.climateatlas.web;

import com.climateatlas.dto.CityRequest;
import com.climateatlas.dto.CityResponse;
import com.climateatlas.dto.EnabledRequest;
import com.climateatlas.dto.WeatherReadingResponse;
import com.climateatlas.service.CityService;
import com.climateatlas.service.WeatherService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/** Endpoints REST del catálogo de ciudades y su historial meteorológico. */
@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;
    private final WeatherService weatherService;

    public CityController(CityService cityService, WeatherService weatherService) {
        this.cityService = cityService;
        this.weatherService = weatherService;
    }

    @GetMapping
    public List<CityResponse> findAll() {
        return cityService.findAll();
    }

    @GetMapping("/{id}")
    public CityResponse findById(@PathVariable Long id) {
        return cityService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CityResponse> create(@Valid @RequestBody CityRequest request) {
        CityResponse created = cityService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public CityResponse update(@PathVariable Long id, @Valid @RequestBody CityRequest request) {
        return cityService.update(id, request);
    }

    @PatchMapping("/{id}/enabled")
    public CityResponse updateEnabled(
            @PathVariable Long id, @Valid @RequestBody EnabledRequest request) {
        return cityService.updateEnabled(id, request.enabled());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cityService.delete(id);
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
        return weatherService.history(id, start, end);
    }
}
