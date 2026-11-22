import { Component, EventEmitter, Input, Output } from '@angular/core';

/** Filtros controlados por el componente Dashboard que los contiene. */
@Component({
  selector: 'app-weather-filters',
  standalone: true,
  template: `
    <section class="filter-bar" aria-label="Filtros de ciudades">
      <label class="search-field">
        <span>Buscar ciudad o estado</span>
        <input type="search" [value]="query" (input)="onQuery($event)"
               placeholder="Ej. Pachuca" autocomplete="off">
      </label>
      <label class="check-field">
        <input type="checkbox" [checked]="enabledOnly" (change)="onEnabled($event)">
        <span>Solo ciudades habilitadas</span>
      </label>
    </section>
  `,
  styles: [`
    .filter-bar { display: flex; align-items: end; flex-wrap: wrap; gap: 18px; padding: 16px; background: #fff; border: 1px solid #d9e2ec; border-radius: 10px; }
    .search-field { display: grid; gap: 6px; width: min(360px, 100%); color: #486581; font-size: 13px; font-weight: 600; }
    input[type="search"] { width: 100%; box-sizing: border-box; border: 1px solid #bcccdc; border-radius: 6px; padding: 10px 12px; font: inherit; color: #102a43; }
    .check-field { display: flex; gap: 8px; align-items: center; padding: 10px 0; color: #334e68; font-size: 14px; }
  `]
})
export class WeatherFiltersComponent {
  @Input() query = '';
  @Input() enabledOnly = true;
  @Output() queryChanged = new EventEmitter<string>();
  @Output() enabledOnlyChanged = new EventEmitter<boolean>();

  onQuery(event: Event): void {
    this.queryChanged.emit((event.target as HTMLInputElement).value);
  }

  onEnabled(event: Event): void {
    this.enabledOnlyChanged.emit((event.target as HTMLInputElement).checked);
  }
}
