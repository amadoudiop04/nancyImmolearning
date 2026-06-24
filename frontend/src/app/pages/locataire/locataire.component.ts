import { Component, OnInit } from '@angular/core';
import { ApiService, PropertyDetails, Payment, Document } from '../../services/api.service';

@Component({
  selector: 'app-locataire',
  standalone: true,
  imports: [],
  template: `
    <div style="max-width:1180px;margin:0 auto;padding:34px 32px 60px;">
      <div style="display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:24px;">
        <div>
          <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Espace locataire</div>
          <h1 style="margin:6px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Bonjour, Thomas</h1>
          <p style="margin:6px 0 0;color:#5A655F;font-size:15px;">{{ currentProperty?.location ?? 'Aucun bien associé' }}</p>
        </div>
      </div>

      <div style="display:grid;grid-template-columns:1.5fr 360px;gap:20px;">
        <div>
          <!-- Solde -->
          <div style="background:#0E4F4A;border-radius:18px;padding:26px;color:#fff;">
            <div style="display:flex;justify-content:space-between;align-items:flex-start;">
              <div>
                <div style="font-family:'IBM Plex Mono',monospace;font-size:10.5px;letter-spacing:0.1em;text-transform:uppercase;color:#7FC9BD;">Solde à régler</div>
                <div style="font-size:42px;font-weight:800;letter-spacing:-0.02em;margin-top:8px;">{{ currentRent }}</div>
                <div style="font-size:13.5px;color:#BFE0D9;margin-top:4px;">Loyer + charges · échéance le 5</div>
              </div>
              <span style="background:#E9C46A;color:#3A2E10;font-weight:700;font-size:12px;padding:6px 13px;border-radius:999px;">À régler</span>
            </div>
            <button style="margin-top:22px;width:100%;padding:14px;border:none;border-radius:12px;background:#2A9D8F;color:#fff;font-family:inherit;font-weight:700;font-size:15px;cursor:pointer;">
              Payer en ligne →
            </button>
          </div>

          <!-- Paiements -->
          <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:22px;margin-top:18px;">
            <div style="font-size:16px;font-weight:700;margin-bottom:14px;">Historique des paiements</div>
            @for (p of payments; track p.id) {
              <div style="display:flex;align-items:center;justify-content:space-between;padding:12px 0;border-bottom:1px solid #F2F4F0;font-size:13px;">
                <div>
                  <div style="font-weight:500;">{{ p.propertyName ?? 'Loyer' }} — {{ formatPeriod(p.period) }}</div>
                  <div style="font-size:11px;color:#9AA49E;">{{ p.paidDate ? 'Payé le ' + p.paidDate : '' }}</div>
                </div>
                <div style="display:flex;align-items:center;gap:12px;">
                  <span style="font-family:'IBM Plex Mono',monospace;font-weight:500;">{{ fmt(p.amount) }}</span>
                  <span [style.background]="badgeBg(p.status)" [style.color]="badgeFg(p.status)"
                    style="padding:4px 10px;border-radius:999px;font-size:11px;font-weight:600;">
                    {{ statusLabel(p.status) }}
                  </span>
                </div>
              </div>
            }
            @if (payments.length === 0) {
              <p style="color:#9AA49E;font-size:13px;">Aucun historique de paiement.</p>
            }
          </div>

          <!-- Documents -->
          <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:22px;margin-top:18px;">
            <div style="font-size:16px;font-weight:700;margin-bottom:14px;">Mes documents</div>
            <div style="display:grid;grid-template-columns:1fr 1fr;gap:11px;">
              @for (d of documents; track d.id) {
                <div style="display:flex;align-items:center;gap:12px;padding:12px;border:1px solid #EEF1ED;border-radius:12px;">
                  <div style="width:34px;height:34px;border-radius:9px;background:#E7F1EF;color:#0E4F4A;display:flex;align-items:center;justify-content:center;font-family:'IBM Plex Mono',monospace;font-size:9.5px;flex:none;">PDF</div>
                  <div style="min-width:0;">
                    <div style="font-size:13px;font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">{{ d.name }}</div>
                    <div style="font-size:11px;color:#9AA49E;">{{ d.createdAt }}</div>
                  </div>
                </div>
              }
              @if (documents.length === 0) {
                <p style="color:#9AA49E;font-size:13px;">Aucun document.</p>
              }
            </div>
          </div>
        </div>

        <!-- Right: bien info -->
        <div>
          @if (currentProperty) {
            <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:20px;">
              <div style="position:relative;height:130px;border-radius:13px;background:repeating-linear-gradient(45deg,#EDEFEA,#EDEFEA 11px,#E4E7E2 11px,#E4E7E2 22px);display:flex;align-items:flex-end;padding:11px;">
                <span style="font-family:'IBM Plex Mono',monospace;font-size:10px;color:#9AA49E;background:#fff;padding:2px 7px;border-radius:5px;">photo du logement</span>
              </div>
              <div style="font-weight:700;font-size:16px;margin-top:14px;">{{ currentProperty.name }}</div>
              <div style="font-size:13px;color:#8A938E;margin-top:3px;">{{ currentProperty.kind }} · {{ currentProperty.size }} · {{ currentProperty.location }}</div>
            </div>
          }
        </div>
      </div>
    </div>
  `
})
export class LocataireComponent implements OnInit {
  currentProperty: PropertyDetails | null = null;
  payments: Payment[] = [];
  documents: Document[] = [];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getPropertyDetails().subscribe({
      next: props => {
        this.currentProperty = props.find(p => p.tenant != null) ?? null;
        if (this.currentProperty?.lease) {
          this.api.getPayments({ leaseId: this.currentProperty.lease.id }).subscribe({ next: p => this.payments = p, error: () => {} });
        }
        if (this.currentProperty) {
          this.api.getDocuments({ propertyId: this.currentProperty.id }).subscribe({ next: d => this.documents = d, error: () => {} });
        }
      },
      error: () => {}
    });
  }

  get currentRent(): string {
    if (!this.currentProperty?.lease?.rentAmount) return '—';
    return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: this.currentProperty.lease.currency ?? 'EUR', maximumFractionDigits: 0 }).format(this.currentProperty.lease.rentAmount);
  }

  fmt(v: number): string {
    return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(v ?? 0);
  }

  formatPeriod(period: string): string {
    if (!period) return '';
    const d = new Date(period);
    return d.toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' });
  }

  statusLabel(s: string): string {
    return s === 'PAID' ? 'Payé' : s === 'PENDING' ? 'En attente' : 'En retard';
  }

  badgeBg(s: string): string {
    return s === 'PAID' ? '#D1FAE5' : s === 'PENDING' ? '#FEF3C7' : '#FEE2E2';
  }

  badgeFg(s: string): string {
    return s === 'PAID' ? '#065F46' : s === 'PENDING' ? '#92400E' : '#991B1B';
  }
}
