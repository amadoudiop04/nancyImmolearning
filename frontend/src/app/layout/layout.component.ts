import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-site-layout',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  template: `
    <div class="min-h-screen bg-slate-50 text-slate-900 flex flex-col">
      <header class="sticky top-0 z-10 border-b border-slate-200/70 bg-white/85 backdrop-blur">
        <div class="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-10 py-4 flex flex-wrap items-center justify-between gap-4">
          <a class="inline-flex items-center gap-3" routerLink="/" aria-label="Nancy Immo accueil">
            <span class="h-11 w-11 rounded-xl grid place-items-center text-white font-extrabold bg-gradient-to-br from-orange-500 to-red-600 shadow-lg shadow-orange-500/30">NI</span>
            <span>
              <strong class="block text-base">Nancy Immo</strong>
              <small class="block text-xs text-slate-500">Gestion immobiliere simple</small>
            </span>
          </a>

          <nav class="flex flex-wrap gap-4 text-sm font-semibold text-slate-600">
            <a class="hover:text-slate-900 transition-colors" routerLink="/">Accueil</a>
            <a class="hover:text-slate-900 transition-colors" href="#services">Services</a>
            <a class="hover:text-slate-900 transition-colors" href="#contact">Contact</a>
          </nav>
        </div>
      </header>

      <main class="flex-1">
        <router-outlet />
      </main>

      <footer class="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-10 py-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-sm text-slate-500" id="contact">
        <p>Base frontend prete pour connecter les modules bailleurs, biens et baux.</p>
        <span>Angular 18 + Spring Boot</span>
      </footer>
    </div>
  `
})
export class SiteLayoutComponent {}
