package com.climateatlas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada. @SpringBootApplication activa la configuración automática
 * y el escaneo de componentes. @EnableScheduling registra los métodos
 * anotados con @Scheduled.
 */
@SpringBootApplication
@EnableScheduling
public class ClimateAtlasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimateAtlasApplication.class, args);
    }
}
