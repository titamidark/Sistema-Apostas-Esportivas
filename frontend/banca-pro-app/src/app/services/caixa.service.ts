import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  CaixaResumo,
  MovimentacaoCaixa,
  TipoMovimentacaoCaixa,
} from '../models/caixa';

@Injectable({
  providedIn: 'root',
})
export class CaixaService {
  private apiUrl = 'http://localhost:8080/caixa';

  constructor(private http: HttpClient) {}

  resumo(): Observable<CaixaResumo> {
    return this.http.get<CaixaResumo>(`${this.apiUrl}/resumo`);
  }

  listarMovimentacoes(): Observable<MovimentacaoCaixa[]> {
    return this.http.get<MovimentacaoCaixa[]>(`${this.apiUrl}/movimentacoes`);
  }

  movimentar(
    tipo: TipoMovimentacaoCaixa,
    valor: number,
    descricao: string,
  ): Observable<MovimentacaoCaixa> {
    return this.http.post<MovimentacaoCaixa>(`${this.apiUrl}/movimentar`, {
      tipo,
      valor,
      descricao,
    });
  }
}
