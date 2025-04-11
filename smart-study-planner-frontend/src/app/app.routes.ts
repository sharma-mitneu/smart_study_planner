import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
  path: 'dashboard',
  loadChildren: () =>
    import('./dashboard/dashboard.routes').then((m) => m.DASHBOARD_ROUTES),
},
{
  path: 'subjects',
  loadChildren: () =>
    import('./subjects/subjects.routes').then((m) => m.SUBJECTS_ROUTES),
},
{
  path: 'tasks',
  loadChildren: () =>
    import('./tasks/tasks.routes').then((m) => m.TASKS_ROUTES),
},
{
  path: 'progress',
  loadChildren: () =>
    import('./progress/progress.routes').then((m) => m.PROGRESS_ROUTES),
},
];
