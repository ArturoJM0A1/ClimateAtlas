package com.climateatlas.batch;

import com.climateatlas.domain.City;
import com.climateatlas.domain.WeatherReading;
import com.climateatlas.repository.CityRepository;
import com.climateatlas.repository.WeatherReadingRepository;
import com.climateatlas.service.OpenMeteoClient;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

/**
 * Job con un Step chunk-oriented: Reader -> Processor -> Writer.
 * Cada chunk se confirma en una transacción y el JobRepository registra el avance.
 */
@Configuration
public class WeatherBatchConfiguration {

    @Bean
    public RepositoryItemReader<City> enabledCityReader(CityRepository cityRepository) {
        return new RepositoryItemReaderBuilder<City>()
                .name("enabledCityReader")
                .repository(cityRepository)
                .methodName("findByEnabledTrue")
                .pageSize(5)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public WeatherItemProcessor weatherItemProcessor(OpenMeteoClient client) {
        return new WeatherItemProcessor(client);
    }

    @Bean
    public WeatherItemWriter weatherItemWriter(WeatherReadingRepository repository) {
        return new WeatherItemWriter(repository);
    }

    @Bean
    public Step weatherRefreshStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RepositoryItemReader<City> enabledCityReader,
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
