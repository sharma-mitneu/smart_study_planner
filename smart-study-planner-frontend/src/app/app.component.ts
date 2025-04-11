import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar navbar-expand navbar-dark bg-dark px-3">
      <a class="navbar-brand" routerLink="/dashboard">Smart Study</a>
      <ul class="navbar-nav me-auto">
        <li class="nav-item"><a class="nav-link" routerLink="/dashboard">Dashboard</a></li>
        <li class="nav-item"><a class="nav-link" routerLink="/subjects">Subjects</a></li>
        <li class="nav-item"><a class="nav-link" routerLink="/tasks">Tasks</a></li>
        <li class="nav-item"><a class="nav-link" routerLink="/progress">Progress</a></li>
      </ul>
      <ul class="navbar-nav ms-auto">
        <li class="nav-item" *ngIf="!auth.isLoggedIn()">
          <a class="nav-link" routerLink="/login">Login</a>
        </li>
        <li class="nav-item" *ngIf="!auth.isLoggedIn()">
          <a class="nav-link" routerLink="/register">Register</a>
        </li>
        <li class="nav-item" *ngIf="auth.isLoggedIn()">
          <button class="btn btn-outline-light btn-sm" (click)="logout()">Logout</button>
        </li>
      </ul>
    </nav>

    <router-outlet></router-outlet>
  `,
})
export class AppComponent {
  constructor(public auth: AuthService, private router: Router) {}

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
