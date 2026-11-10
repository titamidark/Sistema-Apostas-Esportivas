import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Aposta, ResultadoAposta } from '../models/aposta';

@Injectable({
  providedIn: 'root',
})
export class ApostaService {
  private apiUrl = 'http://localhost:8080/apostas';

  constructor(private http: HttpClient) {}

  listar(): Observable<Aposta[]> {
    return this.http.get<Aposta[]>(this.apiUrl);
  }

  criar(payload: any): Observable<Aposta> {
    return this.http.post<Aposta>(this.apiUrl, payload);
  }

  atualizar(id: number, payload: any): Observable<Aposta> {
    return this.http.put<Aposta>(`${this.apiUrl}/${id}`, payload);
  }

  fazerAposta(id: number): Observable<Aposta> {
    return this.http.post<Aposta>(`${this.apiUrl}/${id}/fazer`, {});
  }

  fechar(
    id: number,
    resultado: ResultadoAposta,
    retornoReal: number,
  ): Observable<Aposta> {
    return this.http.post<Aposta>(`${this.apiUrl}/${id}/fechar`, {
      resultado,
      retornoReal,
    });
  }

  cancelar(id: number): Observable<Aposta> {
    return this.http.post<Aposta>(`${this.apiUrl}/${id}/cancelar`, {});
  }

  reabrir(id: number): Observable<Aposta> {
    return this.http.post<Aposta>(`${this.apiUrl}/${id}/reabrir`, {});
  }
}
