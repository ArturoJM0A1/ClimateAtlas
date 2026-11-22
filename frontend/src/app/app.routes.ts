import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { CityDetailComponent } from './pages/city-detail/city-detail.component';

/**
 * Angular Router asocia una URL con un componente.
 * La ruta de detalle contiene un parámetro :id que identifica la ciudad.
 */
export const routes: Routes = [
  { path: '', component: DashboardComponent, title: 'ClimateAtlas | México' },
  { path: 'cities/:id', component: CityDetailComponent, title: 'ClimateAtlas | Ciudad' },
  { path: '**', redirectTo: '' }
];
