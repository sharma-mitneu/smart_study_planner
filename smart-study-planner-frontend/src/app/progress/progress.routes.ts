import { Routes } from '@angular/router';
import { LogProgressComponent } from './log-progress.component';
import { ViewProgressComponent } from './view-progress.component';
import { AuthGuard } from '../auth/auth.guard';

export const PROGRESS_ROUTES: Routes = [
  {
    path: '',
    component: ViewProgressComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'log',
    component: LogProgressComponent,
    canActivate: [AuthGuard],
  },
];
