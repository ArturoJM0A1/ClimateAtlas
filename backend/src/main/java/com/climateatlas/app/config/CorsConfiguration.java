package com.climateatlas.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuración CORS de la API para Angular en desarrollo local. */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    /**
     * Define qué origen web puede llamar a la API desde un navegador. En esta configuración se permite el frontend Angular local en el puerto 4200, con los métodos HTTP indicados y cualquier encabezado.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
