import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from './prestamo.service';

export interface Estudiante {
  id: number;
  nombrecompleto: string;
  documento: string;
  correo: string;
  telefono: string | null;
  fecharegistro: string | null;
}

export interface UsuarioCuenta {
  id: number;
  nombrecompleto: string;
  documento: string;
  correo: string;
  rol: 'administrador' | 'bibliotecario' | 'profesor' | 'estudiante';
  fecharegistro: string | null;
}

export interface NuevoProfesor {
  nombrecompleto: string;
  documento: string;
  correo: string;
  contrasena: string;
}

export interface NuevoEstudiante extends NuevoProfesor {
  telefono: string | null;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private http = inject(HttpClient);
  private root = 'http://localhost:8080/api-biblioteca';

  listarEstudiantes(): Observable<ApiResponse<Estudiante[]>> {
    return this.http.get<ApiResponse<Estudiante[]>>(`${this.root}/Estudiante`);
  }

  listarUsuarios(): Observable<ApiResponse<UsuarioCuenta[]>> {
    return this.http.get<ApiResponse<UsuarioCuenta[]>>(`${this.root}/Usuario`);
  }

  crearEstudiante(body: NuevoEstudiante): Observable<ApiResponse<Estudiante>> {
    return this.http.post<ApiResponse<Estudiante>>(`${this.root}/Estudiante/crear`, body);
  }

  crearProfesor(body: NuevoProfesor): Observable<ApiResponse<UsuarioCuenta>> {
    return this.http.post<ApiResponse<UsuarioCuenta>>(`${this.root}/Usuario/crear`, body);
  }

  actualizarEstudiante(
    id: number,
    body: { nombrecompleto: string; correo: string; telefono: string | null }
  ): Observable<ApiResponse<Estudiante>> {
    return this.http.put<ApiResponse<Estudiante>>(`${this.root}/Estudiante/${id}`, body);
  }

  actualizarProfesor(
    id: number,
    body: { nombrecompleto: string; correo: string }
  ): Observable<ApiResponse<UsuarioCuenta>> {
    return this.http.put<ApiResponse<UsuarioCuenta>>(`${this.root}/Usuario/${id}`, body);
  }

  eliminarEstudiante(id: number): Observable<unknown> {
    return this.http.delete(`${this.root}/Estudiante/${id}`);
  }
}