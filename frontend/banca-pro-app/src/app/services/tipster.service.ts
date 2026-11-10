import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Tipster } from '../models/tipster';

@Injectable({
  providedIn: 'root',
})
export class TipsterService {
  private apiUrl = 'http://localhost:8080/tipsters';

  constructor(private http: HttpClient) {}

  listar(): Observable<Tipster[]> {
    return this.http.get<Tipster[]>(this.apiUrl);
  }

  criar(nome: string): Observable<Tipster> {
    return this.http.post<Tipster>(this.apiUrl, {
      nome,
      ativo: true,
    });
  }
}
