import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-bailleur-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div style="display:flex;align-items:flex-start;">
      <!-- Sidebar -->
      <aside style="position:sticky;top:64px;align-self:flex-start;width:248px;height:calc(100vh - 64px);background:#fff;border-right:1px solid #E4E7E2;padding:22px 16px;display:flex;flex-direction:column;gap:4px;overflow-y:auto;">
        <div style="font-family:'IBM Plex Mono',monospace;font-size:10px;letter-spacing:0.12em;text-transform:uppercase;color:#9AA49E;padding:6px 12px 10px;">Gestion</div>

        <a routerLink="dashboard" routerLinkActive="sidebar-active" class="sidebar-btn" style="display:flex;align-items:center;gap:10px;padding:10px 12px;border-radius:10px;text-decoration:none;color:#5A655F;font-size:14px;font-weight:500;transition:all .15s;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.7"/><rect x="14" y="3" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.7"/><rect x="3" y="14" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.7"/><rect x="14" y="14" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.7"/></svg>
          Tableau de bord
        </a>
        <a routerLink="biens" routerLinkActive="sidebar-active" class="sidebar-btn" style="display:flex;align-items:center;gap:10px;padding:10px 12px;border-radius:10px;text-decoration:none;color:#5A655F;font-size:14px;font-weight:500;transition:all .15s;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M4 11l8-6.5L20 11v8.5a.5.5 0 0 1-.5.5H15v-6H9v6H4.5a.5.5 0 0 1-.5-.5z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/></svg>
          Mes biens
        </a>
        <a routerLink="locataires" routerLinkActive="sidebar-active" class="sidebar-btn" style="display:flex;align-items:center;gap:10px;padding:10px 12px;border-radius:10px;text-decoration:none;color:#5A655F;font-size:14px;font-weight:500;transition:all .15s;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><circle cx="9" cy="8" r="3.2" stroke="currentColor" stroke-width="1.7"/><path d="M3.5 19c0-3 2.5-5 5.5-5s5.5 2 5.5 5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M16 6.5a3 3 0 0 1 0 5.8M17 19c0-2.3-1-4-2.5-4.6" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
          Locataires
        </a>
        <a routerLink="paiements" routerLinkActive="sidebar-active" class="sidebar-btn" style="display:flex;align-items:center;gap:10px;padding:10px 12px;border-radius:10px;text-decoration:none;color:#5A655F;font-size:14px;font-weight:500;transition:all .15s;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><rect x="3" y="5.5" width="18" height="13" rx="2" stroke="currentColor" stroke-width="1.7"/><path d="M3 9.5h18" stroke="currentColor" stroke-width="1.7"/><path d="M6.5 14.5h4" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
          Paiements
        </a>
        <a routerLink="documents" routerLinkActive="sidebar-active" class="sidebar-btn" style="display:flex;align-items:center;gap:10px;padding:10px 12px;border-radius:10px;text-decoration:none;color:#5A655F;font-size:14px;font-weight:500;transition:all .15s;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M6 3h8l4 4v14a0 0 0 0 1 0 0H6a0 0 0 0 1 0 0z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M13 3v5h5M9 13h6M9 16.5h6" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
          Documents
        </a>

        <div style="margin-top:auto;padding:14px;border-radius:14px;background:#0E4F4A;color:#fff;">
          <div style="font-family:'IBM Plex Mono',monospace;font-size:10px;letter-spacing:0.1em;text-transform:uppercase;color:#7FC9BD;">Automatisation</div>
          <div style="font-size:14px;font-weight:600;margin-top:6px;line-height:1.35;">Quittances générées automatiquement</div>
          <button style="margin-top:12px;width:100%;padding:9px;border:none;border-radius:9px;background:#2A9D8F;color:#fff;font-family:inherit;font-weight:600;font-size:13px;cursor:pointer;">Configurer</button>
        </div>
      </aside>

      <!-- Main content -->
      <main style="flex:1;min-width:0;padding:30px 36px 56px;max-width:1240px;">
        <router-outlet />
      </main>
    </div>

    <style>
      .sidebar-btn:hover { background:#F4F6F3; color:#16201D; }
      .sidebar-active { background:#E7F1EF !important; color:#0E4F4A !important; font-weight:600 !important; }
    </style>
  `
})
export class BailleurLayoutComponent {}
