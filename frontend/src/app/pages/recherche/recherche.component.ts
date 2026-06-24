import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService, PropertyDetails } from '../../services/api.service';

@Component({
  selector: 'app-recherche',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div style="max-width:1180px;margin:0 auto;padding:34px 32px 60px;">
      <div style="margin-bottom:24px;">
        <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Trouvez votre logement</div>
        <h1 style="margin:6px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Biens disponibles à Nancy</h1>
      </div>

      <!-- Filters -->
      <div style="background:#fff;border:1px solid #E4E7E2;border-radius:14px;padding:14px;display:flex;gap:10px;flex-wrap:wrap;align-items:center;margin-bottom:14px;">
        <div style="flex:1;min-width:200px;display:flex;align-items:center;gap:9px;background:#F4F6F3;border-radius:10px;padding:11px 14px;">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><circle cx="11" cy="11" r="6.5" stroke="#9AA49E" stroke-width="1.8"/><path d="M16 16l4 4" stroke="#9AA49E" stroke-width="1.8" stroke-linecap="round"/></svg>
          <input [(ngModel)]="search" (ngModelChange)="filter()" placeholder="Nom, localisation, type…"
            style="background:transparent;border:none;outline:none;font-family:inherit;font-size:14px;width:100%;">
        </div>
        <select [(ngModel)]="filterKind" (ngModelChange)="filter()"
          style="padding:11px 15px;border-radius:10px;border:1px solid #E4E7E2;font-size:13px;font-weight:600;color:#5A655F;background:#F1F4F0;font-family:inherit;cursor:pointer;">
          <option value="">Tous types</option>
          @for (k of kinds; track k) { <option [value]="k">{{ k }}</option> }
        </select>
        <select [(ngModel)]="maxBudget" (ngModelChange)="filter()"
          style="padding:11px 15px;border-radius:10px;border:1px solid #E4E7E2;font-size:13px;font-weight:600;color:#5A655F;background:#F1F4F0;font-family:inherit;cursor:pointer;">
          <option [ngValue]="0">Budget max</option>
          <option [ngValue]="500">≤ 500 €</option>
          <option [ngValue]="800">≤ 800 €</option>
          <option [ngValue]="1200">≤ 1 200 €</option>
          <option [ngValue]="2000">≤ 2 000 €</option>
        </select>
        <select [(ngModel)]="minSurface" (ngModelChange)="filter()"
          style="padding:11px 15px;border-radius:10px;border:1px solid #E4E7E2;font-size:13px;font-weight:600;color:#5A655F;background:#F1F4F0;font-family:inherit;cursor:pointer;">
          <option [ngValue]="0">Surface min</option>
          <option [ngValue]="20">≥ 20 m²</option>
          <option [ngValue]="40">≥ 40 m²</option>
          <option [ngValue]="60">≥ 60 m²</option>
          <option [ngValue]="80">≥ 80 m²</option>
        </select>
        <button (click)="reset()"
          style="background:#fff;color:#5A655F;border:1px solid #E4E7E2;padding:11px 16px;border-radius:10px;font-family:inherit;font-weight:600;font-size:13px;cursor:pointer;">
          Réinitialiser
        </button>
      </div>

      <div style="font-size:13px;color:#8A938E;margin-bottom:18px;">
        {{ filtered.length }} bien(s) disponible(s){{ filtered.length !== all.length ? ' sur ' + all.length : '' }}
      </div>

      @if (loading) {
        <div style="text-align:center;padding:48px;color:#9AA49E;">Chargement…</div>
      } @else if (filtered.length === 0) {
        <div style="text-align:center;padding:48px;color:#9AA49E;background:#fff;border:1px solid #E4E7E2;border-radius:16px;">
          Aucun bien disponible pour ces critères.
        </div>
      } @else {
        <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:18px;">
          @for (r of filtered; track r.id) {
            <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;overflow:hidden;">
              <div style="position:relative;height:160px;background:repeating-linear-gradient(45deg,#EDEFEA,#EDEFEA 11px,#E4E7E2 11px,#E4E7E2 22px);display:flex;align-items:flex-end;padding:12px;">
                <span style="position:absolute;top:12px;right:12px;background:#fff;border-radius:999px;padding:5px 12px;font-family:'IBM Plex Mono',monospace;font-size:13px;font-weight:500;color:#0E4F4A;">
                  {{ formatRent(r.rent) }}/mois
                </span>
              </div>
              <div style="padding:16px 18px 18px;">
                <div style="font-weight:700;font-size:15.5px;">{{ r.name }}</div>
                <div style="font-size:12.5px;color:#8A938E;margin-top:4px;">{{ r.location || 'Nancy' }}</div>
                <div style="display:flex;gap:7px;margin-top:13px;flex-wrap:wrap;">
                  @if (r.kind) {
                    <span style="font-family:'IBM Plex Mono',monospace;font-size:11px;color:#5A655F;background:#F1F4F0;padding:4px 9px;border-radius:7px;">{{ r.kind }}</span>
                  }
                  @if (r.size) {
                    <span style="font-family:'IBM Plex Mono',monospace;font-size:11px;color:#5A655F;background:#F1F4F0;padding:4px 9px;border-radius:7px;">{{ r.size }}</span>
                  }
                  <span style="font-size:11px;color:#0E4F4A;background:#E7F1EF;padding:4px 9px;border-radius:7px;font-weight:600;">Disponible</span>
                </div>
                <button (click)="deposerDossier(r)"
                  style="margin-top:16px;width:100%;padding:11px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:13.5px;cursor:pointer;">
                  Déposer mon dossier
                </button>
              </div>
            </div>
          }
        </div>
      }

      <!-- CTA -->
      <div style="margin-top:24px;background:#E7F1EF;border:1px solid #CFE3DE;border-radius:18px;padding:28px;display:flex;align-items:center;justify-content:space-between;gap:24px;flex-wrap:wrap;">
        <div style="max-width:560px;">
          <div style="font-size:19px;font-weight:800;letter-spacing:-0.01em;">Constituez votre dossier une seule fois</div>
          <p style="margin:8px 0 0;color:#3F5A54;font-size:14.5px;line-height:1.6;">Pièces d'identité, justificatifs de revenus et garants : déposez votre dossier numérique et candidatez en un clic.</p>
        </div>
        <a routerLink="/inscription" style="background:#0E4F4A;color:#fff;border:none;padding:14px 24px;border-radius:12px;font-family:inherit;font-weight:700;font-size:15px;cursor:pointer;white-space:nowrap;text-decoration:none;">
          Créer mon dossier
        </a>
      </div>

      <!-- Candidature confirmation modal -->
      @if (candidature) {
        <div (click)="candidature=null"
          style="position:fixed;inset:0;background:rgba(22,32,29,0.55);z-index:100;display:flex;align-items:center;justify-content:center;padding:24px;">
          <div (click)="$event.stopPropagation()"
            style="background:#fff;border-radius:18px;max-width:420px;width:100%;padding:30px;text-align:center;box-shadow:0 30px 70px rgba(0,0,0,0.3);">
            <div style="width:56px;height:56px;border-radius:50%;background:#D1FAE5;color:#065F46;display:flex;align-items:center;justify-content:center;font-size:26px;margin:0 auto 16px;">✓</div>
            <h2 style="margin:0 0 8px;font-size:20px;font-weight:800;">Candidature enregistrée</h2>
            <p style="margin:0 0 22px;color:#5A655F;font-size:14px;line-height:1.6;">
              Votre dossier pour <strong>{{ candidature.name }}</strong> a bien été transmis au bailleur.
              Vous serez recontacté(e) prochainement.
            </p>
            <button (click)="candidature=null"
              style="width:100%;padding:12px;border:none;border-radius:11px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:700;font-size:14px;cursor:pointer;">
              Compris
            </button>
          </div>
        </div>
      }
    </div>
  `
})
export class RechercheComponent implements OnInit {
  all: PropertyDetails[] = [];
  filtered: PropertyDetails[] = [];
  kinds: string[] = [];
  search = '';
  filterKind = '';
  maxBudget = 0;
  minSurface = 0;
  loading = true;
  candidature: PropertyDetails | null = null;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getAvailableProperties().subscribe({
      next: props => {
        this.all = props;
        this.kinds = [...new Set(props.map(p => p.kind).filter(Boolean))];
        this.filtered = props;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  filter() {
    const txt = this.search.trim().toLowerCase();
    this.filtered = this.all.filter(p => {
      const matchSearch = !txt
        || p.name?.toLowerCase().includes(txt)
        || p.location?.toLowerCase().includes(txt)
        || p.kind?.toLowerCase().includes(txt);
      const matchKind = !this.filterKind || p.kind === this.filterKind;
      const matchBudget = !this.maxBudget || (p.rent != null && p.rent <= this.maxBudget);
      const matchSurface = !this.minSurface || this.parseSurface(p.size) >= this.minSurface;
      return matchSearch && matchKind && matchBudget && matchSurface;
    });
  }

  reset() {
    this.search = '';
    this.filterKind = '';
    this.maxBudget = 0;
    this.minSurface = 0;
    this.filtered = this.all;
  }

  deposerDossier(p: PropertyDetails) {
    this.candidature = p;
  }

  parseSurface(size?: string): number {
    if (!size) return 0;
    const match = size.match(/\d+/);
    return match ? parseInt(match[0], 10) : 0;
  }

  formatRent(amount?: number): string {
    if (!amount) return 'Sur demande';
    return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(amount);
  }
}
