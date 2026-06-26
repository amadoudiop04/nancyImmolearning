import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, PropertyDetails, Payment, Document, StatementLine } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-locataire',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div style="max-width:1180px;margin:0 auto;padding:34px 32px 60px;">
      <div style="display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:24px;">
        <div>
          <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Espace locataire</div>
          <h1 style="margin:6px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Bonjour, {{ userName }}</h1>
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
            <button (click)="openPayment()" [disabled]="!currentProperty?.lease"
              style="margin-top:22px;width:100%;padding:14px;border:none;border-radius:12px;background:#2A9D8F;color:#fff;font-family:inherit;font-weight:700;font-size:15px;cursor:pointer;">
              Payer en ligne →
            </button>
          </div>

          <!-- Situation de compte (débit / crédit / solde) -->
          <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:22px;margin-top:18px;">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:14px;">
              <div style="font-size:16px;font-weight:700;">Situation de compte</div>
              <span [style.background]="balance > 0 ? '#FBE7DF' : '#D1FAE5'" [style.color]="balance > 0 ? '#C2563B' : '#065F46'"
                style="padding:5px 12px;border-radius:999px;font-size:12px;font-weight:700;">
                {{ balance > 0 ? 'Solde dû : ' + fmt(balance) : 'À jour' }}
              </span>
            </div>

            <div style="display:grid;grid-template-columns:64px 1fr 92px 92px 96px;gap:10px;padding:10px 0;font-family:'IBM Plex Mono',monospace;font-size:10px;letter-spacing:0.05em;text-transform:uppercase;color:#9AA49E;border-bottom:1px solid #EEF1ED;">
              <span>Date</span><span>Libellé</span>
              <span style="text-align:right;">Débit</span><span style="text-align:right;">Crédit</span><span style="text-align:right;">Solde</span>
            </div>

            @for (l of statement; track $index) {
              <div class="nm-stmt-row" style="display:grid;grid-template-columns:64px 1fr 92px 92px 96px;gap:10px;padding:12px 0;border-bottom:1px solid #F2F4F0;font-size:13px;align-items:center;transition:background .15s;">
                <span style="font-family:'IBM Plex Mono',monospace;color:#9AA49E;font-size:12px;">{{ shortDate(l.date) }}</span>
                <span style="font-weight:500;">{{ l.label }}</span>
                <span style="text-align:right;font-family:'IBM Plex Mono',monospace;color:#C2563B;">{{ l.debit ? fmt(l.debit) : '' }}</span>
                <span style="text-align:right;font-family:'IBM Plex Mono',monospace;color:#2A9D8F;">{{ l.credit ? fmt(l.credit) : '' }}</span>
                <span style="text-align:right;font-family:'IBM Plex Mono',monospace;font-weight:600;">{{ fmt(l.balance) }}</span>
              </div>
            }
            @if (statement.length === 0) {
              <p style="color:#9AA49E;font-size:13px;margin-top:12px;">Aucun mouvement de compte.</p>
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
              <div style="position:relative;height:130px;border-radius:13px;overflow:hidden;display:flex;align-items:flex-end;padding:11px;"
                [style.background]="currentProperty.imageUrl ? '#EDEFEA' : 'repeating-linear-gradient(45deg,#EDEFEA,#EDEFEA 11px,#E4E7E2 11px,#E4E7E2 22px)'">
                @if (currentProperty.imageUrl) {
                  <img [src]="currentProperty.imageUrl" alt="logement" style="position:absolute;inset:0;width:100%;height:100%;object-fit:cover;">
                } @else {
                  <span style="font-family:'IBM Plex Mono',monospace;font-size:10px;color:#9AA49E;background:#fff;padding:2px 7px;border-radius:5px;">photo du logement</span>
                }
              </div>
              <div style="font-weight:700;font-size:16px;margin-top:14px;">{{ currentProperty.name }}</div>
              <div style="font-size:13px;color:#8A938E;margin-top:3px;">{{ currentProperty.kind }} · {{ currentProperty.size }} · {{ currentProperty.location }}</div>
              @if (currentProperty.description) {
                <div style="margin-top:14px;font-size:13.5px;color:#5A655F;line-height:1.6;">{{ currentProperty.description }}</div>
              }
            </div>
          }
        </div>
      </div>

      <!-- Online card payment modal -->
      @if (showPayment) {
        <div (click)="closePayment()"
          style="position:fixed;inset:0;background:rgba(22,32,29,0.55);z-index:100;display:flex;align-items:center;justify-content:center;padding:24px;">
          <div (click)="$event.stopPropagation()"
            style="background:#fff;border-radius:18px;max-width:440px;width:100%;overflow:hidden;box-shadow:0 30px 70px rgba(0,0,0,0.3);">

            <div style="background:#0E4F4A;color:#fff;padding:22px 24px;">
              <div style="font-family:'IBM Plex Mono',monospace;font-size:10.5px;letter-spacing:0.1em;text-transform:uppercase;color:#7FC9BD;">Paiement sécurisé</div>
              <div style="font-size:26px;font-weight:800;margin-top:6px;">{{ currentRent }}</div>
              <div style="font-size:12.5px;color:#BFE0D9;margin-top:2px;">{{ currentProperty?.name }} · Loyer + charges</div>
            </div>

            @if (paid) {
              <div style="padding:34px;text-align:center;">
                <div style="width:56px;height:56px;border-radius:50%;background:#D1FAE5;color:#065F46;display:flex;align-items:center;justify-content:center;font-size:26px;margin:0 auto 16px;">✓</div>
                <h2 style="margin:0 0 8px;font-size:20px;font-weight:800;">Paiement accepté</h2>
                <p style="margin:0 0 22px;color:#5A655F;font-size:14px;">Votre loyer a été réglé. Une quittance sera disponible sous peu.</p>
                <button (click)="closePayment()" style="width:100%;padding:12px;border:none;border-radius:11px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:700;font-size:14px;cursor:pointer;">Terminé</button>
              </div>
            } @else {
              <form (ngSubmit)="pay()" style="padding:24px;">
                <div style="margin-bottom:14px;">
                  <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Titulaire de la carte</label>
                  <input [(ngModel)]="card.holder" name="holder" placeholder="Thomas Martin" required
                    style="width:100%;padding:12px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
                </div>
                <div style="margin-bottom:14px;">
                  <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Numéro de carte</label>
                  <div style="position:relative;">
                    <input [(ngModel)]="card.number" name="number" (ngModelChange)="formatCardNumber()" placeholder="1234 5678 9012 3456" maxlength="19" inputmode="numeric" required
                      style="width:100%;padding:12px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:'IBM Plex Mono',monospace;font-size:14px;outline:none;letter-spacing:0.05em;">
                    <div style="position:absolute;right:12px;top:50%;transform:translateY(-50%);display:flex;gap:3px;">
                      <span style="width:22px;height:14px;border-radius:3px;background:#E76F51;"></span>
                      <span style="width:22px;height:14px;border-radius:3px;background:#E9C46A;"></span>
                    </div>
                  </div>
                </div>
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:20px;">
                  <div>
                    <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Expiration</label>
                    <input [(ngModel)]="card.expiry" name="expiry" placeholder="MM/AA" maxlength="5" required
                      style="width:100%;padding:12px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:'IBM Plex Mono',monospace;font-size:14px;outline:none;">
                  </div>
                  <div>
                    <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">CVV</label>
                    <input [(ngModel)]="card.cvv" name="cvv" type="password" placeholder="123" maxlength="4" inputmode="numeric" required
                      style="width:100%;padding:12px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:'IBM Plex Mono',monospace;font-size:14px;outline:none;">
                  </div>
                </div>

                @if (payError) { <p style="color:#C2563B;font-size:13px;margin:0 0 14px;">{{ payError }}</p> }

                <button type="submit" [disabled]="processing"
                  style="width:100%;padding:13px;border:none;border-radius:11px;background:#2A9D8F;color:#fff;font-family:inherit;font-weight:700;font-size:15px;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;">
                  @if (processing) { <span>Traitement…</span> } @else {
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M6 11V8a6 6 0 0 1 12 0v3" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/><rect x="4" y="11" width="16" height="10" rx="2" stroke="#fff" stroke-width="1.8"/></svg>
                    <span>Payer {{ currentRent }}</span>
                  }
                </button>
                <p style="text-align:center;margin:12px 0 0;font-size:11.5px;color:#9AA49E;">🔒 Paiement chiffré — démo, aucune carte réelle n'est débitée.</p>
              </form>
            }
          </div>
        </div>
      }
    </div>
  `
})
export class LocataireComponent implements OnInit {
  currentProperty: PropertyDetails | null = null;
  payments: Payment[] = [];
  documents: Document[] = [];
  statement: StatementLine[] = [];
  balance = 0;

  showPayment = false;
  processing = false;
  paid = false;
  payError = '';
  card = { holder: '', number: '', expiry: '', cvv: '' };

  constructor(private api: ApiService, private auth: AuthService) {}

  get userName(): string {
    const u = this.auth.user;
    if (!u) return '';
    return [u.firstName, u.lastName].filter(Boolean).join(' ');
  }

  openPayment() {
    if (!this.currentProperty?.lease) return;
    this.showPayment = true;
    this.paid = false;
    this.payError = '';
    this.card = { holder: '', number: '', expiry: '', cvv: '' };
  }

  closePayment() {
    this.showPayment = false;
  }

  formatCardNumber() {
    const digits = this.card.number.replace(/\D/g, '').slice(0, 16);
    this.card.number = digits.replace(/(.{4})/g, '$1 ').trim();
  }

  pay() {
    this.payError = '';
    const digits = this.card.number.replace(/\D/g, '');
    if (!this.card.holder || digits.length < 16 || !this.card.expiry || this.card.cvv.length < 3) {
      this.payError = 'Veuillez vérifier les informations de votre carte.';
      return;
    }
    const lease = this.currentProperty?.lease;
    if (!lease) return;

    this.processing = true;
    const today = new Date().toISOString().slice(0, 10);
    this.api.createPayment({
      leaseId: lease.id,
      period: today,
      amount: lease.rentAmount,
      status: 'PAID',
      paidDate: today,
    } as any).subscribe({
      next: () => {
        this.processing = false;
        this.paid = true;
        if (lease) {
          this.api.getPayments({ leaseId: lease.id }).subscribe({ next: p => this.payments = p, error: () => {} });
          this.loadStatement();
        }
      },
      error: () => {
        this.processing = false;
        this.payError = 'Le paiement a échoué. Réessayez.';
      }
    });
  }

  ngOnInit() {
    const email = this.auth.user?.email?.toLowerCase();
    this.api.getPropertyDetails().subscribe({
      next: props => {
        // Cible le logement du locataire connecté (par email), sinon le premier occupé.
        this.currentProperty =
          props.find(p => p.tenant?.email?.toLowerCase() === email)
          ?? props.find(p => p.tenant != null)
          ?? null;
        if (this.currentProperty?.lease) {
          this.api.getPayments({ leaseId: this.currentProperty.lease.id }).subscribe({ next: p => this.payments = p, error: () => {} });
          this.loadStatement();
        }
        if (this.currentProperty) {
          this.api.getDocuments({ propertyId: this.currentProperty.id }).subscribe({ next: d => this.documents = d, error: () => {} });
        }
      },
      error: () => {}
    });
  }

  loadStatement() {
    const leaseId = this.currentProperty?.lease?.id;
    if (!leaseId) return;
    this.api.getStatement(leaseId).subscribe({
      next: lines => {
        this.statement = lines;
        this.balance = lines.length ? lines[lines.length - 1].balance : 0;
      },
      error: () => {}
    });
  }

  shortDate(date: string): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' });
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
