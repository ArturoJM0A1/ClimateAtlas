package com.climateatlas.app;

import com.climateatlas.negocio.port.CityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Prueba de integración de las capas, SQLite, JPA, Batch y datos iniciales. */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:./target/climateatlas-test.db",
        "climate.weather.initial-delay-ms=3600000"
})
class ClimateAtlasApplicationTest {
    @Autowired
    private CityRepository cityRepository;

    /**
     * Verifica la integración completa del contexto Spring. Comprueba que arranque la aplicación de prueba y que el inicializador haya guardado al menos siete ciudades en SQLite.
     */
    @Test
    void contextStartsAndSeedsInitialCitiesInSqlite() {
        assertTrue(cityRepository.count() >= 7,
                "El catálogo debe contener las siete ciudades iniciales.");
    }
}
