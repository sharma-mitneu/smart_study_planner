import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProgressService } from './progress.service';
import { SubjectsService } from '../subjects/subjects.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-log-progress',
  templateUrl: './log-progress.component.html',
  imports: [CommonModule, ReactiveFormsModule],
})
export class LogProgressComponent implements OnInit {
  form!: FormGroup;
  subjects: any[] = [];
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private progressService: ProgressService,
    private subjectsService: SubjectsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      subjectId: ['', Validators.required],
      durationMinutes: [0, [Validators.required, Validators.min(1)]],
      notes: [''],
    });

    this.subjectsService.getAll().subscribe((data) => {
      this.subjects = data;
    });
  }

  submit() {
    if (this.form.invalid) return;

    this.progressService.log(this.form.value).subscribe({
      next: () => this.router.navigate(['/progress']),
      error: (err) => (this.error = err.error.message || 'Failed to log progress'),
    });
  }
}
