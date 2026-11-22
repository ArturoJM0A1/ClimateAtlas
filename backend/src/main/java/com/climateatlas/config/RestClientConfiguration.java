package com.climateatlas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import java.net.http.HttpClient;
import java.time.Duration;

/** Configura conexión y lectura con timeout para el cliente Open-Meteo. */
@Configuration
public class RestClientConfiguration {

    @Bean
    public RestClient openMeteoRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        return builder.baseUrl("https://api.open-meteo.com")
                .requestFactory(requestFactory)
                .build();
    }
}
