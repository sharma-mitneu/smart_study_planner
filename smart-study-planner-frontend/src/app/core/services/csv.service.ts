import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubjectDTO } from '../models/subject.model';
import { TaskDto } from '../models/task.model';
import { ProgressDto } from '../models/progress.model';
import { environment } from '../../../environments/environment';

const API_URL = `${environment.apiUrl}/api/csv`;

@Injectable({
  providedIn: 'root'
})
export class CsvService {

  constructor(private http: HttpClient) { }

  // Export endpoints
  exportSubjects(): Observable<Blob> {
    return this.http.get(`${API_URL}/export/subjects`, {
      responseType: 'blob'
    });
  }

  exportTasks(): Observable<Blob> {
    return this.http.get(`${API_URL}/export/tasks`, {
      responseType: 'blob'
    });
  }

  exportProgress(): Observable<Blob> {
    return this.http.get(`${API_URL}/export/progress`, {
      responseType: 'blob'
    });
  }

  exportStudySummary(): Observable<Blob> {
    return this.http.get(`${API_URL}/export/study-summary`, {
      responseType: 'blob'
    });
  }

  // Import endpoints
  importSubjects(file: File): Observable<SubjectDTO[]> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<SubjectDTO[]>(`${API_URL}/import/subjects`, formData);
  }

  importTasks(file: File): Observable<TaskDto[]> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<TaskDto[]>(`${API_URL}/import/tasks`, formData);
  }

  importProgress(file: File): Observable<ProgressDto[]> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<ProgressDto[]>(`${API_URL}/import/progress`, formData);
  }

  // Helper method to trigger download of exported files
  downloadFile(blob: Blob, fileName: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  }
}