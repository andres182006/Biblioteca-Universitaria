import { Injectable, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { Observable } from 'rxjs';
export interface LoginData {
    token: string;
    tipo: string;
    expriresIn: number;
    correo: string;
    roles: string[];
}
export interface apiResponse <T> {
    data: T;
    message: string;
    status: number;
}
@Injectable({providedIn: 'root'})
export class LoginService {
   private http = inject(HttpClient);
   private apiUrl = 'http://localhost:8080/api-biblioteca/Auth/login';

   login(correo: string, password: string): Observable<any> {
    return this.http.post<any>(this.apiUrl, { correo, contrasena: password });
  }
}
