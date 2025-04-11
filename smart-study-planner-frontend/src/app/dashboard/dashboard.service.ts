import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly apiUrl = 'http://localhost:8080/api/dashboard'; // Adjust path as needed

  constructor(private http: HttpClient) {}

  getStats(): Observable<{
    totalSubjects: number;
    totalTasks: number;
    completedTasks: number;
  }> {
    return this.http.get<{
      totalSubjects: number;
      totalTasks: number;
      completedTasks: number;
    }>(`${this.apiUrl}/stats`);
  }
}
