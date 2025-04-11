import { Routes } from '@angular/router';
import { ListTasksComponent } from './list-tasks.component';
import { CreateTaskComponent } from './create-task.component';
import { EditTaskComponent } from './edit-task.component';
import { AuthGuard } from '../auth/auth.guard';

export const TASKS_ROUTES: Routes = [
  {
    path: '',
    component: ListTasksComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'create',
    component: CreateTaskComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'edit/:id',
    component: EditTaskComponent,
    canActivate: [AuthGuard],
  },
];
