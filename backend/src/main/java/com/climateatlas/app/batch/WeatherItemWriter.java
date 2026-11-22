package com.climateatlas.app.batch;

import com.climateatlas.negocio.model.WeatherReading;
import com.climateatlas.negocio.port.WeatherReadingRepository;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

/** Writer que persiste mediante el puerto de negocio, sin conocer JPA. */
public class WeatherItemWriter implements ItemWriter<WeatherReading> {
    private final WeatherReadingRepository repository;

    public WeatherItemWriter(WeatherReadingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends WeatherReading> chunk) {
        List<WeatherReading> readingsToSave = new ArrayList<>();
        for (WeatherReading incoming : chunk) {
            repository.findByCityIdAndMeasuredAt(
                    incoming.getCity().getId(), incoming.getMeasuredAt()
            ).ifPresentOrElse(existing -> {
                existing.updateMeasurement(incoming.getTemperature(), incoming.getHumidity(),
                        incoming.getWindSpeed(), incoming.getQueriedAt());
                readingsToSave.add(existing);
            }, () -> readingsToSave.add(incoming));
        }
        repository.saveAll(readingsToSave);
    }
}
