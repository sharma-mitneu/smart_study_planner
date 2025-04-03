import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudyTimeDto, TaskCompletionDto } from '../models/progress.model';
import { environment } from '../../../environments/environment';

const API_URL = `${environment.apiUrl}/api/analytics`;

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  constructor(private http: HttpClient) { }

  getStudyTimeAnalytics(startDate?: string, endDate?: string): Observable<StudyTimeDto> {
    let url = `${API_URL}/study-time`;
    const params: string[] = [];
    
    if (startDate) {
      params.push(`startDate=${startDate}`);
    }
    
    if (endDate) {
      params.push(`endDate=${endDate}`);
    }
    
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    
    return this.http.get<StudyTimeDto>(url);
  }

  getTaskCompletionAnalytics(): Observable<TaskCompletionDto> {
    return this.http.get<TaskCompletionDto>(`${API_URL}/task-completion`);
  }

  getCurrentStudyStreak(): Observable<number> {
    return this.http.get<number>(`${API_URL}/study-streak`);
  }

  getProductivityScore(): Observable<number> {
    return this.http.get<number>(`${API_URL}/productivity-score`);
  }
}