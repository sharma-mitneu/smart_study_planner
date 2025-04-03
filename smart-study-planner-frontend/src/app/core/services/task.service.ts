import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaskDto } from '../models/task.model';
import { environment } from '../../../environments/environment';

const API_URL = `${environment.apiUrl}/api/tasks`;

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private http: HttpClient) { }

  getAllTasks(sortBy?: string): Observable<TaskDto[]> {
    let url = API_URL;
    if (sortBy) {
      url += `?sortBy=${sortBy}`;
    }
    return this.http.get<TaskDto[]>(url);
  }

  getTasksBySubject(subjectId: number, sortBy?: string): Observable<TaskDto[]> {
    let url = `${API_URL}/by-subject/${subjectId}`;
    if (sortBy) {
      url += `?sortBy=${sortBy}`;
    }
    return this.http.get<TaskDto[]>(url);
  }

  getUpcomingTasks(): Observable<TaskDto[]> {
    return this.http.get<TaskDto[]>(`${API_URL}/upcoming`);
  }

  getOverdueTasks(): Observable<TaskDto[]> {
    return this.http.get<TaskDto[]>(`${API_URL}/overdue`);
  }

  getTaskById(id: number): Observable<TaskDto> {
    return this.http.get<TaskDto>(`${API_URL}/${id}`);
  }

  createTask(task: TaskDto): Observable<TaskDto> {
    return this.http.post<TaskDto>(API_URL, task);
  }

  updateTask(id: number, task: TaskDto): Observable<TaskDto> {
    return this.http.put<TaskDto>(`${API_URL}/${id}`, task);
  }

  deleteTask(id: number): Observable<any> {
    return this.http.delete(`${API_URL}/${id}`);
  }

  markTaskAsCompleted(id: number): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${API_URL}/${id}/complete`, {});
  }
}