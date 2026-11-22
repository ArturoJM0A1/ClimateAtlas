package com.climateatlas.service;

import com.climateatlas.dto.CityRequest;
import com.climateatlas.exception.ResourceNotFoundException;
import com.climateatlas.repository.CityRepository;
import com.climateatlas.repository.WeatherReadingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del servicio sin arrancar Spring ni SQLite. */
@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock private CityRepository cityRepository;
    @Mock private WeatherReadingRepository weatherReadingRepository;
    @InjectMocks private CityService cityService;

    @Test
    void createRejectsADuplicateCity() {
        CityRequest request = new CityRequest("Pachuca", "Hidalgo", 20.10, -98.75);
        when(cityRepository.existsByNameIgnoreCaseAndStateIgnoreCase("Pachuca", "Hidalgo"))
                .thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> cityService.create(request));
    }

    @Test
    void findByIdRejectsAnUnknownCity() {
        when(cityRepository.findById(999L)).thenReturn(java.util.Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cityService.findById(999L));
    }
}
