import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges, ViewChild } from '@angular/core';
import * as L from 'leaflet';
import { City } from '../../models/weather.models';

/**
 * Mapa Leaflet de México.
 * El mapa, la capa de marcadores y cada círculo tienen referencias separadas:
 * así se pueden reemplazar marcadores sin destruir la instancia cartográfica.
 */
@Component({
  selector: 'app-weather-map',
  standalone: true,
  template: `
    <div #mapContainer class="map-container" role="region"
         aria-label="Mapa meteorológico interactivo de México"></div>
    <div class="map-legend" aria-label="Leyenda de temperatura">
      <span><i class="cold"></i> Menos de 10 °C</span>
      <span><i class="mild"></i> 10 a 19.9 °C</span>
      <span><i class="warm"></i> 20 a 29.9 °C</span>
      <span><i class="hot"></i> 30 °C o más</span>
      <span><i class="unknown"></i> Sin lectura</span>
    </div>
  `,
  styles: [`
    .map-container { width: 100%; height: 510px; border-radius: 10px; background: #e5edf5; overflow: hidden; }
    .map-legend { display: flex; flex-wrap: wrap; gap: 10px 16px; padding: 12px 2px; color: #486581; font-size: 12px; }
    .map-legend span { display: inline-flex; align-items: center; gap: 6px; }
    .map-legend i { display: inline-block; width: 10px; height: 10px; border-radius: 50%; }
    .cold { background: #2471a3; } .mild { background: #2e86c1; }
    .warm { background: #e67e22; } .hot { background: #c0392b; }
    .unknown { background: #829ab1; }
  `]
})
export class WeatherMapComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() cities: City[] = [];
  @Output() citySelected = new EventEmitter<City>();
  @ViewChild('mapContainer') mapContainer?: ElementRef<HTMLDivElement>;

  private map?: L.Map;
  private markerLayer?: L.LayerGroup;

  ngAfterViewInit(): void {
    if (!this.mapContainer) return;

    this.map = L.map(this.mapContainer.nativeElement, {
      center: [23.6345, -102.5528],
      zoom: 4.5,
      minZoom: 3,
      maxZoom: 12,
      zoomControl: true
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.markerLayer = L.layerGroup().addTo(this.map);
    this.renderMarkers();
  }

  ngOnChanges(_changes: SimpleChanges): void {
    this.renderMarkers();
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private renderMarkers(): void {
    if (!this.map || !this.markerLayer) return;

    this.markerLayer.clearLayers();

    for (const city of this.cities) {
      const temperature = city.currentWeather?.temperature ?? null;
      const marker = L.circleMarker([city.latitude, city.longitude], {
        radius: 9,
        color: '#ffffff',
        weight: 2,
        fillColor: this.temperatureColor(temperature),
        fillOpacity: 0.95
      });

      marker.bindTooltip(`${city.name}, ${city.state}`);
      marker.bindPopup(this.createPopupContent(city));
      marker.on('click', () => this.citySelected.emit(city));
      marker.addTo(this.markerLayer);
    }
  }

  /**
   * Reglas explícitas de color:
   * <10 azul oscuro; [10,20) azul; [20,30) naranja; >=30 rojo; null gris.
   */
  private temperatureColor(temperature: number | null): string {
    if (temperature === null) return '#829ab1';
    if (temperature < 10) return '#2471a3';
    if (temperature < 20) return '#2e86c1';
    if (temperature < 30) return '#e67e22';
    return '#c0392b';
  }

  private createPopupContent(city: City): HTMLElement {
    // textContent evita interpretar el nombre de la ciudad como HTML.
    const wrapper = document.createElement('div');
    const heading = document.createElement('strong');
    heading.textContent = `${city.name}, ${city.state}`;
    wrapper.appendChild(heading);

    const description = document.createElement('p');
    description.style.margin = '6px 0 0';
    description.textContent = city.currentWeather
      ? `${city.currentWeather.temperature.toFixed(1)} °C · Humedad ${city.currentWeather.humidity.toFixed(0)} %`
      : 'Todavía no existe una lectura guardada.';
    wrapper.appendChild(description);
    return wrapper;
  }
}
