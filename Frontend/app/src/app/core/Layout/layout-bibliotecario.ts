import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

interface ItemMenu {
  texto: string;
  icono: string;
  ruta: string | null; // null = todavía no existe la pantalla
}

@Component({
  selector: 'app-layout-bibliotecario',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout-bibliotecario.html'
})
export class LayoutBibliotecario {
  private router = inject(Router);

  correo = localStorage.getItem('correo') ?? '';
  esAdmin = (JSON.parse(localStorage.getItem('roles') ?? '[]') as string[])
    .includes('ROLE_ADMINISTRADOR');

  // Cuando crees la pantalla de Libros, cambia ruta: null por ruta: '/libros'
  menu: ItemMenu[] = [
    { texto: 'Solicitudes de préstamo', icono: 'rule_folder', ruta: '/prestamos' },
    { texto: 'Préstamos y devoluciones', icono: 'assignment_turned_in', ruta: '/devoluciones' },
    { texto: 'Libros', icono: 'menu_book', ruta: '/libros' },
    { texto: 'Usuarios', icono: 'group', ruta: null },
    { texto: 'Videobeams', icono: 'videocam', ruta: null },
    { texto: 'Reservas', icono: 'bookmark_border', ruta: null },
  ];

  cerrarSesion() {
    ['token', 'correo', 'roles'].forEach((k) => localStorage.removeItem(k));
    this.router.navigate(['/login']);
  }
}