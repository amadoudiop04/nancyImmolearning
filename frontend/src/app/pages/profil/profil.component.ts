import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Landlord } from '../../services/api.service';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div style="max-width:1000px;margin:0 auto;padding:34px 32px 60px;">
      <div style="margin-bottom:24px;">
        <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Compte</div>
        <h1 style="margin:6px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Mon profil</h1>
      </div>

      <div style="display:grid;grid-template-columns:300px 1fr;gap:20px;align-items:flex-start;">
        <!-- Avatar card -->
        <div style="background:#fff;border:1px solid #E4E7E2;border-radius:18px;padding:26px;text-align:center;">
          <div style="width:84px;height:84px;border-radius:24px;background:#0E4F4A;color:#fff;display:flex;align-items:center;justify-content:center;font-size:28px;font-weight:800;margin:0 auto;">
            {{ initials }}
          </div>
          <div style="font-size:19px;font-weight:800;margin-top:16px;">{{ landlord ? landlord.firstName + ' ' + landlord.lastName : '—' }}</div>
          <div style="font-size:13.5px;color:#8A938E;margin-top:3px;">{{ landlord?.email }}</div>
          <span style="display:inline-block;margin-top:12px;background:#E7F1EF;color:#0E4F4A;font-size:12px;font-weight:600;padding:6px 13px;border-radius:999px;">Bailleur</span>

          @if (!landlord) {
            <button (click)="showCreate=true"
              style="margin-top:18px;width:100%;padding:11px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:13.5px;cursor:pointer;">
              Créer mon profil
            </button>
          }
        </div>

        <div style="display:flex;flex-direction:column;gap:18px;">
          @if (showCreate && !landlord) {
            <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:24px;">
              <div style="font-size:16px;font-weight:700;margin-bottom:18px;">Créer un profil bailleur</div>
              <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
                @for (f of fields; track f.key) {
                  <div>
                    <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">{{ f.label }}</label>
                    <input [(ngModel)]="newLandlord[f.key]" [placeholder]="f.placeholder" [type]="f.type ?? 'text'"
                      style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;">
                  </div>
                }
              </div>
              <button (click)="createLandlord()"
                style="margin-top:14px;padding:11px 22px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
                Enregistrer
              </button>
            </div>
          }

          @if (landlord) {
            <div style="background:#fff;border:1px solid #E4E7E2;border-radius:16px;padding:24px;">
              <div style="font-size:16px;font-weight:700;margin-bottom:18px;">Informations personnelles</div>
              <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
                @for (f of fields; track f.key) {
                  <div>
                    <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">{{ f.label }}</label>
                    <input [ngModel]="$any(landlord)[f.key]" [disabled]="!editing" [type]="f.type ?? 'text'"
                      style="width:100%;padding:11px 13px;border:1px solid #D6DED9;border-radius:10px;font-family:inherit;font-size:14px;outline:none;"
                      [style.background]="editing ? '#fff' : '#F9FAF8'"
                      (ngModelChange)="editLandlord[f.key]=$event">
                  </div>
                }
              </div>
              <div style="display:flex;gap:10px;margin-top:16px;">
                @if (!editing) {
                  <button (click)="editing=true"
                    style="padding:11px 22px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
                    Modifier
                  </button>
                } @else {
                  <button (click)="saveEdit()"
                    style="padding:11px 22px;border:none;border-radius:10px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
                    Enregistrer
                  </button>
                  <button (click)="editing=false"
                    style="padding:11px 22px;border:1px solid #D6DED9;border-radius:10px;background:#fff;color:#16201D;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
                    Annuler
                  </button>
                }
              </div>
            </div>
          }
        </div>
      </div>
    </div>
  `
})
export class ProfilComponent implements OnInit {
  landlord: Landlord | null = null;
  editing = false;
  showCreate = false;
  newLandlord: any = {};
  editLandlord: any = {};

  fields = [
    { key: 'firstName', label: 'Prénom', placeholder: 'Nancy' },
    { key: 'lastName', label: 'Nom', placeholder: 'Aubert' },
    { key: 'email', label: 'Email', placeholder: 'nancy@example.fr' },
    { key: 'phone', label: 'Téléphone', placeholder: '0600000000', type: 'number' },
    { key: 'street', label: 'Rue', placeholder: '12 rue de la Paix' },
    { key: 'city', label: 'Ville', placeholder: 'Nancy' },
    { key: 'zipCode', label: 'Code postal', placeholder: '54000' },
    { key: 'country', label: 'Pays', placeholder: 'France' },
  ];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getLandlords().subscribe({
      next: ls => { this.landlord = ls[0] ?? null; },
      error: () => {}
    });
  }

  get initials(): string {
    if (!this.landlord) return '?';
    return (this.landlord.firstName?.[0] ?? '') + (this.landlord.lastName?.[0] ?? '');
  }

  createLandlord() {
    this.api.createLandlord(this.newLandlord).subscribe({
      next: l => { this.landlord = l; this.showCreate = false; },
      error: () => {}
    });
  }

  saveEdit() {
    if (!this.landlord) return;
    this.api.updateLandlord(this.landlord.id, { ...this.landlord, ...this.editLandlord }).subscribe({
      next: l => { this.landlord = l; this.editing = false; },
      error: () => {}
    });
  }
}
