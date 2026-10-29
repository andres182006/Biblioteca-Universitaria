import { Routes } from '@angular/router';
import { rolGuard } from './core/guards/rol.guard';
import { LoginComponent } from './pages/login/login';
import { LayoutBibliotecario } from './core/Layout/layout-bibliotecario';
import{Prestamos}from './pages/prestamos/prestamos';
import { Devolusionesprestamos } from './pages/devolusionesprestamos/devolusionesprestamos';
import { Prestamoestudiante } from './pages/prestamoestudiante/prestamoestudiante';
import { Videobeamsprofesores } from './pages/videobeamsprofesores/videobeamsprofesores';
import { Libros } from './pages/libros/libro';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  

   {
    path: '',
    component: LayoutBibliotecario,
    canActivate: [rolGuard],
    data: { roles: ['ROLE_BIBLIOTECARIO', 'ROLE_ADMINISTRADOR'] },
    children: [
      { path: 'prestamos', component: Prestamos },
      { path: 'devoluciones', component: Devolusionesprestamos },
      {path: 'libros', component: Libros}
    ]
  },

  { path: 'prestamos', 
    component: Prestamos ,
    canActivate: [rolGuard],
    data: { roles: ['ROLE_BIBLIOTECARIO'] }
  },
  { path: 'prestamosestudiante',
     component: Prestamoestudiante ,
    canActivate: [rolGuard],
    data: { roles: ['ROLE_ESTUDIANTE'] }
  },
  { path: 'videobeamsprofesores',
     component: Videobeamsprofesores ,
    canActivate: [rolGuard],
    data: { roles: ['ROLE_PROFESOR'] }
  },
  
];