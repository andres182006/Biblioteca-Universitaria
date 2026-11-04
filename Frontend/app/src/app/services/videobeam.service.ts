import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from './prestamo.service';

export type EstadoVideobeam = 'disponible' | 'reservado' | 'mantenimiento';

export interface Videobeam {
  id: number;
  codigo: string;
  descripcion: string | null;
  estado: EstadoVideobeam;
}

export interface VideobeamRequest {
  codigo: string;
  descripcion: string | null;
  estado: EstadoVideobeam;
}

@Injectable({ providedIn: 'root' })
export class VideobeamService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api-biblioteca/Videobeam';

  listar(): Observable<ApiResponse<Videobeam[]>> {
    return this.http.get<ApiResponse<Videobeam[]>>(this.base);
  }

  crear(body: VideobeamRequest): Observable<ApiResponse<Videobeam>> {
    return this.http.post<ApiResponse<Videobeam>>(`${this.base}/crear`, body);
  }

  actualizar(id: number, body: VideobeamRequest): Observable<ApiResponse<Videobeam>> {
    return this.http.put<ApiResponse<Videobeam>>(`${this.base}/${id}`, body);
  }

  eliminar(id: number): Observable<unknown> {
    return this.http.delete(`${this.base}/${id}`);
  }
}