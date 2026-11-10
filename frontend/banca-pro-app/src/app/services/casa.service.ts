import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Casa } from '../models/casa';
import {
  MovimentacaoCasa,
  TipoMovimentacaoCasa,
} from '../models/movimentacao-casa';

@Injectable({
  providedIn: 'root',
})
export class CasaService {
  private apiUrl = 'http://localhost:8080/casas';

  constructor(private http: HttpClient) {}

  listar(): Observable<Casa[]> {
    return this.http.get<Casa[]>(this.apiUrl);
  }

  criar(casa: Partial<Casa>): Observable<Casa> {
    return this.http.post<Casa>(this.apiUrl, casa);
  }

  atualizar(id: number, casa: Partial<Casa>): Observable<Casa> {
    return this.http.put<Casa>(`${this.apiUrl}/${id}`, casa);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  atualizarSaldo(
    casaId: number,
    novoSaldo: number,
    retiradaPessoal: number,
  ): Observable<Casa> {
    return this.http.post<Casa>(`${this.apiUrl}/${casaId}/atualizar-saldo`, {
      novoSaldo,
      retiradaPessoal,
    });
  }
  resultadoAtualizacaoSaldoHoje(casaId: number): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/${casaId}/resultado-atualizacao-saldo-hoje`,
    );
  }

  valorApostadoAberto(casaId: number): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/${casaId}/valor-apostado-aberto`,
    );
  }

  valorAgendado(casaId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${casaId}/valor-agendado`);
  }

  movimentar(
    casaId: number,
    tipo: TipoMovimentacaoCasa,
    valor: number,
    descricao: string,
  ): Observable<MovimentacaoCasa> {
    return this.http.post<MovimentacaoCasa>(
      `${this.apiUrl}/${casaId}/movimentacoes`,
      { tipo, valor, descricao },
    );
  }

  listarMovimentacoes(casaId: number): Observable<MovimentacaoCasa[]> {
    return this.http.get<MovimentacaoCasa[]>(
      `${this.apiUrl}/${casaId}/movimentacoes`,
    );
  }
}
