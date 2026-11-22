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

    /**
     * Constructor: recibe el puerto CityRepository. Así, el Reader obtiene ciudades sin conocer Spring Data ni SQLite.
     */
    public EnabledCityItemReader(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    /**
     * Se ejecuta al abrir el Step. Carga las ciudades habilitadas y recupera del ExecutionContext el índice guardado en un checkpoint para poder reanudar.
     */
    @Override
    public void open(ExecutionContext context) throws ItemStreamException {
        cities = cityRepository.findEnabled();
        currentIndex = context.getInt(CURRENT_INDEX_KEY, 0);
    }

    /**
     * Entrega la siguiente ciudad de la lista. Devuelve null cuando ya no quedan elementos, señal que indica a Spring Batch que terminó la lectura.
     */
    @Override
    public City read() {
        if (currentIndex >= cities.size()) {
            return null;
        }
        return cities.get(currentIndex++);
    }

    /**
     * Guarda el índice actual en ExecutionContext. Spring Batch utiliza este dato para continuar desde el punto correcto si el Step se reinicia.
     */
    @Override
    public void update(ExecutionContext context) throws ItemStreamException {
        context.putInt(CURRENT_INDEX_KEY, currentIndex);
    }

    /**
     * Libera la lista cargada y restablece el índice al cerrar el Reader.
     */
    @Override
    public void close() throws ItemStreamException {
        cities = List.of();
        currentIndex = 0;
    }
}
