import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { MetodoRobo, Robo } from '../models/robo';

@Injectable({
  providedIn: 'root',
})
export class RoboService {
  private apiUrl = 'http://localhost:8080/robos';

  constructor(private http: HttpClient) {}

  listarRobos(): Observable<Robo[]> {
    return this.http.get<Robo[]>(this.apiUrl);
  }

  criarRobo(nome: string): Observable<Robo> {
    return this.http.post<Robo>(this.apiUrl, {
      nome,
      ativo: true,
    });
  }

  listarMetodos(): Observable<MetodoRobo[]> {
    return this.http.get<MetodoRobo[]>(`${this.apiUrl}/metodos`);
  }

  listarMetodosPorRobo(roboId: number): Observable<MetodoRobo[]> {
    return this.http.get<MetodoRobo[]>(`${this.apiUrl}/${roboId}/metodos`);
  }

  criarMetodo(
    roboId: number,
    nome: string,
    descricao: string,
  ): Observable<MetodoRobo> {
    return this.http.post<MetodoRobo>(`${this.apiUrl}/metodos`, {
      roboId,
      nome,
      descricao,
      ativo: true,
    });
  }
}
