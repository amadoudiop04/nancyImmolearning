import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

/** Protège les routes : redirige vers /connexion si aucun token. */
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) return true;

  return router.createUrlTree(['/connexion'], { queryParams: { redirect: state.url } });
};

/** Réserve une route à un rôle précis (BAILLEUR / LOCATAIRE). */
export function roleGuard(role: 'BAILLEUR' | 'LOCATAIRE'): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (!auth.isLoggedIn()) {
      return router.createUrlTree(['/connexion']);
    }
    if (auth.user?.role !== role) {
      // Mauvais espace pour ce rôle : on renvoie vers l'espace adapté.
      const target = auth.user?.role === 'LOCATAIRE' ? '/locataire' : '/bailleur';
      return router.createUrlTree([target]);
    }
    return true;
  };
}
