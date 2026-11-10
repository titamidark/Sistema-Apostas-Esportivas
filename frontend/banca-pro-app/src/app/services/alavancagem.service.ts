import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  CicloAlavancagem,
  ProjetoAlavancagem,
  ProjetoAlavancagemRequest,
} from '../models/alavancagem';

@Injectable({
  providedIn: 'root',
})
export class AlavancagemService {
  private apiUrl = 'http://localhost:8080/alavancagens';

  constructor(private http: HttpClient) {}

  listar(): Observable<ProjetoAlavancagem[]> {
    return this.http.get<ProjetoAlavancagem[]>(this.apiUrl);
  }

  listarAtivos(): Observable<ProjetoAlavancagem[]> {
    return this.http.get<ProjetoAlavancagem[]>(`${this.apiUrl}/ativos`);
  }

  criar(payload: ProjetoAlavancagemRequest): Observable<ProjetoAlavancagem> {
    return this.http.post<ProjetoAlavancagem>(this.apiUrl, payload);
  }

  proximaAposta(id: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${id}/proxima-aposta`);
  }

  listarCiclos(id: number): Observable<CicloAlavancagem[]> {
    return this.http.get<CicloAlavancagem[]>(`${this.apiUrl}/${id}/ciclos`);
  }
}
