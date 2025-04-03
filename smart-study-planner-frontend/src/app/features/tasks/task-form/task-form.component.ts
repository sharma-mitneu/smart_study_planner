// src/app/features/tasks/task-form/task-form.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { finalize } from 'rxjs/operators';

// Angular Material imports
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { TaskService } from '../../../core/services/task.service';
import { SubjectService } from '../../../core/services/subject.service';
import { Task, RecurrenceFrequency } from '../../../core/models/task.model';
import { Subject, Priority } from '../../../core/models/subject.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.scss']
})
export class TaskFormComponent implements OnInit {
  taskForm!: FormGroup;
  subjects: Subject[] = [];
  priorities = Object.values(Priority);
  recurrenceFrequencies = Object.values(RecurrenceFrequency);
  isEditMode = false;
  taskId: number | null = null;
  loading = false;
  submitting = false;
  
  constructor(
    private formBuilder: FormBuilder,
    private taskService: TaskService,
    private subjectService: SubjectService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.initForm();
    this.loadSubjects();
    
    // Check if we're in edit mode
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        if (id) {
          this.isEditMode = true;
          this.taskId = +id;
          return this.taskService.getTaskById(+id);
        }
        return of(null);
      })
    ).subscribe(task => {
      if (task) {
        this.populateForm(task);
      }
    });
  }
  
  private initForm(): void {
    this.taskForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      dueDate: [new Date(), Validators.required],
      dueTime: ['12:00', Validators.required],
      priority: [Priority.MEDIUM, Validators.required],
      subjectId: [null, Validators.required],
      recurring: [false],
      recurrenceFrequency: [{ value: RecurrenceFrequency.WEEKLY, disabled: true }],
      recurrenceEndDate: [{ value: null, disabled: true }]
    });
    
    // Listen for changes to recurring checkbox to enable/disable recurrence fields
    this.taskForm.get('recurring')?.valueChanges.subscribe(recurring => {
      if (recurring) {
        this.taskForm.get('recurrenceFrequency')?.enable();
        this.taskForm.get('recurrenceEndDate')?.enable();
        this.taskForm.get('recurrenceEndDate')?.setValidators(Validators.required);
      } else {
        this.taskForm.get('recurrenceFrequency')?.disable();
        this.taskForm.get('recurrenceEndDate')?.disable();
        this.taskForm.get('recurrenceEndDate')?.clearValidators();
      }
      this.taskForm.get('recurrenceEndDate')?.updateValueAndValidity();
    });
  }
  
  private loadSubjects(): void {
    this.loading = true;
    this.subjectService.getAllSubjects()
      .pipe(finalize(() => this.loading = false))
      .subscribe(subjects => {
        this.subjects = subjects;
        
        // If there's only one subject, select it automatically
        if (subjects.length === 1) {
          this.taskForm.patchValue({
            subjectId: subjects[0].id
          });
        }
      });
  }
  
  private populateForm(task: Task): void {
    // Split date and time from dueDate
    const dueDate = new Date(task.dueDate);
    const hours = dueDate.getHours().toString().padStart(2, '0');
    const minutes = dueDate.getMinutes().toString().padStart(2, '0');
    const dueTime = `${hours}:${minutes}`;
    
    // Split date from recurrenceEndDate if it exists
    let recurrenceEndDate = null;
    if (task.recurrenceEndDate) {
      recurrenceEndDate = new Date(task.recurrenceEndDate);
    }
    
    this.taskForm.patchValue({
      title: task.title,
      description: task.description,
      dueDate: dueDate,
      dueTime: dueTime,
      priority: task.priority,
      subjectId: task.subjectId,
      recurring: task.recurring,
      recurrenceFrequency: task.recurrenceFrequency,
      recurrenceEndDate: recurrenceEndDate
    });
    
    // Ensure recurrence fields are enabled if task is recurring
    if (task.recurring) {
      this.taskForm.get('recurrenceFrequency')?.enable();
      this.taskForm.get('recurrenceEndDate')?.enable();
    }
  }
  
  onSubmit(): void {
    if (this.taskForm.invalid) {
      return;
    }
    
    this.submitting = true;
    
    // Combine date and time for dueDate
    const formValues = this.taskForm.value;
    const dueDate = new Date(formValues.dueDate);
    const [hours, minutes] = formValues.dueTime.split(':');
    dueDate.setHours(+hours, +minutes);
    
    // Prepare task object
    const task: Task = {
      title: formValues.title,
      description: formValues.description,
      dueDate: dueDate.toISOString(),
      completed: false,
      priority: formValues.priority,
      subjectId: formValues.subjectId,
      recurring: formValues.recurring,
      recurrenceFrequency: formValues.recurring ? formValues.recurrenceFrequency : undefined,
      recurrenceEndDate: formValues.recurring ? formValues.recurrenceEndDate?.toISOString() : undefined
    };
    
    let saveObservable: Observable<Task>;
    
    if (this.isEditMode && this.taskId) {
      saveObservable = this.taskService.updateTask(this.taskId, task);
    } else {
      saveObservable = this.taskService.createTask(task);
    }
    
    saveObservable
      .pipe(finalize(() => this.submitting = false))
      .subscribe({
        next: () => {
          const message = this.isEditMode ? 'Task updated successfully' : 'Task created successfully';
          this.snackBar.open(message, 'Close', { duration: 3000 });
          this.router.navigate(['/tasks']);
        },
        error: error => {
          let errorMessage = 'Failed to save task';
          if (error.error && error.error.message) {
            errorMessage = error.error.message;
          }
          this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
        }
      });
  }
}