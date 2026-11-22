package com.climateatlas.app.batch;

import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.model.WeatherReading;
import com.climateatlas.negocio.port.CityRepository;
import com.climateatlas.negocio.port.WeatherProvider;
import com.climateatlas.negocio.port.WeatherReadingRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/** Definición del Job; Reader, Processor y Writer usan tipos de negocio. */
@Configuration
public class WeatherBatchConfiguration {
    @Bean
    @StepScope
    public EnabledCityItemReader enabledCityReader(CityRepository cityRepository) {
        return new EnabledCityItemReader(cityRepository);
    }

    @Bean
    public WeatherItemProcessor weatherItemProcessor(WeatherProvider weatherProvider) {
        return new WeatherItemProcessor(weatherProvider);
    }

    @Bean
    public WeatherItemWriter weatherItemWriter(WeatherReadingRepository repository) {
        return new WeatherItemWriter(repository);
    }

    @Bean
    public Step weatherRefreshStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemStreamReader<City> enabledCityReader,
            WeatherItemProcessor weatherItemProcessor,
            WeatherItemWriter weatherItemWriter,
            @Value("${climate.weather.batch-chunk-size:5}") int chunkSize) {
        return new StepBuilder("weatherRefreshStep", jobRepository)
                .<City, WeatherReading>chunk(chunkSize)
                .transactionManager(transactionManager)
                .reader(enabledCityReader)
                .processor(weatherItemProcessor)
                .writer(weatherItemWriter)
                .build();
    }

    @Bean
    public Job weatherRefreshJob(JobRepository jobRepository, Step weatherRefreshStep) {
        return new JobBuilder("weatherRefreshJob", jobRepository)
                .start(weatherRefreshStep)
                .build();
    }
}
