import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, RegistrationRequest, AuthResponse } from '../models/auth.model';
import { TokenStorageService } from './token.storage';
import { environment } from '../../../environments/environment';

const AUTH_API = `${environment.apiUrl}/api/auth`;
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  constructor(
    private http: HttpClient,
    private tokenStorage: TokenStorageService
  ) { }
  
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${AUTH_API}/login`, 
      credentials, 
      httpOptions
    ).pipe(
      tap(response => {
        this.tokenStorage.saveToken(response.token);
        this.tokenStorage.saveUser(response);
      })
    );
  }
  
  registerStudent(data: RegistrationRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${AUTH_API}/student/register`, 
      data, 
      httpOptions
    );
  }
  
  registerAdmin(data: RegistrationRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${AUTH_API}/admin/register`, 
      data, 
      httpOptions
    );
  }
  
  registerInitialAdmin(data: RegistrationRequest, setupToken: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${AUTH_API}/admin/setup?setupToken=${setupToken}`, 
      data, 
      httpOptions
    );
  }
  
  logout(): void {
    this.tokenStorage.signOut();
    // Force reload to clear any in-memory state
    window.location.reload();
  }
  
  isLoggedIn(): boolean {
    return !!this.tokenStorage.getToken();
  }
  
  getCurrentUser(): any {
    return this.tokenStorage.getUser();
  }
  
  isAdmin(): boolean {
    return this.tokenStorage.isAdmin();
  }
  
  isStudent(): boolean {
    return this.tokenStorage.isStudent();
  }
}