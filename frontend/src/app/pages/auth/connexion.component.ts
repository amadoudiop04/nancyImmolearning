import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-connexion',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="nm-auth-split" style="min-height:100vh;display:grid;grid-template-columns:1fr 1fr;">
      <!-- Left brand panel -->
      <div class="nm-auth-brand" style="background:#0E4F4A;color:#fff;padding:56px 64px;display:flex;flex-direction:column;justify-content:space-between;position:relative;overflow:hidden;">
        <a routerLink="/" style="display:flex;align-items:center;gap:11px;position:relative;z-index:2;text-decoration:none;color:#fff;">
          <div style="width:32px;height:32px;border-radius:9px;background:#2A9D8F;display:flex;align-items:center;justify-content:center;">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M4 11.5L12 5l8 6.5V20a1 1 0 0 1-1 1h-4v-6h-6v6H5a1 1 0 0 1-1-1z" fill="#fff"/></svg>
          </div>
          <span style="font-weight:800;font-size:19px;">Nancy<span style="color:#7FC9BD;">Immo</span></span>
        </a>
        <div style="position:relative;z-index:2;">
          <h2 style="font-size:38px;line-height:1.1;font-weight:800;letter-spacing:-0.02em;margin:0;">La gestion locative,<br>enfin entre vos mains.</h2>
          <p style="font-size:16px;color:#BFE0D9;line-height:1.6;margin:18px 0 0;max-width:380px;">Biens, locataires, paiements et quittances réunis dans une seule interface. Sans agence, sans commission.</p>
          <div style="display:flex;gap:28px;margin-top:34px;">
            <div><div style="font-size:24px;font-weight:800;">15+</div><div style="font-size:13px;color:#7FC9BD;">biens gérés</div></div>
            <div><div style="font-size:24px;font-weight:800;">0%</div><div style="font-size:13px;color:#7FC9BD;">commission</div></div>
            <div><div style="font-size:24px;font-weight:800;">12 min</div><div style="font-size:13px;color:#7FC9BD;">pour démarrer</div></div>
          </div>
        </div>
        <div style="font-size:12.5px;color:#5E9991;position:relative;z-index:2;">© 2026 Nancy Immo</div>
        <div style="position:absolute;right:-120px;bottom:-120px;width:380px;height:380px;border-radius:50%;background:rgba(42,157,143,0.25);"></div>
        <div style="position:absolute;right:60px;top:-90px;width:220px;height:220px;border-radius:50%;background:rgba(42,157,143,0.16);"></div>
      </div>

      <!-- Right form panel -->
      <div style="background:#F4F6F3;display:flex;align-items:center;justify-content:center;padding:48px 32px;">
        <div style="width:100%;max-width:400px;">
          <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Bon retour</div>
          <h1 style="margin:8px 0 0;font-size:28px;font-weight:800;letter-spacing:-0.02em;">Connexion</h1>
          <p style="margin:8px 0 0;color:#5A655F;font-size:14.5px;">Accédez à votre espace Nancy Immo.</p>

          <form (ngSubmit)="login()" style="margin-top:26px;">
            <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Email</label>
            <input [(ngModel)]="email" name="email" type="email" placeholder="vous@email.fr" required
              style="width:100%;padding:13px 14px;border:1px solid #D6DED9;border-radius:11px;font-family:inherit;font-size:14.5px;outline:none;background:#fff;margin-bottom:16px;">

            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px;">
              <label style="font-size:12.5px;font-weight:600;color:#5A655F;">Mot de passe</label>
              <span (click)="openReset()" style="font-size:12.5px;color:#2A9D8F;font-weight:600;cursor:pointer;">Oublié ?</span>
            </div>
            <div style="position:relative;">
              <input [(ngModel)]="password" name="password" [type]="showPassword ? 'text' : 'password'" placeholder="••••••••" required
                style="width:100%;padding:13px 44px 13px 14px;border:1px solid #D6DED9;border-radius:11px;font-family:inherit;font-size:14.5px;outline:none;background:#fff;">
              <button type="button" (click)="showPassword = !showPassword"
                [attr.aria-label]="showPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'"
                style="position:absolute;right:6px;top:50%;transform:translateY(-50%);background:transparent;border:none;cursor:pointer;padding:8px;display:flex;align-items:center;color:#9AA49E;">
                @if (showPassword) {
                  <svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M3 3l18 18" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M10.6 10.6a2 2 0 0 0 2.8 2.8" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/><path d="M9.4 5.2A9.5 9.5 0 0 1 12 5c5 0 9 4.5 9 7 0 .9-.7 2.2-1.9 3.4M6.3 6.3C3.9 7.7 3 9.8 3 12c0 0 2.7 5 9 5 1.2 0 2.3-.2 3.2-.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>
                } @else {
                  <svg width="19" height="19" viewBox="0 0 24 24" fill="none"><path d="M3 12s3-7 9-7 9 7 9 7-3 7-9 7-9-7-9-7z" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><circle cx="12" cy="12" r="2.6" stroke="currentColor" stroke-width="1.7"/></svg>
                }
              </button>
            </div>

            <label style="display:flex;align-items:center;gap:9px;margin-top:16px;font-size:13.5px;color:#5A655F;cursor:pointer;">
              <input [(ngModel)]="rememberMe" name="rememberMe" type="checkbox" style="width:16px;height:16px;accent-color:#0E4F4A;"> Rester connecté
            </label>

            @if (error) { <p style="color:#C2563B;font-size:13px;margin:14px 0 0;">{{ error }}</p> }

            <button type="submit" [disabled]="loading"
              style="margin-top:22px;width:100%;padding:14px;border:none;border-radius:12px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:700;font-size:15px;cursor:pointer;">
              {{ loading ? 'Connexion…' : 'Se connecter' }}
            </button>
          </form>

          <div style="display:flex;align-items:center;gap:12px;margin:22px 0;">
            <div style="flex:1;height:1px;background:#E4E7E2;"></div>
            <span style="font-size:12px;color:#9AA49E;">ou</span>
            <div style="flex:1;height:1px;background:#E4E7E2;"></div>
          </div>
          <button type="button" disabled title="Bientôt disponible"
            style="width:100%;padding:13px;border:1px solid #D6DED9;border-radius:12px;background:#fff;color:#16201D;font-family:inherit;font-weight:600;font-size:14px;cursor:not-allowed;opacity:0.75;display:flex;align-items:center;justify-content:center;gap:9px;">
            <svg width="17" height="17" viewBox="0 0 24 24"><path fill="#4285F4" d="M22.5 12.2c0-.7-.1-1.4-.2-2H12v3.9h5.9a5 5 0 0 1-2.2 3.3v2.7h3.6c2-1.9 3.2-4.7 3.2-7.9z"/><path fill="#34A853" d="M12 23c2.9 0 5.4-1 7.2-2.6l-3.6-2.7c-1 .7-2.2 1-3.6 1-2.8 0-5.2-1.9-6-4.4H2.3v2.8A11 11 0 0 0 12 23z"/><path fill="#FBBC05" d="M6 14.3a6.6 6.6 0 0 1 0-4.2V7.3H2.3a11 11 0 0 0 0 9.8z"/><path fill="#EA4335" d="M12 5.4c1.6 0 3 .5 4.1 1.6l3.1-3.1A11 11 0 0 0 2.3 7.3l3.7 2.8c.9-2.6 3.3-4.5 6-4.5z"/></svg>
            Continuer avec Google
          </button>

          <p style="text-align:center;margin:24px 0 0;font-size:13.5px;color:#5A655F;">
            Pas encore de compte ?
            <a routerLink="/inscription" style="color:#2A9D8F;font-weight:700;text-decoration:none;">Créer un compte</a>
          </p>
        </div>
      </div>

      <!-- Modale : mot de passe oublié -->
      @if (showReset) {
        <div (click)="closeReset()"
          style="position:fixed;inset:0;background:rgba(14,40,37,0.55);display:flex;align-items:center;justify-content:center;padding:20px;z-index:50;">
          <div (click)="$event.stopPropagation()"
            style="background:#fff;border-radius:18px;width:100%;max-width:420px;padding:30px 30px 26px;box-shadow:0 24px 60px rgba(0,0,0,0.25);">
            <div style="display:flex;justify-content:space-between;align-items:flex-start;">
              <div>
                <div style="font-family:'IBM Plex Mono',monospace;font-size:11px;letter-spacing:0.1em;text-transform:uppercase;color:#9AA49E;">Réinitialisation</div>
                <h2 style="margin:6px 0 0;font-size:21px;font-weight:800;letter-spacing:-0.02em;">Mot de passe oublié</h2>
              </div>
              <button type="button" (click)="closeReset()" aria-label="Fermer"
                style="background:transparent;border:none;cursor:pointer;color:#9AA49E;font-size:22px;line-height:1;padding:2px 4px;">×</button>
            </div>

            @if (resetStep === 1) {
              <p style="margin:12px 0 0;color:#5A655F;font-size:14px;line-height:1.5;">Entrez l'email de votre compte. Nous générons un lien de réinitialisation.</p>
              <form (ngSubmit)="requestReset()" style="margin-top:18px;">
                <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Email</label>
                <input [(ngModel)]="resetEmail" name="resetEmail" type="email" placeholder="vous@email.fr" required
                  style="width:100%;padding:13px 14px;border:1px solid #D6DED9;border-radius:11px;font-family:inherit;font-size:14.5px;outline:none;background:#fff;">
                @if (resetMsg) { <p style="color:#2A7A6F;font-size:13px;margin:14px 0 0;">{{ resetMsg }}</p> }
                @if (resetError) { <p style="color:#C2563B;font-size:13px;margin:14px 0 0;">{{ resetError }}</p> }
                <button type="submit" [disabled]="resetLoading"
                  style="margin-top:18px;width:100%;padding:13px;border:none;border-radius:12px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:700;font-size:14.5px;cursor:pointer;">
                  {{ resetLoading ? 'Vérification…' : 'Continuer' }}
                </button>
              </form>
            } @else {
              <p style="margin:12px 0 0;color:#5A655F;font-size:14px;line-height:1.5;">Compte trouvé. Choisissez un nouveau mot de passe (6 caractères minimum).</p>
              <form (ngSubmit)="confirmReset()" style="margin-top:18px;">
                <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Nouveau mot de passe</label>
                <input [(ngModel)]="resetNewPassword" name="resetNewPassword" type="password" placeholder="••••••••" required
                  style="width:100%;padding:13px 14px;border:1px solid #D6DED9;border-radius:11px;font-family:inherit;font-size:14.5px;outline:none;background:#fff;margin-bottom:14px;">
                <label style="font-size:12.5px;font-weight:600;color:#5A655F;margin-bottom:6px;display:block;">Confirmer le mot de passe</label>
                <input [(ngModel)]="resetConfirm" name="resetConfirm" type="password" placeholder="••••••••" required
                  style="width:100%;padding:13px 14px;border:1px solid #D6DED9;border-radius:11px;font-family:inherit;font-size:14.5px;outline:none;background:#fff;">
                @if (resetError) { <p style="color:#C2563B;font-size:13px;margin:14px 0 0;">{{ resetError }}</p> }
                <button type="submit" [disabled]="resetLoading"
                  style="margin-top:18px;width:100%;padding:13px;border:none;border-radius:12px;background:#0E4F4A;color:#fff;font-family:inherit;font-weight:700;font-size:14.5px;cursor:pointer;">
                  {{ resetLoading ? 'Enregistrement…' : 'Réinitialiser et se connecter' }}
                </button>
              </form>
            }
          </div>
        </div>
      }
    </div>
  `
})
export class ConnexionComponent {
  email = '';
  password = '';
  showPassword = false;
  rememberMe = true;
  loading = false;
  error = '';

  // Flux « mot de passe oublié »
  showReset = false;
  resetStep = 1;
  resetEmail = '';
  resetToken = '';
  resetNewPassword = '';
  resetConfirm = '';
  resetMsg = '';
  resetError = '';
  resetLoading = false;

  constructor(private auth: AuthService, private router: Router, private route: ActivatedRoute) {}

  openReset() {
    this.showReset = true;
    this.resetStep = 1;
    this.resetEmail = this.email;
    this.resetToken = '';
    this.resetNewPassword = '';
    this.resetConfirm = '';
    this.resetMsg = '';
    this.resetError = '';
  }

  closeReset() {
    this.showReset = false;
  }

  requestReset() {
    if (!this.resetEmail) {
      this.resetError = 'Veuillez renseigner votre email.';
      return;
    }
    this.resetLoading = true;
    this.resetError = '';
    this.resetMsg = '';
    this.auth.forgotPassword(this.resetEmail.trim().toLowerCase()).subscribe({
      next: (res) => {
        this.resetLoading = false;
        if (res.resetToken) {
          this.resetToken = res.resetToken;
          this.resetStep = 2;
        } else {
          this.resetMsg = res.message || "Si un compte existe, un lien a été généré.";
        }
      },
      error: () => {
        this.resetLoading = false;
        this.resetError = "Une erreur est survenue. Veuillez réessayer.";
      }
    });
  }

  confirmReset() {
    if (!this.resetNewPassword || this.resetNewPassword.length < 6) {
      this.resetError = 'Mot de passe trop court (6 caractères minimum).';
      return;
    }
    if (this.resetNewPassword !== this.resetConfirm) {
      this.resetError = 'Les deux mots de passe ne correspondent pas.';
      return;
    }
    this.resetLoading = true;
    this.resetError = '';
    this.auth.resetPassword(this.resetToken, this.resetNewPassword).subscribe({
      next: (user) => {
        this.resetLoading = false;
        this.showReset = false;
        this.router.navigate([user.role === 'LOCATAIRE' ? '/locataire' : '/bailleur']);
      },
      error: (err) => {
        this.resetLoading = false;
        this.resetError = err?.error?.message || 'Lien invalide ou expiré. Refaites une demande.';
      }
    });
  }

  login() {
    if (!this.email || !this.password) {
      this.error = 'Veuillez renseigner votre email et votre mot de passe.';
      return;
    }
    this.loading = true;
    this.error = '';

    this.auth.login(this.email.trim().toLowerCase(), this.password).subscribe({
      next: (user) => {
        this.loading = false;
        const redirect = this.route.snapshot.queryParamMap.get('redirect');
        if (redirect) {
          this.router.navigateByUrl(redirect);
        } else {
          this.router.navigate([user.role === 'LOCATAIRE' ? '/locataire' : '/bailleur']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Email ou mot de passe incorrect.';
      }
    });
  }
}
