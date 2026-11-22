import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <header class="site-header">
      <div class="header-inner">
        <a class="brand" routerLink="/" aria-label="ClimateAtlas, inicio">
          <span class="brand-mark">CA</span>
          <span>
            <strong>ClimateAtlas</strong>
            <small>Atlas meteorológico de México</small>
          </span>
        </a>
        <nav aria-label="Navegación principal">
          <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">
            Dashboard
          </a>
        </nav>
      </div>
    </header>
    <main class="page-shell">
      <router-outlet />
    </main>
    <footer class="site-footer">
      Datos meteorológicos de <a href="https://open-meteo.com/" target="_blank" rel="noreferrer">Open-Meteo</a>.
      Mapa © <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noreferrer">OpenStreetMap</a>.
    </footer>
  `,
  styles: [`
    :host { display: block; min-height: 100vh; }
    .site-header { background: #102a43; color: #fff; }
    .header-inner { max-width: 1240px; margin: auto; padding: 16px 24px; display: flex; align-items: center; justify-content: space-between; }
    .brand { display: flex; align-items: center; gap: 12px; text-decoration: none; color: inherit; }
    .brand-mark { width: 42px; height: 42px; display: grid; place-items: center; border: 1px solid #9fb3c8; border-radius: 10px; font-weight: 800; letter-spacing: -1px; }
    .brand strong, .brand small { display: block; }
    .brand small { color: #bcccdc; font-size: 12px; margin-top: 2px; }
    nav a { color: #d9e2ec; text-decoration: none; padding: 8px 12px; border-radius: 6px; }
    nav a.active, nav a:hover { background: #243b53; color: #fff; }
    .page-shell { max-width: 1240px; margin: 0 auto; padding: 28px 24px 48px; }
    .site-footer { border-top: 1px solid #d9e2ec; padding: 20px 24px; text-align: center; color: #627d98; font-size: 13px; }
    .site-footer a { color: #486581; }
    @media (max-width: 600px) { .header-inner { padding: 14px 16px; } .page-shell { padding: 22px 16px 36px; } }
  `]
})
export class AppComponent {}
