import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaskDto } from '../models/task.model';
import { environment } from '../../../environments/environment';

const API_URL = `${environment.apiUrl}/api/scheduler`;

@Injectable({
  providedIn: 'root'
})
export class SchedulerService {

  constructor(private http: HttpClient) { }

  generateDailySchedule(date?: string, strategy?: string, maxHours?: number): Observable<TaskDto[]> {
    let url = `${API_URL}/daily`;
    const params: string[] = [];
    
    if (date) {
      params.push(`date=${date}`);
    }
    
    if (strategy) {
      params.push(`strategy=${strategy}`);
    }
    
    if (maxHours !== undefined) {
      params.push(`maxHours=${maxHours}`);
    }
    
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    
    return this.http.get<TaskDto[]>(url);
  }

  generateWeeklySchedule(startDate?: string, strategy?: string, maxHoursPerDay?: number): Observable<TaskDto[][]> {
    let url = `${API_URL}/weekly`;
    const params: string[] = [];
    
    if (startDate) {
      params.push(`startDate=${startDate}`);
    }
    
    if (strategy) {
      params.push(`strategy=${strategy}`);
    }
    
    if (maxHoursPerDay !== undefined) {
      params.push(`maxHoursPerDay=${maxHoursPerDay}`);
    }
    
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    
    return this.http.get<TaskDto[][]>(url);
  }

  createRecurringTaskInstances(taskId: number): Observable<TaskDto[]> {
    return this.http.post<TaskDto[]>(`${API_URL}/recurring-tasks/${taskId}`, {});
  }

  suggestPriorityTasks(limit?: number): Observable<TaskDto[]> {
    let url = `${API_URL}/suggest-priority`;
    
    if (limit) {
      url += `?limit=${limit}`;
    }
    
    return this.http.get<TaskDto[]>(url);
  }

  balanceStudyLoad(tasksPerSubject?: number): Observable<TaskDto[]> {
    let url = `${API_URL}/balance-load`;
    
    if (tasksPerSubject) {
      url += `?tasksPerSubject=${tasksPerSubject}`;
    }
    
    return this.http.get<TaskDto[]>(url);
  }
}