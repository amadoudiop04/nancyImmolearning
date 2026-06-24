import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Document, PropertyDetails, Tenant } from '../../../services/api.service';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div>
      <div style="display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:24px;">
        <div>
          <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Archivage</div>
          <h1 style="margin:6px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Documents</h1>
        </div>
        <button (click)="showForm=!showForm"
          style="padding:11px 18px;border:none;border-radius:11px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
          + Ajouter un document
        </button>
      </div>

      @if (showForm) {
        <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:24px;margin-bottom:24px;">
          <div style="display:grid;grid-template-columns:1fr 1fr 1fr 1fr;gap:14px;">
            <div style="grid-column:1/3;">
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Nom du document</label>
              <input [(ngModel)]="newDoc.name" placeholder="Bail signé Mars 2024"
                style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
            </div>
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Type</label>
              <select [(ngModel)]="newDoc.docType"
                style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;background:#fff;outline:none;">
                <option value="LEASE">Bail</option>
                <option value="QUITTANCE">Quittance</option>
                <option value="IDENTITY">Identité</option>
                <option value="OTHER">Autre</option>
              </select>
            </div>
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Lié au bien</label>
              <select [(ngModel)]="newDoc.propertyId"
                style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;background:#fff;outline:none;">
                <option [value]="undefined">— Aucun —</option>
                @for (p of properties; track p.id) {
                  <option [value]="p.id">{{ p.name }}</option>
                }
              </select>
            </div>
            <div>
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Lié au locataire</label>
              <select [(ngModel)]="newDoc.tenantId"
                style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;background:#fff;outline:none;">
                <option [value]="undefined">— Aucun —</option>
                @for (t of tenants; track t.id) {
                  <option [value]="t.id">{{ t.firstName }} {{ t.lastName }}</option>
                }
              </select>
            </div>
          </div>
          <div style="display:flex;gap:10px;margin-top:16px;">
            <button (click)="createDoc()"
              style="padding:11px 22px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
              Enregistrer
            </button>
            <button (click)="showForm=false"
              style="padding:11px 22px;border:1px solid #D6DED9;border-radius:10px;background:#fff;color:#16201D;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
              Annuler
            </button>
          </div>
        </div>
      }

      <!-- Filter tabs -->
      <div style="display:flex;gap:8px;margin-bottom:18px;flex-wrap:wrap;">
        @for (t of typeTabs; track t.value) {
          <button (click)="activeType=t.value"
            [style.background]="activeType===t.value ? '#0E4F4A' : '#fff'"
            [style.color]="activeType===t.value ? '#fff' : '#5A655F'"
            [style.border]="activeType===t.value ? '1px solid #0E4F4A' : '1px solid #E4E7E2'"
            style="padding:8px 15px;border-radius:999px;font-family:inherit;font-size:13px;font-weight:600;cursor:pointer;">
            {{ t.label }}
          </button>
        }
      </div>

      <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:14px;">
        @for (d of visibleDocuments; track d.id) {
          <div style="background:#fff;border:1px solid #E4E7E2;border-radius:14px;padding:18px;display:flex;gap:13px;align-items:center;">
            <div style="width:40px;height:40px;border-radius:10px;background:#E7F1EF;color:#0E4F4A;display:flex;align-items:center;justify-content:center;font-family:'IBM Plex Mono',monospace;font-size:10px;flex:none;">PDF</div>
            <div style="min-width:0;flex:1;">
              <div style="font-weight:600;font-size:14px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">{{ d.name }}</div>
              <div style="font-size:11.5px;color:#9AA49E;">{{ typeLabel(d.docType) }} · {{ d.createdAt }}</div>
            </div>
            <button (click)="view(d)" title="Visualiser"
              style="border:1px solid #D6DED9;background:#fff;color:#0E4F4A;border-radius:8px;cursor:pointer;font-size:12px;font-weight:600;padding:6px 10px;flex:none;">
              Visualiser
            </button>
            <button (click)="deleteDoc(d.id)" title="Supprimer"
              style="border:none;background:transparent;color:#C2563B;cursor:pointer;font-size:13px;flex:none;">✕</button>
          </div>
        }
        @if (visibleDocuments.length === 0) {
          <div style="grid-column:1/-1;text-align:center;padding:48px;color:#9AA49E;">Aucun document dans cette catégorie.</div>
        }
      </div>

      <!-- Viewer modal -->
      @if (viewing) {
        <div (click)="viewing=null"
          style="position:fixed;inset:0;background:rgba(22,32,29,0.55);z-index:100;display:flex;align-items:center;justify-content:center;padding:24px;">
          <div (click)="$event.stopPropagation()"
            style="background:#fff;border-radius:18px;max-width:560px;width:100%;max-height:90vh;overflow:auto;box-shadow:0 30px 70px rgba(0,0,0,0.3);">
            <!-- toolbar -->
            <div style="display:flex;align-items:center;justify-content:space-between;padding:16px 22px;border-bottom:1px solid #EEF1ED;">
              <div style="font-size:14px;font-weight:700;">{{ viewing.name }}</div>
              <button (click)="viewing=null" style="border:none;background:transparent;font-size:18px;cursor:pointer;color:#5A655F;">✕</button>
            </div>
            <!-- simulated PDF body -->
            <div style="padding:34px;background:#F4F6F3;">
              <div style="background:#fff;border:1px solid #E4E7E2;border-radius:10px;padding:34px;font-family:'Hanken Grotesk',sans-serif;">
                <div style="display:flex;justify-content:space-between;align-items:flex-start;border-bottom:2px solid #0E4F4A;padding-bottom:16px;">
                  <div>
                    <div style="font-weight:800;font-size:18px;">Nancy<span style="color:#2A9D8F;">Immo</span></div>
                    <div style="font-size:11px;color:#9AA49E;margin-top:2px;">{{ typeLabel(viewing.docType) }}</div>
                  </div>
                  <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;color:#5A655F;text-align:right;">
                    Réf. #{{ viewing.id }}<br>{{ viewing.createdAt }}
                  </div>
                </div>
                <h2 style="font-size:20px;font-weight:800;margin:22px 0 8px;">{{ viewing.name }}</h2>
                @if (viewing.docType === 'QUITTANCE') {
                  <p style="font-size:13.5px;color:#5A655F;line-height:1.7;">
                    Le bailleur atteste avoir reçu du locataire la somme correspondant au paiement
                    du loyer et des charges pour la période indiquée, et lui en délivre quittance.
                  </p>
                  <div style="margin-top:20px;background:#E7F1EF;border-radius:10px;padding:16px;display:flex;justify-content:space-between;align-items:center;">
                    <span style="font-size:13px;color:#0E4F4A;font-weight:600;">Statut du paiement</span>
                    <span style="font-size:13px;color:#065F46;font-weight:700;background:#D1FAE5;padding:5px 12px;border-radius:999px;">Acquitté</span>
                  </div>
                } @else {
                  <p style="font-size:13.5px;color:#5A655F;line-height:1.7;">
                    Document archivé dans votre espace Nancy Immo. Aperçu généré automatiquement.
                  </p>
                }
                <div style="margin-top:28px;display:flex;justify-content:space-between;font-size:12px;color:#9AA49E;border-top:1px solid #EEF1ED;padding-top:14px;">
                  <span>Nancy Immo — Gestion locative autonome</span>
                  <span>Signature : N. Aubert</span>
                </div>
              </div>
            </div>
            <div style="padding:14px 22px;border-top:1px solid #EEF1ED;display:flex;justify-content:flex-end;gap:10px;">
              <button (click)="viewing=null" style="padding:9px 18px;border:1px solid #D6DED9;border-radius:9px;background:#fff;color:#16201D;font-family:inherit;font-weight:600;font-size:13px;cursor:pointer;">Fermer</button>
              <button (click)="print()" style="padding:9px 18px;border:none;border-radius:9px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:13px;cursor:pointer;">Imprimer / PDF</button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class DocumentsComponent implements OnInit {
  documents: Document[] = [];
  properties: PropertyDetails[] = [];
  tenants: Tenant[] = [];
  showForm = false;
  newDoc: any = { docType: 'OTHER' };
  viewing: Document | null = null;
  activeType = 'ALL';

  typeTabs = [
    { value: 'ALL', label: 'Tous' },
    { value: 'QUITTANCE', label: 'Quittances' },
    { value: 'LEASE', label: 'Baux' },
    { value: 'IDENTITY', label: 'Identité' },
    { value: 'OTHER', label: 'Autres' },
  ];

  constructor(private api: ApiService) {}

  get visibleDocuments(): Document[] {
    if (this.activeType === 'ALL') return this.documents;
    return this.documents.filter(d => d.docType === this.activeType);
  }

  view(d: Document) { this.viewing = d; }

  print() { window.print(); }

  ngOnInit() {
    this.load();
    this.api.getPropertyDetails().subscribe({ next: p => this.properties = p, error: () => {} });
    this.api.getTenants().subscribe({ next: t => this.tenants = t, error: () => {} });
  }

  load() {
    this.api.getDocuments().subscribe({ next: d => this.documents = d, error: () => {} });
  }

  createDoc() {
    this.api.createDocument({ ...this.newDoc, createdAt: new Date().toISOString().slice(0, 10) } as any).subscribe({
      next: () => { this.showForm = false; this.newDoc = { docType: 'OTHER' }; this.load(); },
      error: () => {}
    });
  }

  deleteDoc(id: number) {
    this.api.deleteDocument(id).subscribe({ next: () => this.load(), error: () => {} });
  }

  typeLabel(t: string): string {
    return t === 'LEASE' ? 'Bail' : t === 'QUITTANCE' ? 'Quittance' : t === 'IDENTITY' ? 'Identité' : 'Autre';
  }
}
