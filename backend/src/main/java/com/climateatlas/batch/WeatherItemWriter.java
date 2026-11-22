package com.climateatlas.batch;

import com.climateatlas.domain.WeatherReading;
import com.climateatlas.repository.WeatherReadingRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Upsert didáctico: busca city + measuredAt, actualiza si existe o inserta.
 * La restricción UNIQUE de SQLite protege también frente a carreras concurrentes.
 */
public class WeatherItemWriter implements ItemWriter<WeatherReading> {

    private final WeatherReadingRepository repository;

    public WeatherItemWriter(WeatherReadingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends WeatherReading> chunk) {
        List<WeatherReading> recordsToSave = new ArrayList<>();

        for (WeatherReading incoming : chunk) {
            repository.findByCity_IdAndMeasuredAt(
                    incoming.getCity().getId(), incoming.getMeasuredAt()
            ).ifPresentOrElse(existing -> {
                existing.updateMeasurement(incoming.getTemperature(), incoming.getHumidity(),
                        incoming.getWindSpeed(), incoming.getQueriedAt());
                recordsToSave.add(existing);
            }, () -> recordsToSave.add(incoming));
        }

        repository.saveAll(recordsToSave);
    }
}
