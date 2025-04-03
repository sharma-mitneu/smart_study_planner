import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubjectDTO, SubjectEnrollmentDTO } from '../models/subject.model';
import { environment } from '../../../environments/environment';

const API_URL = `${environment.apiUrl}/api/subjects`;

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

  constructor(private http: HttpClient) { }

  getAllSubjects(): Observable<SubjectDTO[]> {
    return this.http.get<SubjectDTO[]>(API_URL);
  }

  getSubjectById(id: number): Observable<SubjectDTO> {
    return this.http.get<SubjectDTO>(`${API_URL}/${id}`);
  }

  createSubject(subject: SubjectDTO): Observable<SubjectDTO> {
    return this.http.post<SubjectDTO>(`${API_URL}/admin/create-subject`, subject);
  }

  updateSubject(id: number, subject: SubjectDTO): Observable<SubjectDTO> {
    return this.http.put<SubjectDTO>(`${API_URL}/admin/update-subject/${id}`, subject);
  }

  deleteSubject(id: number): Observable<any> {
    return this.http.delete(`${API_URL}/admin/delete-subject/${id}`);
  }

  // Student-specific endpoints
  enrollInSubject(id: number): Observable<SubjectEnrollmentDTO> {
    return this.http.post<SubjectEnrollmentDTO>(`${API_URL}/student/${id}/enroll`, {});
  }

  unenrollFromSubject(id: number): Observable<any> {
    return this.http.delete(`${API_URL}/student/${id}/unenroll`);
  }

  getEnrolledSubjects(): Observable<SubjectDTO[]> {
    return this.http.get<SubjectDTO[]>(`${API_URL}/student/enrolled`);
  }
}