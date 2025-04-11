import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TasksService } from './tasks.service';
import { SubjectsService } from '../subjects/subjects.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-create-task',
  templateUrl: './create-task.component.html',
  imports: [CommonModule, ReactiveFormsModule],
})
export class CreateTaskComponent implements OnInit {
  form!: FormGroup;
  subjects: any[] = [];
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private taskService: TasksService,
    private subjectService: SubjectsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      subjectId: ['', Validators.required],
      dueDate: ['', Validators.required],
      status: ['PENDING', Validators.required],
    });

    this.subjectService.getAll().subscribe((data) => {
      this.subjects = data;
    });
  }

  create() {
    if (this.form.invalid) return;
    this.taskService.create(this.form.value).subscribe({
      next: () => this.router.navigate(['/tasks']),
      error: (err) => (this.error = err.error.message || 'Error creating task'),
    });
  }
}
