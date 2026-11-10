import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Bilhete, CriarBilheteRequest } from '../models/bilhete';

@Injectable({
  providedIn: 'root',
})
export class BilheteService {
  private readonly apiUrl = 'http://localhost:8080/bilhetes';

  constructor(private http: HttpClient) {}

  listar(): Observable<Bilhete[]> {
    return this.http.get<Bilhete[]>(this.apiUrl);
  }

  criar(request: CriarBilheteRequest): Observable<Bilhete> {
    return this.http.post<Bilhete>(this.apiUrl, request);
  }
}
