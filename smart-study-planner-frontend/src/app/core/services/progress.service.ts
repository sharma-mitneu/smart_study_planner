import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProgressDto } from '../models/progress.model';
import { environment } from '../../../environments/environment';

const API_URL = `${environment.apiUrl}/api/progress`;

@Injectable({
  providedIn: 'root'
})
export class ProgressService {

  constructor(private http: HttpClient) { }

  getAllProgressEntries(): Observable<ProgressDto[]> {
    return this.http.get<ProgressDto[]>(API_URL);
  }

  getProgressByDateRange(startDate: string, endDate: string): Observable<ProgressDto[]> {
    return this.http.get<ProgressDto[]>(
      `${API_URL}/by-date-range?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getProgressByTask(taskId: number): Observable<ProgressDto[]> {
    return this.http.get<ProgressDto[]>(`${API_URL}/by-task/${taskId}`);
  }

  getProgressById(id: number): Observable<ProgressDto> {
    return this.http.get<ProgressDto>(`${API_URL}/${id}`);
  }

  createProgress(progress: ProgressDto): Observable<ProgressDto> {
    return this.http.post<ProgressDto>(API_URL, progress);
  }

  updateProgress(id: number, progress: ProgressDto): Observable<ProgressDto> {
    return this.http.put<ProgressDto>(`${API_URL}/${id}`, progress);
  }

  deleteProgress(id: number): Observable<any> {
    return this.http.delete(`${API_URL}/${id}`);
  }
}