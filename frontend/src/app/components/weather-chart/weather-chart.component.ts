import { AfterViewInit, Component, ElementRef, Input, OnChanges, OnDestroy, SimpleChanges, ViewChild } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { WeatherReading } from '../../models/weather.models';

Chart.register(...registerables);

/** Gráfica de línea creada directamente con Chart.js. */
@Component({
  selector: 'app-weather-chart',
  standalone: true,
  template: `
    <div class="chart-wrapper">
      <canvas #chartCanvas aria-label="Historial de temperatura" role="img"></canvas>
    </div>
  `,
  styles: [`
    .chart-wrapper { position: relative; width: 100%; height: 320px; }
  `]
})
export class WeatherChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() readings: WeatherReading[] = [];
  @ViewChild('chartCanvas') canvas?: ElementRef<HTMLCanvasElement>;

  private chart?: Chart;
  private viewReady = false;

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.renderChart();
  }

  ngOnChanges(_changes: SimpleChanges): void {
    if (this.viewReady) this.renderChart();
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }

  private renderChart(): void {
    if (!this.canvas) return;

    // Chart.js conserva listeners y elementos canvas: destroy evita duplicarlos.
    this.chart?.destroy();

    if (this.readings.length === 0) {
      this.chart = undefined;
      return;
    }

    const labels = this.readings.map(reading =>
      new Date(reading.measuredAt).toLocaleString('es-MX', {
        month: 'short', day: '2-digit', hour: '2-digit', minute: '2-digit'
      })
    );
    const values = this.readings.map(reading => reading.temperature);

    this.chart = new Chart(this.canvas.nativeElement, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: 'Temperatura (°C)',
          data: values,
          borderColor: '#2471a3',
          backgroundColor: 'rgba(36, 113, 163, 0.12)',
          borderWidth: 2,
          pointRadius: 3,
          pointHoverRadius: 5,
          fill: true,
          tension: 0.15
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { intersect: false, mode: 'index' },
        scales: {
          x: { title: { display: true, text: 'Fecha de medición' } },
          y: { title: { display: true, text: '°C' } }
        }
      }
    });
  }
}
