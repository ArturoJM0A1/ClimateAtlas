import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CityApiService } from '../../services/city-api.service';
import { WeatherApiService } from '../../services/weather-api.service';
import { City, WeatherReading } from '../../models/weather.models';
import { WeatherChartComponent } from '../../components/weather-chart/weather-chart.component';

@Component({
  selector: 'app-city-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, WeatherChartComponent],
  templateUrl: './city-detail.component.html',
  styleUrl: './city-detail.component.css'
})
export class CityDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly cityApi = inject(CityApiService);
  private readonly weatherApi = inject(WeatherApiService);

  city: City | null = null;
  readings: WeatherReading[] = [];
  loading = false;
  error: string | null = null;
  fromDate = this.dateDaysAgo(7);
  toDate = this.dateToday();
  private cityId = 0;

  ngOnInit(): void {
    this.cityId = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isInteger(this.cityId) || this.cityId <= 0) {
      this.error = 'El identificador de ciudad no es válido.';
      return;
    }

    this.cityApi.findById(this.cityId).subscribe({
      next: city => this.city = city,
      error: () => this.error = 'No fue posible consultar la ciudad.'
    });
    this.loadHistory();
  }

  loadHistory(): void {
    this.loading = true;
    this.error = null;
    const from = `${this.fromDate}T00:00:00`;
    const to = `${this.toDate}T23:59:59`;

    this.weatherApi.history(this.cityId, from, to).subscribe({
      next: readings => {
        this.readings = readings;
        this.loading = false;
      },
      error: () => {
        this.error = 'No fue posible consultar el historial.';
        this.loading = false;
      }
    });
  }

  private dateToday(): string {
    return this.formatDate(new Date());
  }

  private dateDaysAgo(days: number): string {
    const date = new Date();
    date.setDate(date.getDate() - days);
    return this.formatDate(date);
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
