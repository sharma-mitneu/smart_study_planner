import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SubjectsService {
  private readonly apiUrl = 'http://localhost:8080/api/subjects';

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  create(subject: any): Observable<any> {
    return this.http.post(this.apiUrl, subject);
  }

  update(id: number, subject: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, subject);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
  enroll(subjectId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${subjectId}/enroll`, {});
  }
  
  getEnrolledSubjectIds(): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/enrolled-ids`);
  }
  
}
