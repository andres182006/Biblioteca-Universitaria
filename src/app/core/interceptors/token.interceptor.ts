import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');
  const esLogin = req.url.includes('/Auth/login');

  const peticion = token && !esLogin
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((err) => {
      if (err.status === 401 && !esLogin) {
        ['token', 'correo', 'roles'].forEach((k) => localStorage.removeItem(k));
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};