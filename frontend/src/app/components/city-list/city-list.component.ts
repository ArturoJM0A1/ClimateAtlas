import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { City } from '../../models/weather.models';

/** Lista de ciudades; comunica selección al padre mediante un Output. */
@Component({
  selector: 'app-city-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section class="city-list" aria-label="Ciudades">
      <div class="list-heading">
        <h2>Ciudades</h2>
        <span>{{ cities.length }} resultados</span>
      </div>
      <p *ngIf="cities.length === 0" class="empty-state">
        No hay ciudades que coincidan con los filtros.
      </p>
      <article *ngFor="let city of cities; trackBy: trackById" class="city-card"
               [class.selected]="selectedCityId === city.id">
        <button class="city-select" type="button" (click)="citySelected.emit(city)">
          <span class="city-name">{{ city.name }}</span>
          <span class="city-state">{{ city.state }}</span>
          <span class="temperature" *ngIf="city.currentWeather as weather">
            {{ weather.temperature | number:'1.1-1' }} °C
          </span>
          <span class="no-reading" *ngIf="!city.currentWeather">Sin lectura</span>
        </button>
        <a class="detail-link" [routerLink]="['/cities', city.id]">Historial</a>
      </article>
    </section>
  `,
  styles: [`
    .list-heading { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
    h2 { margin: 0; font-size: 18px; color: #102a43; }
    .list-heading span { color: #627d98; font-size: 12px; }
    .city-card { display: grid; grid-template-columns: 1fr auto; gap: 8px; align-items: center; padding: 10px 12px; margin-bottom: 8px; border: 1px solid #d9e2ec; border-radius: 8px; background: #fff; }
    .city-card.selected { border-color: #2680c2; background: #f0f7ff; }
    .city-select { min-width: 0; display: grid; grid-template-columns: 1fr auto; text-align: left; border: 0; background: transparent; cursor: pointer; padding: 0; color: inherit; }
    .city-name { font-weight: 700; color: #102a43; }
    .city-state { grid-column: 1; color: #627d98; font-size: 12px; }
    .temperature { grid-column: 2; grid-row: 1; color: #102a43; font-weight: 700; }
    .no-reading { grid-column: 2; grid-row: 1; color: #829ab1; font-size: 12px; }
    .detail-link { color: #186faf; font-size: 12px; text-decoration: none; white-space: nowrap; }
    .detail-link:hover { text-decoration: underline; }
    .empty-state { color: #627d98; font-size: 13px; }
  `]
})
export class CityListComponent {
  @Input() cities: City[] = [];
  @Input() selectedCityId: number | null = null;
  @Output() citySelected = new EventEmitter<City>();

  trackById(_index: number, city: City): number {
    return city.id;
  }
}
