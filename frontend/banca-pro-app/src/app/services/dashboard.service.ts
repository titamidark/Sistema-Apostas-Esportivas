import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { DashboardResumo } from '../models/dashboard-resumo';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/dashboard';

  constructor(private http: HttpClient) {}

  resumo(): Observable<DashboardResumo> {
    return this.http.get<DashboardResumo>(`${this.apiUrl}/resumo`);
  }

  zerarTudo(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/zerar-tudo`, {});
  }
}
