import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

/**
 * bootstrapApplication inicia la aplicación standalone.
 * No existe un NgModule raíz: la configuración se declara en app.config.ts.
 */
bootstrapApplication(AppComponent, appConfig)
  .catch((error: unknown) => console.error('No se pudo iniciar ClimateAtlas', error));
