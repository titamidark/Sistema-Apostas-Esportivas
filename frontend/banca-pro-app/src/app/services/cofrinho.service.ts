import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Cofrinho, CofrinhoDia, CofrinhoRequest } from '../models/cofrinho';

@Injectable({
  providedIn: 'root',
})
export class CofrinhoService {
  private apiUrl = 'http://localhost:8080/cofrinhos';

  constructor(private http: HttpClient) {}

  listar(): Observable<Cofrinho[]> {
    return this.http.get<Cofrinho[]>(this.apiUrl);
  }

  buscar(id: number): Observable<Cofrinho> {
    return this.http.get<Cofrinho>(`${this.apiUrl}/${id}`);
  }

  criar(payload: CofrinhoRequest): Observable<Cofrinho> {
    return this.http.post<Cofrinho>(this.apiUrl, payload);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  listarDias(cofrinhoId: number): Observable<CofrinhoDia[]> {
    return this.http.get<CofrinhoDia[]>(`${this.apiUrl}/${cofrinhoId}/dias`);
  }

  marcarDia(
    cofrinhoId: number,
    diaId: number,
    status: string,
    valorDepositado: number,
  ): Observable<CofrinhoDia> {
    return this.http.post<CofrinhoDia>(
      `${this.apiUrl}/${cofrinhoId}/dias/${diaId}/marcar`,
      {
        status,
        valorDepositado,
      },
    );
  }

  alterarDiasRestantes(cofrinhoId: number, dias: number): Observable<void> {
    return this.http.put<void>(
      `${this.apiUrl}/${cofrinhoId}/dias-restantes?diasRestantes=${dias}`,
      {},
    );
  }

  adicionarGasto(
    cofrinhoId: number,
    valor: number,
    descricao: string,
  ): Observable<Cofrinho> {
    return this.http.post<Cofrinho>(`${this.apiUrl}/${cofrinhoId}/gastos`, {
      valor,
      descricao,
    });
  }
}
