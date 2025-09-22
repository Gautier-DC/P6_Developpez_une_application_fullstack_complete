import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isAuth = authService.isLoggedIn();
  
  if (isAuth) {
    // if already authenticated, redirect to articles
    router.navigate(['/articles']); 
    return false;
  } else {
    // If not connected, allow access to guest pages (login/register)
    return true;
  }
};
