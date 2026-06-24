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

      <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:14px;">
        @for (d of documents; track d.id) {
          <div style="background:#fff;border:1px solid #E4E7E2;border-radius:14px;padding:18px;display:flex;gap:13px;align-items:center;">
            <div style="width:40px;height:40px;border-radius:10px;background:#E7F1EF;color:#0E4F4A;display:flex;align-items:center;justify-content:center;font-family:'IBM Plex Mono',monospace;font-size:10px;flex:none;">PDF</div>
            <div style="min-width:0;flex:1;">
              <div style="font-weight:600;font-size:14px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">{{ d.name }}</div>
              <div style="font-size:11.5px;color:#9AA49E;">{{ typeLabel(d.docType) }} · {{ d.createdAt }}</div>
            </div>
            <button (click)="deleteDoc(d.id)"
              style="border:none;background:transparent;color:#C2563B;cursor:pointer;font-size:13px;flex:none;">✕</button>
          </div>
        }
        @if (documents.length === 0) {
          <div style="grid-column:1/-1;text-align:center;padding:48px;color:#9AA49E;">Aucun document archivé.</div>
        }
      </div>
    </div>
  `
})
export class DocumentsComponent implements OnInit {
  documents: Document[] = [];
  properties: PropertyDetails[] = [];
  tenants: Tenant[] = [];
  showForm = false;
  newDoc: any = { docType: 'OTHER' };

  constructor(private api: ApiService) {}

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
