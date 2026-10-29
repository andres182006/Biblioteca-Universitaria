import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ApiResponse<T> {
  data: T;
  message: string;
  status: number;
}

export interface Prestamo {
  id: number;
  estudiante: {
    id: number;
    nombrecompleto: string;
    documento: string;
    correo: string;
  };
  libro: {
    id: number;
    titulo: string;
    autor: string;
    isbn: string;
    cantidadtotal: number;
    cantidaddisponible: number;
  };
 usuariobibliotecario: { nombrecompleto: string } | null;   
  fechasolicitud: string;
  fechaprestamo: string | null;
  fechadevolucionreal: string | null;
  diasenprestamo: number | null;                              
  estado: 'solicitado' | 'activo' | 'devuelto' | 'rechazado' | 'cancelado';
  motivorechazo: string | null;
}

@Injectable({ providedIn: 'root' })
export class PrestamoService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/Prestamo`;

  historial(): Observable<ApiResponse<Prestamo[]>> {
    return this.http.get<ApiResponse<Prestamo[]>>(`${this.base}/historial`);
  }

  aprobar(id: number): Observable<ApiResponse<Prestamo>> {
    return this.http.put<ApiResponse<Prestamo>>(`${this.base}/${id}/aprobar`, {});
  }
  
  devolucion(id: number): Observable<ApiResponse<Prestamo>> {
  return this.http.put<ApiResponse<Prestamo>>(`${this.base}/${id}/devolucion`, {});
}

  rechazar(id: number, motivo?: string): Observable<ApiResponse<Prestamo>> {
    return this.http.put<ApiResponse<Prestamo>>(`${this.base}/${id}/rechazar`, {
      motivo: motivo || null
    });
  }
}