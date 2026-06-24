import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService, Property, Building, Landlord } from '../../../services/api.service';

@Component({
  selector: 'app-biens',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div>
      <div style="display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:24px;">
        <div>
          <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Patrimoine</div>
          <h1 style="margin:6px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Mes biens</h1>
        </div>
        <button (click)="showForm=!showForm"
          style="padding:11px 18px;border:none;border-radius:11px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
          + Ajouter un bien
        </button>
      </div>

      <!-- Add form -->
      @if (showForm) {
        <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:24px;margin-bottom:24px;">
          <h2 style="margin:0 0 18px;font-size:16px;font-weight:700;">Nouveau bien</h2>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Nom du bien</label>
              <input [(ngModel)]="newProp.name" placeholder="Ex: Appartement T3 Foch" style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
            </div>
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Type</label>
              <input [(ngModel)]="newProp.kind" placeholder="Ex: T3, Studio, Maison" style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
            </div>
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Surface</label>
              <input [(ngModel)]="newProp.size" placeholder="Ex: 65 m²" style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
            </div>
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Adresse / Localisation</label>
              <input [(ngModel)]="newProp.location" placeholder="Ex: 12 Av. Foch, Nancy" style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
            </div>
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Immeuble</label>
              <select [(ngModel)]="newProp.buildingId" style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;background:#fff;">
                <option [value]="undefined">— Aucun —</option>
                @for (b of buildings; track b.id) {
                  <option [value]="b.id">{{ b.name }}</option>
                }
              </select>
            </div>
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Bailleur</label>
              <select [(ngModel)]="newProp.landlordId" style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;background:#fff;">
                <option [value]="undefined">— Aucun —</option>
                @for (l of landlords; track l.id) {
                  <option [value]="l.id">{{ l.firstName }} {{ l.lastName }}</option>
                }
              </select>
            </div>
          </div>
          <div style="display:flex;gap:10px;margin-top:16px;">
            <button (click)="createProperty()"
              style="padding:11px 22px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
              Enregistrer
            </button>
            <button (click)="showForm=false"
              style="padding:11px 22px;border:1px solid #D6DED9;border-radius:10px;background:#fff;color:#16201D;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
              Annuler
            </button>
          </div>
          @if (error) { <p style="color:#C2563B;margin-top:10px;font-size:13px;">{{ error }}</p> }
        </div>
      }

      <!-- Properties grid -->
      <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:18px;">
        @for (p of properties; track p.id) {
          <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;overflow:hidden;">
            <div style="position:relative;height:148px;background:repeating-linear-gradient(45deg,#EDEFEA,#EDEFEA 11px,#E4E7E2 11px,#E4E7E2 22px);display:flex;align-items:flex-end;padding:12px;">
              <span style="position:absolute;top:12px;left:12px;padding:5px 11px;border-radius:999px;font-size:11.5px;font-weight:600;"
                [style.background]="p.landlord ? '#D1FAE5' : '#F3F4F6'"
                [style.color]="p.landlord ? '#065F46' : '#6B7280'">
                {{ p.landlord ? 'Géré' : 'Sans bailleur' }}
              </span>
            </div>
            <div style="padding:16px 18px 18px;">
              <div style="display:flex;justify-content:space-between;align-items:flex-start;gap:10px;">
                <div style="font-weight:700;font-size:15.5px;line-height:1.25;">{{ p.name }}</div>
                <div style="font-family:'IBM Plex Mono',monospace;font-weight:500;font-size:15px;color:#0E4F4A;white-space:nowrap;">
                  {{ p.lease?.rentAmount ? formatRent(p.lease!.rentAmount, p.lease!.currency) : '—' }}/mois
                </div>
              </div>
              <div style="font-size:12.5px;color:#8A938E;margin-top:4px;">{{ p.location }}</div>
              <div style="display:flex;gap:8px;margin-top:14px;font-family:'IBM Plex Mono',monospace;font-size:11px;color:#5A655F;flex-wrap:wrap;">
                <span style="background:#F1F4F0;padding:4px 9px;border-radius:7px;">{{ p.kind }}</span>
                <span style="background:#F1F4F0;padding:4px 9px;border-radius:7px;">{{ p.size }}</span>
                <span style="background:#F1F4F0;padding:4px 9px;border-radius:7px;">
                  {{ p.tenant ? p.tenant.firstName + ' ' + p.tenant.lastName : 'Vacant' }}
                </span>
              </div>
              <div style="display:flex;gap:8px;margin-top:14px;">
                <a [routerLink]="p.id.toString()"
                  style="flex:1;padding:9px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:13px;cursor:pointer;text-decoration:none;text-align:center;">
                  Voir détail
                </a>
                <button (click)="deleteProperty(p.id)"
                  style="padding:9px 14px;border:1px solid #D6DED9;border-radius:10px;background:#fff;color:#C2563B;font-family:inherit;font-weight:600;font-size:13px;cursor:pointer;">
                  ✕
                </button>
              </div>
            </div>
          </div>
        }
        @if (properties.length === 0 && !loading) {
          <div style="grid-column:1/-1;text-align:center;padding:48px;color:#9AA49E;">
            Aucun bien enregistré. Ajoutez votre premier bien ci-dessus.
          </div>
        }
      </div>
    </div>
  `
})
export class BiensComponent implements OnInit {
  properties: any[] = [];
  buildings: Building[] = [];
  landlords: Landlord[] = [];
  loading = true;
  showForm = false;
  error = '';
  newProp: Partial<Property> = { name: '', kind: '', size: '', location: '' };

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.load();
    this.api.getBuildings().subscribe({ next: b => this.buildings = b, error: () => {} });
    this.api.getLandlords().subscribe({ next: l => this.landlords = l, error: () => {} });
  }

  load() {
    this.loading = true;
    this.api.getPropertyDetails().subscribe({
      next: p => { this.properties = p; this.loading = false; },
      error: () => this.loading = false
    });
  }

  createProperty() {
    this.error = '';
    if (!this.newProp.name) { this.error = 'Le nom est requis.'; return; }
    this.api.createProperty(this.newProp as any).subscribe({
      next: () => { this.showForm = false; this.newProp = { name: '', kind: '', size: '', location: '' }; this.load(); },
      error: () => this.error = 'Erreur lors de la création.'
    });
  }

  deleteProperty(id: number) {
    if (!confirm('Supprimer ce bien ?')) return;
    this.api.deleteProperty(id).subscribe({ next: () => this.load(), error: () => {} });
  }

  formatRent(amount: number, currency: string): string {
    return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: currency ?? 'EUR', maximumFractionDigits: 0 }).format(amount);
  }
}
