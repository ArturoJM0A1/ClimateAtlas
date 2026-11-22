package com.climateatlas.negocio.service;

import com.climateatlas.negocio.exception.ResourceNotFoundException;
import com.climateatlas.negocio.port.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de reglas de negocio sin iniciar Spring ni SQLite. */
@ExtendWith(MockitoExtension.class)
class CityServiceTest {
    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    @Test
    void createRejectsADuplicateCity() {
        when(cityRepository.existsByNameAndState("Pachuca", "Hidalgo")).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> cityService.create("Pachuca", "Hidalgo", 20.10, -98.75));
    }

    @Test
    void findByIdRejectsAnUnknownCity() {
        when(cityRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> cityService.findById(999L));
    }
}
