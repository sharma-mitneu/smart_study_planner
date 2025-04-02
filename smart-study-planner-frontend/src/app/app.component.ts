// src/app/app.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { MatSidenavModule } from '@angular/material/sidenav';

import { HeaderComponent } from './shared/components/header/header.component';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';
import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule, 
    RouterOutlet,
    MatSidenavModule,
    HeaderComponent,
    SidebarComponent
  ],
  template: `
    <div class="app-container">
      <ng-container *ngIf="isLoggedIn; else loginTemplate">
        <app-header></app-header>
        <mat-sidenav-container class="sidenav-container">
          <mat-sidenav mode="side" opened class="sidenav">
            <app-sidebar [currentPage]="currentPage"></app-sidebar>
          </mat-sidenav>
          <mat-sidenav-content class="sidenav-content">
            <div class="content-wrapper">
              <router-outlet></router-outlet>
            </div>
          </mat-sidenav-content>
        </mat-sidenav-container>
      </ng-container>
      
      <ng-template #loginTemplate>
        <div class="auth-container">
          <router-outlet></router-outlet>
        </div>
      </ng-template>
    </div>
  `,
  styles: [`
    .app-container {
      height: 100vh;
      display: flex;
      flex-direction: column;
    }
    
    .sidenav-container {
      flex: 1;
    }
    
    .sidenav {
      width: 250px;
      background-color: #f5f5f5;
      border-right: 1px solid #e0e0e0;
    }
    
    .sidenav-content {
      background-color: #fafafa;
    }
    
    .content-wrapper {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }
    
    .auth-container {
      height: 100vh;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #f5f5f5;
    }
  `]
})
export class AppComponent implements OnInit {
  title = 'Smart Study Planner';
  isLoggedIn = false;
  currentPage = '';
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    // Check authentication status
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
    });
    
    // Track current page for UI highlighting
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(event => {
      if (event instanceof NavigationEnd) {
        const path = event.urlAfterRedirects.split('/')[1] || 'dashboard';
        this.currentPage = path;
      }
    });
  }
}