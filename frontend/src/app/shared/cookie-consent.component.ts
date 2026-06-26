import { Component, signal } from '@angular/core';

const KEY = 'nancyimmo_cookie_consent';

@Component({
  selector: 'app-cookie-consent',
  standalone: true,
  template: `
    @if (visible()) {
      <div style="position:fixed;left:0;right:0;bottom:0;z-index:1100;display:flex;justify-content:center;padding:16px;pointer-events:none;animation:nm-cookie .35s cubic-bezier(.2,.8,.2,1);">
        <div style="pointer-events:auto;background:#16201D;color:#fff;border-radius:16px;max-width:880px;width:100%;
          padding:20px 22px;display:flex;align-items:center;gap:20px;flex-wrap:wrap;box-shadow:0 20px 50px rgba(0,0,0,0.3);">

          <div style="flex:1;min-width:240px;">
            <div style="display:flex;align-items:center;gap:9px;font-weight:700;font-size:15px;">
              <span style="font-size:18px;">🍪</span> Cookies
            </div>
            <p style="margin:7px 0 0;font-size:13.5px;color:#BFC9C5;line-height:1.55;">
              Nancy Immo utilise des cookies pour assurer votre connexion et améliorer votre expérience.
              Vous pouvez accepter ou refuser les cookies non essentiels.
              @if (showDetails()) {
                <br><span style="color:#9AA49E;font-size:12.5px;">Essentiels : session de connexion (obligatoires). Mesure d'audience : désactivée par défaut.</span>
              }
            </p>
            <button (click)="showDetails.set(!showDetails())"
              style="margin-top:6px;background:transparent;border:none;color:#7FC9BD;font-family:inherit;font-size:12.5px;font-weight:600;cursor:pointer;padding:0;">
              {{ showDetails() ? 'Masquer les détails' : 'En savoir plus' }}
            </button>
          </div>

          <div style="display:flex;gap:10px;flex-wrap:wrap;">
            <button (click)="choose('refused')"
              style="padding:11px 20px;border:1px solid #3A4A45;border-radius:11px;background:transparent;color:#fff;font-family:inherit;font-weight:600;font-size:14px;cursor:pointer;">
              Refuser
            </button>
            <button (click)="choose('accepted')"
              style="padding:11px 22px;border:none;border-radius:11px;background:#2A9D8F;color:#fff;font-family:inherit;font-weight:700;font-size:14px;cursor:pointer;">
              Tout accepter
            </button>
          </div>
        </div>
      </div>
    }

    <style>
      @keyframes nm-cookie { from { opacity:0; transform: translateY(24px); } to { opacity:1; transform:none; } }
    </style>
  `
})
export class CookieConsentComponent {
  showDetails = signal(false);
  visible = signal(!localStorage.getItem(KEY));

  choose(choice: 'accepted' | 'refused') {
    localStorage.setItem(KEY, choice);
    localStorage.setItem(KEY + '_date', new Date().toISOString());
    this.visible.set(false);
  }
}
