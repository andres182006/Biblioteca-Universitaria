import { Component, inject, signal } from '@angular/core';
import{FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import{LoginService} from '../../services/login.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.html'
})


export class LoginComponent  {
private fb = inject(FormBuilder);
private loginService = inject(LoginService);
private router = inject(Router);

cargando = signal(false);
verClave = signal(false);
errorGlobal = signal('');

form = this.fb.nonNullable.group({
  correo: ['', [Validators.required, Validators.email]],
  clave: ['', [Validators.required]]
});

invalido(campo: 'correo' | 'clave'): boolean {
  const c = this.form.controls[campo];
  return c.invalid && (c.touched || c.dirty);
}
textoError(campo: 'correo' | 'clave'): string {
    const c = this.form.controls[campo];
    if (c.hasError('required')) return 'Este campo es obligatorio';
    if (c.hasError('email')) return 'Ingresa un correo válido';
    return '';
  }


entrar() {
    this.errorGlobal.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { correo, clave } = this.form.getRawValue();
    this.cargando.set(true);

    this.loginService.login(correo, clave).subscribe({
      next: (res) => {
        const roles: string[] = res.data.roles.filter((r: string) => r.startsWith('ROLE_'));

        localStorage.setItem('token', res.data.token);
        localStorage.setItem('correo', res.data.correo);
        localStorage.setItem('roles', JSON.stringify(roles));

        if (roles.includes('ROLE_BIBLIOTECARIO') || roles.includes('ROLE_ADMIN')) {
          this.router.navigate(['/prestamos']);
        } else if (roles.includes('ROLE_ESTUDIANTE')) {
          this.router.navigate(['/prestamosestudiante']);
        } else if (roles.includes('ROLE_PROFESOR')) {
          this.router.navigate(['/videobeamsprofesores']);
        } else {
          this.cargando.set(false);
          this.errorGlobal.set('Tu usuario no tiene un rol con acceso a la aplicación.');
        }
      },
      error: (err) => {
        this.cargando.set(false);
        this.errorGlobal.set(
          err.status === 401
            ? 'Correo o contraseña incorrectos'
            : 'No se pudo conectar con el servidor. Inténtalo de nuevo.'
        );
      }
    });
  }
}