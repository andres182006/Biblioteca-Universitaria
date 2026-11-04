import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from './prestamo.service';

export type EstadoReserva = 'pendiente' | 'entregado' | 'devuelto' | 'cancelado';

export interface Reserva {
  id: number;
  usuarioprofesor: { id: number; nombrecompleto: string; documento: string; correo: string };
  videobeam: { id: number; codigo: string; descripcion: string | null; estado: string };
  fechareserva: string;   // yyyy-MM-dd
  horainicio: string;     // HH:mm:ss
  horafin: string;        // HH:mm:ss
  motivo: string | null;
  estado: EstadoReserva;
  fechacreacion: string | null;
}

export interface ReservaRequest {
  idvideobeam: number;
  fechareserva: string;
  horainicio: string;
  horafin: string;
  motivo: string | null;
}

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api-biblioteca/Reserva';

  listar(): Observable<ApiResponse<Reserva[]>> {
    return this.http.get<ApiResponse<Reserva[]>>(this.base);
  }

  actualizar(id: number, body: ReservaRequest): Observable<ApiResponse<Reserva>> {
    return this.http.put<ApiResponse<Reserva>>(`${this.base}/${id}`, body);
  }

  entregar(id: number): Observable<ApiResponse<Reserva>> {
    return this.http.put<ApiResponse<Reserva>>(`${this.base}/${id}/entregar`, {});
  }

  devolver(id: number): Observable<ApiResponse<Reserva>> {
    return this.http.put<ApiResponse<Reserva>>(`${this.base}/${id}/devolver`, {});
  }

  cancelar(id: number): Observable<ApiResponse<Reserva>> {
    return this.http.put<ApiResponse<Reserva>>(`${this.base}/${id}/cancelar`, {});
  }
}