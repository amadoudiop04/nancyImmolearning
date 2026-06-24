import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Tenant, PropertyDetails } from '../../../services/api.service';

@Component({
  selector: 'app-locataires',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div>
      <div style="display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:24px;">
        <div>
          <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Relations</div>
          <h1 style="margin:6px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Locataires</h1>
        </div>
        <button (click)="showForm=!showForm"
          style="padding:11px 18px;border:none;border-radius:11px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
          + Inviter un locataire
        </button>
      </div>

      <!-- Add form -->
      @if (showForm) {
        <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:24px;margin-bottom:24px;">
          <h2 style="margin:0 0 18px;font-size:16px;font-weight:700;">Nouveau locataire</h2>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
            @for (f of tenantFields; track f.key) {
              <div>
                <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">{{ f.label }}</label>
                <input [(ngModel)]="newTenant[f.key]" [placeholder]="f.placeholder" [type]="f.type ?? 'text'"
                  style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
              </div>
            }
          </div>
          <div style="display:flex;gap:10px;margin-top:16px;">
            <button (click)="createTenant()"
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

      <!-- Table -->
      <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;overflow:hidden;">
        <div style="display:grid;grid-template-columns:2fr 2fr 1fr 130px;gap:14px;padding:14px 22px;background:#F7F9F6;font-family:'IBM Plex Mono',monospace;font-size:10.5px;letter-spacing:0.06em;text-transform:uppercase;color:#9AA49E;">
          <span>Locataire</span><span>Bien</span><span>Loyer</span><span style="text-align:right;">Actions</span>
        </div>
        @for (t of tenants; track t.id) {
          <div style="display:grid;grid-template-columns:2fr 2fr 1fr 130px;gap:14px;padding:15px 22px;align-items:center;border-top:1px solid #EEF1ED;">
            <div style="display:flex;align-items:center;gap:12px;min-width:0;">
              <div [style.background]="colorFor(t.id)"
                style="width:36px;height:36px;border-radius:10px;flex:none;display:flex;align-items:center;justify-content:center;color:#fff;font-weight:700;font-size:12.5px;">
                {{ t.firstName[0] }}{{ t.lastName[0] }}
              </div>
              <div>
                <div style="font-weight:600;font-size:14px;">{{ t.firstName }} {{ t.lastName }}</div>
                <div style="font-size:12px;color:#8A938E;">{{ t.email }}</div>
              </div>
            </div>
            <span style="font-size:13.5px;color:#5A655F;">{{ propertyFor(t.id) }}</span>
            <span style="font-family:'IBM Plex Mono',monospace;font-size:13.5px;">{{ rentFor(t.id) }}</span>
            <div style="text-align:right;display:flex;justify-content:flex-end;gap:8px;">
              <button (click)="deleteTenant(t.id)"
                style="padding:6px 12px;border:1px solid #D6DED9;border-radius:8px;background:#fff;color:#C2563B;font-family:inherit;font-size:12px;cursor:pointer;font-weight:600;">
                Supprimer
              </button>
            </div>
          </div>
        }
        @if (tenants.length === 0 && !loading) {
          <div style="padding:36px;text-align:center;color:#9AA49E;">Aucun locataire enregistré.</div>
        }
      </div>
    </div>
  `
})
export class LocatairesComponent implements OnInit {
  tenants: Tenant[] = [];
  properties: PropertyDetails[] = [];
  loading = true;
  showForm = false;
  error = '';
  newTenant: any = {};

  tenantFields = [
    { key: 'firstName', label: 'Prénom', placeholder: 'Thomas' },
    { key: 'lastName', label: 'Nom', placeholder: 'Martin' },
    { key: 'email', label: 'Email', placeholder: 'thomas.martin@email.fr' },
    { key: 'phone', label: 'Téléphone', placeholder: '0600000000', type: 'number' },
    { key: 'street', label: 'Rue', placeholder: '12 avenue Foch' },
    { key: 'city', label: 'Ville', placeholder: 'Nancy' },
    { key: 'zipCode', label: 'Code postal', placeholder: '54000' },
    { key: 'country', label: 'Pays', placeholder: 'France' },
  ];

  private colors = ['#0E4F4A', '#2A9D8F', '#264653', '#E76F51', '#E9C46A'];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.load();
    this.api.getPropertyDetails().subscribe({ next: p => this.properties = p, error: () => {} });
  }

  load() {
    this.loading = true;
    this.api.getTenants().subscribe({
      next: t => { this.tenants = t; this.loading = false; },
      error: () => this.loading = false
    });
  }

  createTenant() {
    this.error = '';
    if (!this.newTenant.firstName || !this.newTenant.lastName) { this.error = 'Prénom et nom requis.'; return; }
    this.api.createTenant(this.newTenant).subscribe({
      next: () => { this.showForm = false; this.newTenant = {}; this.load(); },
      error: () => this.error = 'Erreur lors de la création.'
    });
  }

  deleteTenant(id: number) {
    if (!confirm('Supprimer ce locataire ?')) return;
    this.api.deleteTenant(id).subscribe({ next: () => this.load(), error: () => {} });
  }

  colorFor(id: number): string {
    return this.colors[id % this.colors.length];
  }

  propertyFor(tenantId: number): string {
    const prop = this.properties.find(p => p.tenant?.id === tenantId);
    return prop?.name ?? '—';
  }

  rentFor(tenantId: number): string {
    const prop = this.properties.find(p => p.tenant?.id === tenantId);
    if (!prop?.lease?.rentAmount) return '—';
    return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: prop.lease.currency ?? 'EUR', maximumFractionDigits: 0 }).format(prop.lease.rentAmount) + '/mois';
  }
}
