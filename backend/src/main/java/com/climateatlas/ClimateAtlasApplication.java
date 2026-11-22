package com.climateatlas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Bootstrap del backend.
 *
 * Se mantiene en el paquete raíz para que Spring Boot descubra automáticamente
 * los componentes, entidades JPA y repositorios de las capas app, negocio y repository.
 */
@SpringBootApplication
@EnableScheduling
public class ClimateAtlasApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClimateAtlasApplication.class, args);
    }
}
