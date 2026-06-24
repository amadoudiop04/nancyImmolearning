import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-site-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div style="min-height:100vh;background:#F4F6F3;font-family:'Hanken Grotesk',sans-serif;">

      @if (!isAuthRoute) {
        <!-- Global Top Bar -->
        <header style="position:sticky;top:0;z-index:50;height:64px;background:rgba(255,255,255,0.92);
          backdrop-filter:blur(10px);border-bottom:1px solid #E4E7E2;display:flex;align-items:center;
          gap:28px;padding:0 28px;">

          <a routerLink="/" style="display:flex;align-items:center;gap:11px;text-decoration:none;color:inherit;">
            <div style="width:30px;height:30px;border-radius:9px;background:#0E4F4A;display:flex;align-items:center;justify-content:center;">
              <svg width="17" height="17" viewBox="0 0 24 24" fill="none">
                <path d="M4 11.5L12 5l8 6.5V20a1 1 0 0 1-1 1h-4v-6h-6v6H5a1 1 0 0 1-1-1z" fill="#2A9D8F"/>
              </svg>
            </div>
            <span style="font-weight:800;font-size:18px;letter-spacing:-0.02em;">Nancy<span style="color:#2A9D8F;">Immo</span></span>
          </a>

          <nav style="display:flex;gap:6px;margin-left:8px;">
            <a routerLink="/" routerLinkActive="nav-active" [routerLinkActiveOptions]="{exact:true}"
              style="padding:7px 14px;border-radius:8px;font-size:14px;font-weight:500;text-decoration:none;color:#5A655F;transition:all .15s;"
              class="nav-btn">Accueil</a>
            <a routerLink="/recherche" routerLinkActive="nav-active"
              style="padding:7px 14px;border-radius:8px;font-size:14px;font-weight:500;text-decoration:none;color:#5A655F;transition:all .15s;"
              class="nav-btn">Rechercher un bien</a>
            <a routerLink="/bailleur" routerLinkActive="nav-active"
              style="padding:7px 14px;border-radius:8px;font-size:14px;font-weight:500;text-decoration:none;color:#5A655F;transition:all .15s;"
              class="nav-btn">Espace bailleur</a>
            <a routerLink="/locataire" routerLinkActive="nav-active"
              style="padding:7px 14px;border-radius:8px;font-size:14px;font-weight:500;text-decoration:none;color:#5A655F;transition:all .15s;"
              class="nav-btn">Espace locataire</a>
          </nav>

          <div style="margin-left:auto;display:flex;align-items:center;gap:12px;">
            @if (user(); as u) {
              <a routerLink="/profil" title="Mon profil"
                style="width:36px;height:36px;border-radius:50%;background:#E7F1EF;color:#0E4F4A;
                  display:flex;align-items:center;justify-content:center;font-weight:700;font-size:13px;
                  cursor:pointer;text-decoration:none;">{{ initials(u) }}</a>
            } @else {
              <a routerLink="/connexion"
                style="padding:8px 16px;border-radius:9px;font-size:14px;font-weight:600;text-decoration:none;color:#5A655F;">Connexion</a>
              <a routerLink="/inscription"
                style="padding:8px 16px;border-radius:9px;font-size:14px;font-weight:700;text-decoration:none;color:#fff;background:#0E4F4A;">Inscription</a>
            }
          </div>
        </header>
      }

      <main>
        <router-outlet />
      </main>
    </div>

    <style>
      .nav-btn:hover { background:#F4F6F3; color:#16201D; }
      .nav-active { background:#E7F1EF !important; color:#0E4F4A !important; font-weight:600 !important; }
    </style>
  `
})
export class SiteLayoutComponent {
  constructor(private router: Router, private auth: AuthService) {}

  get isAuthRoute(): boolean {
    const url = this.router.url.split('?')[0];
    return url === '/connexion' || url === '/inscription';
  }

  user = this.auth.currentUser;

  initials(u: { firstName: string; lastName: string }): string {
    return (u.firstName?.[0] ?? '') + (u.lastName?.[0] ?? '');
  }
}
