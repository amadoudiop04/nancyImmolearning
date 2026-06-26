import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Tenant, PropertyDetails } from '../../../services/api.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-locataires',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div>
      <div style="display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:24px;">
        <div>
          <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Relations</div>
          <h1 style="margin:6px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Locataires</h1>
        </div>
        <button (click)="openCreate()"
          style="padding:11px 18px;border:none;border-radius:11px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
          + Inviter un locataire
        </button>
      </div>

      <!-- Add form -->
      @if (showForm) {
        <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:24px;margin-bottom:24px;">
          <h2 style="margin:0 0 18px;font-size:16px;font-weight:700;">{{ editingId ? 'Modifier le locataire' : 'Nouveau locataire' }}</h2>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
            @for (f of tenantFields; track f.key) {
              <div>
                <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">{{ f.label }}</label>
                <input [(ngModel)]="newTenant[f.key]" [placeholder]="f.placeholder" [type]="f.type ?? 'text'"
                  style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
              </div>
            }
          </div>

          <!-- Attacher à un bien (création uniquement) -->
          @if (!editingId) {
            <div style="margin-top:20px;padding-top:18px;border-top:1px dashed #E0E5DE;">
              <div style="font-family:'IBM Plex Mono',monospace;font-size:10.5px;letter-spacing:0.06em;text-transform:uppercase;color:#9AA49E;margin-bottom:12px;">
                Attribuer un bien (optionnel)
              </div>
              <div style="display:grid;grid-template-columns:2fr 1fr 1fr;gap:14px;">
                <div>
                  <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Bien</label>
                  <select [(ngModel)]="newTenant.propertyId" (ngModelChange)="onPropertyChange($event)"
                    style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;background:#fff;">
                    <option [ngValue]="null">— Aucun bien —</option>
                    @for (p of availableProperties; track p.id) {
                      <option [ngValue]="p.id">{{ p.name }}</option>
                    }
                  </select>
                </div>
                <div>
                  <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Loyer (€/mois)</label>
                  <input [(ngModel)]="newTenant.rentAmount" type="number" placeholder="750" [disabled]="!newTenant.propertyId"
                    style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
                </div>
                <div>
                  <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Début du bail</label>
                  <input [(ngModel)]="newTenant.startDate" type="date" [disabled]="!newTenant.propertyId"
                    style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
                </div>
              </div>
              @if (availableProperties.length === 0) {
                <p style="color:#9AA49E;margin:10px 0 0;font-size:12.5px;">Aucun bien disponible — tous vos biens sont déjà loués.</p>
              }
            </div>
          }

          <div style="display:flex;gap:10px;margin-top:16px;">
            <button (click)="saveTenant()"
              style="padding:11px 22px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
              {{ editingId ? 'Enregistrer les modifications' : 'Enregistrer' }}
            </button>
            <button (click)="cancelForm()"
              style="padding:11px 22px;border:1px solid #D6DED9;border-radius:10px;background:#fff;color:#16201D;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
              Annuler
            </button>
          </div>
          @if (error) { <p style="color:#C2563B;margin-top:10px;font-size:13px;">{{ error }}</p> }
        </div>
      }

      <!-- Table -->
      <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;overflow:hidden;">
        <div style="display:grid;grid-template-columns:2fr 2fr 1fr 190px;gap:14px;padding:14px 22px;background:#F7F9F6;font-family:'IBM Plex Mono',monospace;font-size:10.5px;letter-spacing:0.06em;text-transform:uppercase;color:#9AA49E;">
          <span>Locataire</span><span>Bien</span><span>Loyer</span><span style="text-align:right;">Actions</span>
        </div>
        @for (t of tenants; track t.id) {
          <div [style.opacity]="removingId === t.id ? '0' : '1'"
            [style.transform]="removingId === t.id ? 'translateX(20px)' : 'none'"
            style="display:grid;grid-template-columns:2fr 2fr 1fr 190px;gap:14px;padding:15px 22px;align-items:center;border-top:1px solid #EEF1ED;transition:opacity .28s ease,transform .28s ease;">
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
              <button (click)="startEdit(t)" class="nm-edit"
                style="padding:6px 12px;border:1px solid #D6DED9;border-radius:8px;background:#fff;color:#0E4F4A;font-family:inherit;font-size:12px;cursor:pointer;font-weight:600;transition:all .15s;">
                Modifier
              </button>
              <button (click)="deleteTenant(t.id, t.firstName + ' ' + t.lastName)" class="nm-del"
                style="padding:6px 12px;border:1px solid #D6DED9;border-radius:8px;background:#fff;color:#C2563B;font-family:inherit;font-size:12px;cursor:pointer;font-weight:600;transition:all .15s;">
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

    <style>
      .nm-del:hover { background:#FBE7DF; border-color:#E4C8C0; }
      .nm-edit:hover { background:#E7F1EF; border-color:#CFE0DA; }
    </style>
  `
})
export class LocatairesComponent implements OnInit {
  tenants: Tenant[] = [];
  properties: PropertyDetails[] = [];
  availableProperties: PropertyDetails[] = [];
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
  removingId: number | null = null;
  editingId: number | null = null;

  constructor(private api: ApiService, private toast: ToastService) {}

  openCreate() {
    this.editingId = null;
    this.error = '';
    this.newTenant = {};
    this.showForm = !this.showForm;
  }

  startEdit(t: Tenant) {
    this.editingId = t.id;
    this.error = '';
    this.newTenant = { ...t };
    this.showForm = true;
    // Le formulaire est en haut de la page : on y remonte pour qu'il soit visible.
    setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 0);
  }

  cancelForm() {
    this.showForm = false;
    this.editingId = null;
    this.newTenant = {};
    this.error = '';
  }

  ngOnInit() {
    this.load();
    this.api.getPropertyDetails().subscribe({ next: p => this.properties = p, error: () => {} });
    this.loadAvailableProperties();
  }

  loadAvailableProperties() {
    this.api.getAvailableProperties().subscribe({ next: p => this.availableProperties = p, error: () => {} });
  }

  onPropertyChange(propertyId: number | null) {
    // Pré-remplit le loyer depuis le bien sélectionné (modifiable ensuite).
    const prop = this.availableProperties.find(p => p.id === propertyId);
    this.newTenant.rentAmount = prop?.rent ?? null;
    if (!propertyId) {
      this.newTenant.startDate = null;
    }
  }

  load() {
    this.loading = true;
    this.api.getTenants().subscribe({
      next: t => { this.tenants = t; this.loading = false; },
      error: () => this.loading = false
    });
  }

  saveTenant() {
    this.error = '';
    if (!this.newTenant.firstName || !this.newTenant.lastName) { this.error = 'Prénom et nom requis.'; return; }

    if (this.editingId) {
      this.api.updateTenant(this.editingId, this.newTenant).subscribe({
        next: () => { this.cancelForm(); this.toast.success('Locataire modifié'); this.load(); },
        error: () => { this.error = 'Erreur lors de la modification.'; this.toast.error('Impossible de modifier le locataire'); }
      });
    } else {
      const propertyId = this.newTenant.propertyId;
      const rentAmount = this.newTenant.rentAmount;
      const startDate = this.newTenant.startDate;
      this.api.createTenant(this.newTenant).subscribe({
        next: (tenant) => {
          if (propertyId) {
            this.attachToProperty(tenant.id, propertyId, rentAmount, startDate);
          } else {
            this.cancelForm(); this.toast.success('Locataire ajouté'); this.load();
          }
        },
        error: () => { this.error = 'Erreur lors de la création.'; this.toast.error('Impossible d\'ajouter le locataire'); }
      });
    }
  }

  private attachToProperty(tenantId: number, propertyId: number, rentAmount: number | null, startDate: string | null) {
    this.api.createLease({
      propertyId,
      tenantId,
      rentAmount: rentAmount ?? 0,
      currency: 'EUR',
      startDate: startDate ?? new Date().toISOString().slice(0, 10),
    } as any).subscribe({
      next: () => {
        this.cancelForm();
        this.toast.success('Locataire ajouté et attribué au bien');
        this.load();
        this.loadAvailableProperties();
        this.api.getPropertyDetails().subscribe({ next: p => this.properties = p, error: () => {} });
      },
      error: () => {
        // Le locataire est créé mais le bail a échoué : on prévient sans bloquer.
        this.cancelForm();
        this.toast.error('Locataire créé, mais l\'attribution au bien a échoué');
        this.load();
      }
    });
  }

  deleteTenant(id: number, name?: string) {
    if (!confirm('Supprimer ce locataire ?')) return;
    this.removingId = id;
    this.api.deleteTenant(id).subscribe({
      next: () => {
        this.toast.success(name ? `« ${name} » supprimé` : 'Locataire supprimé');
        setTimeout(() => { this.removingId = null; this.load(); }, 280);
      },
      error: () => { this.removingId = null; this.toast.error('Suppression impossible'); }
    });
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
