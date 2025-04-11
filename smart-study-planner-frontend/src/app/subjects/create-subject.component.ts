import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SubjectsService } from './subjects.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-create-subject',
  templateUrl: './create-subject.component.html',
  imports: [CommonModule, ReactiveFormsModule],
})
export class CreateSubjectComponent implements OnInit {
  form!: FormGroup;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private service: SubjectsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: [''],
    });
  }

  create() {
    if (this.form.invalid) return;
    this.service.create(this.form.value).subscribe({
      next: () => this.router.navigate(['/subjects']),
      error: (err) => (this.error = err.error.message || 'Error creating subject'),
    });
  }
}
