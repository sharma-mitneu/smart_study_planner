import { Routes } from '@angular/router';
import { ListSubjectsComponent } from './list-subjects.component';
import { CreateSubjectComponent } from './create-subject.component';
import { EditSubjectComponent } from './edit-subject.component';
import { AuthGuard } from '../auth/auth.guard';
import { AvailableSubjectsComponent } from './available-subjects.component';

export const SUBJECTS_ROUTES: Routes = [
  {
    path: '',
    component: ListSubjectsComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'create',
    component: CreateSubjectComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'edit/:id',
    component: EditSubjectComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'available',
    component: AvailableSubjectsComponent,
    canActivate: [AuthGuard],
  },
];
