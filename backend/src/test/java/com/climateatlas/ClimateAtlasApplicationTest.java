package com.climateatlas;

import com.climateatlas.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integración mínima: levanta el contexto completo de Spring Boot
 * sobre un archivo SQLite dentro de target y comprueba que se insertan las
 * ciudades iniciales. También verifica indirectamente que JPA y Batch pueden
 * crear sus esquemas e instanciar los componentes del Job.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:./target/climateatlas-test.db",
        "climate.weather.initial-delay-ms=3600000"
})
class ClimateAtlasApplicationTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    void contextStartsAndSeedsInitialCitiesInSqlite() {
        assertTrue(cityRepository.count() >= 7,
                "El catálogo inicial debe contener las siete ciudades configuradas.");
    }
}
