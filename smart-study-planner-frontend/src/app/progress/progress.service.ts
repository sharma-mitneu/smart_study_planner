import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProgressService {
  private readonly apiUrl = 'http://localhost:8080/api/progress';

  constructor(private http: HttpClient) {}

  log(entry: any): Observable<any> {
    return this.http.post(this.apiUrl, entry);
  }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}
