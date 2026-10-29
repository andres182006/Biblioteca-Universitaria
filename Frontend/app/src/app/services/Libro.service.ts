import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from './prestamo.service';
import { environment } from '../../environments/environment';

export interface Libro {
  id: number;
  titulo: string;
  autor: string;
  isbn: string | null;
  categoria: string | null;
  cantidadtotal: number;
  cantidaddisponible: number;
}

export interface LibroRequest {
  titulo: string;
  autor: string;
  isbn: string | null;
  categoria: string | null;
  cantidadtotal: number;
}

@Injectable({ providedIn: 'root' })
export class LibroService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/Libro`;

  listar(): Observable<ApiResponse<Libro[]>> {
    return this.http.get<ApiResponse<Libro[]>>(this.base);
  }

  crear(body: LibroRequest): Observable<ApiResponse<Libro>> {
    return this.http.post<ApiResponse<Libro>>(`${this.base}/Crear`, body);
  }

  actualizar(id: number, body: LibroRequest): Observable<ApiResponse<Libro>> {
    return this.http.put<ApiResponse<Libro>>(`${this.base}/${id}`, body);
  }

  eliminar(id: number): Observable<unknown> {
    return this.http.delete(`${this.base}/${id}`);
  }
}