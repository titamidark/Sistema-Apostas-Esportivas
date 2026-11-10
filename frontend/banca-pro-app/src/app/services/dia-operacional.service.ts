import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AbrirDiaRequest, DiaOperacional } from '../models/dia-operacional';

@Injectable({
  providedIn: 'root',
})
export class DiaOperacionalService {
  private apiUrl = 'http://localhost:8080/dia-operacional';

  constructor(private http: HttpClient) {}

  atual(): Observable<DiaOperacional> {
    return this.http.get<DiaOperacional>(`${this.apiUrl}/atual`);
  }

  abrir(request: AbrirDiaRequest): Observable<DiaOperacional> {
    return this.http.post<DiaOperacional>(`${this.apiUrl}/abrir`, request);
  }

  fechar(): Observable<DiaOperacional> {
    return this.http.post<DiaOperacional>(`${this.apiUrl}/fechar`, {});
  }

  iniciarProximo(): Observable<DiaOperacional> {
    return this.http.post<DiaOperacional>(`${this.apiUrl}/iniciar-proximo`, {});
  }

  historico(): Observable<DiaOperacional[]> {
    return this.http.get<DiaOperacional[]>(`${this.apiUrl}/historico`);
  }
}
