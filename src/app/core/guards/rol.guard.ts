import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const rolGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');
  const misroles: string[] = JSON.parse(localStorage.getItem('roles') || '[]');
  const rolesPermitidos: string[] = route.data['roles']?? [];

  if (token && misroles.some(role => rolesPermitidos.includes(role))) {
    return true;
  }

  return router.createUrlTree(['/login']);
};