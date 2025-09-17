import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isAuth = authService.isLoggedIn();
  
  if (isAuth) {
    // Si déjà connecté, rediriger vers la page d'accueil authentifiée
    router.navigate(['/articles']); // ou toute autre page par défaut
    return false;
  } else {
    // Si pas connecté, autoriser l'accès aux pages guest (login/register)
    return true;
  }
};
