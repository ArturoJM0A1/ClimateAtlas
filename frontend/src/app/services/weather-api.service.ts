import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BatchExecution, Statistics, WeatherReading } from '../models/weather.models';

/** Servicio HTTP para historial, estadísticas e inicio manual del Job Batch. */
@Injectable({ providedIn: 'root' })
export class WeatherApiService {
  private readonly http = inject(HttpClient);

  history(cityId: number, from?: string, to?: string): Observable<WeatherReading[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get<WeatherReading[]>(
      `/api/cities/${cityId}/weather/history`, { params }
    );
  }

  statistics(): Observable<Statistics> {
    return this.http.get<Statistics>('/api/weather/statistics');
  }

  refresh(): Observable<BatchExecution> {
    return this.http.post<BatchExecution>('/api/weather/refresh', {});
  }
}
