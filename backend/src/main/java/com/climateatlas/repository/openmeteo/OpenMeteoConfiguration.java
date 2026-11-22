package com.climateatlas.repository.openmeteo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import java.net.http.HttpClient;
import java.time.Duration;

/** Configuración HTTP interna del adaptador Open-Meteo. */
@Configuration
public class OpenMeteoConfiguration {
    /**
     * Construye el cliente HTTP que utilizará el adaptador Open-Meteo. Define la URL base, un máximo de cinco segundos para conectar y diez segundos para recibir la respuesta.
     */
    @Bean
    public RestClient openMeteoRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5)).build();
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        return RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .requestFactory(requestFactory)
                .build();
    }
}
