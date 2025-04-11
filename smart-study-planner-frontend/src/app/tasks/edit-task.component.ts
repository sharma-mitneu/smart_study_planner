import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TasksService } from './tasks.service';
import { SubjectsService } from '../subjects/subjects.service';

@Component({
  standalone: true,
  selector: 'app-edit-task',
  templateUrl: './edit-task.component.html',
  imports: [CommonModule, ReactiveFormsModule],
})
export class EditTaskComponent implements OnInit {
  form!: FormGroup;
  id!: number;
  subjects: any[] = [];
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private taskService: TasksService,
    private subjectService: SubjectsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = +this.route.snapshot.params['id'];

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

    this.taskService.getById(this.id).subscribe((task) => {
      this.form.patchValue(task);
    });
  }

  update() {
    if (this.form.invalid) return;

    this.taskService.update(this.id, this.form.value).subscribe({
      next: () => this.router.navigate(['/tasks']),
      error: (err) => (this.error = err.error.message || 'Error updating task'),
    });
  }
}
