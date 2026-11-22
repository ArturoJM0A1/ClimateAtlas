import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { City } from '../models/weather.models';

/**
 * Servicio HTTP para el recurso City.
 * Los componentes no construyen URLs ni conocen detalles de HttpClient.
 */
@Injectable({ providedIn: 'root' })
export class CityApiService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/cities';

  findAll(): Observable<City[]> {
    return this.http.get<City[]>(this.endpoint);
  }

  findById(id: number): Observable<City> {
    return this.http.get<City>(`${this.endpoint}/${id}`);
  }

  create(city: Omit<City, 'id' | 'enabled' | 'currentWeather'>): Observable<City> {
    return this.http.post<City>(this.endpoint, city);
  }

  update(id: number, city: Omit<City, 'id' | 'enabled' | 'currentWeather'>): Observable<City> {
    return this.http.put<City>(`${this.endpoint}/${id}`, city);
  }

  updateEnabled(id: number, enabled: boolean): Observable<City> {
    return this.http.patch<City>(`${this.endpoint}/${id}/enabled`, { enabled });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }
}
