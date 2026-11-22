package com.climateatlas.app.batch;

import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.port.CityRepository;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamException;
import org.springframework.batch.infrastructure.item.ItemStreamReader;

import java.util.List;

/**
 * Reader que usa el puerto de negocio, nunca una interfaz Spring Data.
 * Guarda su índice en ExecutionContext para soportar checkpoints de Step.
 */
public class EnabledCityItemReader implements ItemStreamReader<City> {
    private static final String CURRENT_INDEX_KEY = "enabledCityItemReader.currentIndex";

    private final CityRepository cityRepository;
    private List<City> cities = List.of();
    private int currentIndex;

    public EnabledCityItemReader(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public void open(ExecutionContext context) throws ItemStreamException {
        cities = cityRepository.findEnabled();
        currentIndex = context.getInt(CURRENT_INDEX_KEY, 0);
    }

    @Override
    public City read() {
        if (currentIndex >= cities.size()) {
            return null;
        }
        return cities.get(currentIndex++);
    }

    @Override
    public void update(ExecutionContext context) throws ItemStreamException {
        context.putInt(CURRENT_INDEX_KEY, currentIndex);
    }

    @Override
    public void close() throws ItemStreamException {
        cities = List.of();
        currentIndex = 0;
    }
}
