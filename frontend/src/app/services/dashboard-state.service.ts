import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, catchError, of, tap, finalize } from 'rxjs';
import { CityApiService } from './city-api.service';
import { City } from '../models/weather.models';

/**
 * Estado compartido mínimo usando BehaviorSubject.
 *
 * BehaviorSubject conserva el último valor y lo emite a nuevas suscripciones.
 * Los componentes consumen los observables de solo lectura y solicitan cambios
 * mediante métodos del servicio, no accediendo al Subject directamente.
 */
@Injectable({ providedIn: 'root' })
export class DashboardStateService {
  private readonly cityApi = inject(CityApiService);

  private readonly citiesSubject = new BehaviorSubject<City[]>([]);
  private readonly selectedCitySubject = new BehaviorSubject<City | null>(null);
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  private readonly errorSubject = new BehaviorSubject<string | null>(null);

  readonly cities$ = this.citiesSubject.asObservable();
  readonly selectedCity$ = this.selectedCitySubject.asObservable();
  readonly loading$ = this.loadingSubject.asObservable();
  readonly error$ = this.errorSubject.asObservable();

  loadCities(): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);

    this.cityApi.findAll().pipe(
      tap(cities => {
        this.citiesSubject.next(cities);
        const selectedId = this.selectedCitySubject.value?.id;
        const selected = cities.find(city => city.id === selectedId) ?? null;
        this.selectedCitySubject.next(selected);
      }),
      catchError(() => {
        this.errorSubject.next('No fue posible cargar las ciudades. Comprueba que el backend esté iniciado.');
        return of([]);
      }),
      finalize(() => this.loadingSubject.next(false))
    ).subscribe();
  }

  reload(): Observable<City[]> {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    return this.cityApi.findAll().pipe(
      tap(cities => {
        this.citiesSubject.next(cities);
        const selectedId = this.selectedCitySubject.value?.id;
        this.selectedCitySubject.next(cities.find(city => city.id === selectedId) ?? null);
      }),
      finalize(() => this.loadingSubject.next(false))
    );
  }

  selectCity(city: City): void {
    this.selectedCitySubject.next(city);
  }

  clearSelection(): void {
    this.selectedCitySubject.next(null);
  }
}
