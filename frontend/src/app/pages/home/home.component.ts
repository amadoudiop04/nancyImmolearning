import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-10 py-6 sm:py-10">
      <div class="rounded-3xl bg-white shadow-xl shadow-slate-900/10 ring-1 ring-slate-200 p-6 sm:p-10 grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-8 items-center">
        <div class="lg:col-span-7">
          <p class="text-xs font-extrabold tracking-[0.22em] uppercase text-orange-700 mb-3">Application de gestion immobiliere</p>
          <h1 class="text-4xl sm:text-5xl lg:text-6xl font-black leading-[0.95] text-slate-900 mb-4">Une base d'accueil simple, propre et prete a evoluer.</h1>
          <p class="text-slate-600 leading-7 max-w-2xl mb-6">
          Cette page pose les fondations du frontend Nancy Immo avec une architecture claire,
          un style moderne et une structure facile a etendre.
        </p>

          <div class="flex flex-wrap items-center gap-3">
            <a class="inline-flex items-center justify-center rounded-full bg-gradient-to-r from-orange-500 to-red-600 px-5 py-3 text-sm font-bold text-white shadow-lg shadow-orange-500/25 hover:brightness-105" href="#services">Decouvrir les services</a>
            <a class="inline-flex items-center justify-center rounded-full border border-slate-300 bg-white px-5 py-3 text-sm font-bold text-slate-800 hover:bg-slate-100" href="#contact">Contacter l'equipe</a>
          </div>
        </div>

        
      </div>

      <section class="mt-6" id="services">
        <div class="mb-4">
          <p class="text-xs font-extrabold tracking-[0.22em] uppercase text-orange-700 mb-2">Services</p>
          <h2 class="text-2xl sm:text-4xl font-black text-slate-900">Les blocs essentiels sont deja penses pour le projet.</h2>
      </div>

        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          <article class="rounded-2xl bg-white p-5 ring-1 ring-slate-200 shadow-md shadow-slate-900/5" *ngFor="let feature of features">
            <h3 class="text-lg font-bold text-slate-900 mb-2">{{ feature.title }}</h3>
            <p class="text-slate-600 leading-7">{{ feature.description }}</p>
        </article>
      </div>
      </section>
    </section>
  `,
})
export class HomeComponent {
  readonly stats = [
    { value: '100%', label: 'Vue centralisée des biens' },
    { value: '3', label: 'Modules clés prêts à brancher' },
    { value: '1', label: 'Base claire pour évoluer' }
  ];

  readonly features = [
    {
      title: 'Suivi des biens',
      description: 'Organisez vos immeubles, logements et propriétaires dans une structure simple et lisible.'
    },
    {
      title: 'Gestion des baux',
      description: 'Préparez le terrain pour suivre les contrats, les dates importantes et les états.'
    },
    {
      title: 'Architecture propre',
      description: 'Séparation nette entre layout, pages et données pour faciliter la maintenance.'
    }
  ];
}