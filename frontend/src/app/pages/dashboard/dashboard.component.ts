import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, EMPTY, catchError, combineLatest, finalize, map, of, switchMap } from 'rxjs';
import { City } from '../../models/weather.models';
import { DashboardStateService } from '../../services/dashboard-state.service';
import { WeatherApiService } from '../../services/weather-api.service';
import { CityListComponent } from '../../components/city-list/city-list.component';
import { WeatherFiltersComponent } from '../../components/weather-filters/weather-filters.component';
import { WeatherMapComponent } from '../../components/weather-map/weather-map.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, CityListComponent, WeatherFiltersComponent, WeatherMapComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly state = inject(DashboardStateService);
  private readonly weatherApi = inject(WeatherApiService);

  readonly loading$ = this.state.loading$;
  readonly error$ = this.state.error$;
  readonly selectedCity$ = this.state.selectedCity$;
  readonly cities$ = this.state.cities$;

  private readonly querySubject = new BehaviorSubject<string>('');
  private readonly enabledOnlySubject = new BehaviorSubject<boolean>(true);
  private readonly statsReloadSubject = new BehaviorSubject<void>(undefined);

  readonly filteredCities$ = combineLatest([
    this.cities$,
    this.querySubject,
    this.enabledOnlySubject
  ]).pipe(
    map(([cities, query, enabledOnly]) => {
      const normalizedQuery = query.trim().toLocaleLowerCase('es-MX');
      return cities.filter(city => {
        const matchesText = `${city.name} ${city.state}`
          .toLocaleLowerCase('es-MX').includes(normalizedQuery);
        return matchesText && (!enabledOnly || city.enabled);
      });
    })
  );

  readonly statistics$ = this.statsReloadSubject.pipe(
    switchMap(() => this.weatherApi.statistics().pipe(catchError(() => of(null))))
  );

  refreshing = false;
  refreshMessage: string | null = null;

  ngOnInit(): void {
    this.state.loadCities();
  }

  updateQuery(query: string): void {
    this.querySubject.next(query);
  }

  updateEnabledOnly(enabledOnly: boolean): void {
    this.enabledOnlySubject.next(enabledOnly);
  }

  selectCity(city: City): void {
    this.state.selectCity(city);
  }

  refreshNow(): void {
    this.refreshing = true;
    this.refreshMessage = null;

    this.weatherApi.refresh().pipe(
      switchMap(execution => {
        this.refreshMessage = `Ejecución #${execution.executionId}: ${execution.status}`;
        return this.state.reload();
      }),
      catchError(() => {
        this.refreshMessage = 'No se pudo iniciar la actualización. Revisa el estado del backend.';
        return EMPTY;
      }),
      finalize(() => {
        this.refreshing = false;
        this.statsReloadSubject.next();
      })
    ).subscribe();
  }
}
