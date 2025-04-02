// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  // Auth routes
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  
  // Protected routes
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      
      // Subject routes
      {
        path: 'subjects',
        loadComponent: () => import('./features/subjects/subject-list/subject-list.component').then(m => m.SubjectListComponent)
      },
      {
        path: 'subjects/new',
        loadComponent: () => import('./features/subjects/subject-form/subject-form.component').then(m => m.SubjectFormComponent)
      },
      {
        path: 'subjects/edit/:id',
        loadComponent: () => import('./features/subjects/subject-form/subject-form.component').then(m => m.SubjectFormComponent)
      },
      {
        path: 'subjects/enroll',
        loadComponent: () => import('./features/subjects/subject-enrollment/subject-enrollment.component').then(m => m.SubjectEnrollmentComponent)
      },
      
      // Task routes
      {
        path: 'tasks',
        loadComponent: () => import('./features/tasks/task-list/task-list.component').then(m => m.TaskListComponent)
      },
      {
        path: 'tasks/new',
        loadComponent: () => import('./features/tasks/task-form/task-form.component').then(m => m.TaskFormComponent)
      },
      {
        path: 'tasks/edit/:id',
        loadComponent: () => import('./features/tasks/task-form/task-form.component').then(m => m.TaskFormComponent)
      },
      
      // Progress routes
      {
        path: 'progress',
        loadComponent: () => import('./features/progress/progress-list/progress-list.component').then(m => m.ProgressListComponent)
      },
      {
        path: 'progress/new',
        loadComponent: () => import('./features/progress/progress-form/progress-form.component').then(m => m.ProgressFormComponent)
      },
      {
        path: 'progress/edit/:id',
        loadComponent: () => import('./features/progress/progress-form/progress-form.component').then(m => m.ProgressFormComponent)
      },
      
      // Scheduler routes
      {
        path: 'scheduler',
        loadComponent: () => import('./features/scheduler/scheduler.component').then(m => m.SchedulerComponent)
      },
      
      // Analytics routes
      {
        path: 'analytics',
        loadComponent: () => import('./features/analytics/analytics.component').then(m => m.AnalyticsComponent)
      }
    ]
  },
  
  // Fallback route
  { path: '**', redirectTo: '/dashboard' }
];