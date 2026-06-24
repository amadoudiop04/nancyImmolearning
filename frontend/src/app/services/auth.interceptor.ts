import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from './auth.service';

/** Attache le JWT à chaque requête /api et déconnecte sur 401. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();

  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      // Session expirée / non autorisée : on nettoie et on renvoie vers la connexion,
      // sauf pour les appels d'authentification eux-mêmes.
      if (err.status === 401 && !req.url.includes('/api/auth/')) {
        auth.logout();
        router.navigate(['/connexion']);
      }
      return throwError(() => err);
    })
  );
};
