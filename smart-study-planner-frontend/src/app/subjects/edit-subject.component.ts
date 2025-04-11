import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SubjectsService } from './subjects.service';

@Component({
  standalone: true,
  selector: 'app-edit-subject',
  templateUrl: './edit-subject.component.html',
  imports: [CommonModule, ReactiveFormsModule],
})
export class EditSubjectComponent implements OnInit {
  form!: FormGroup;
  id!: number;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private service: SubjectsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: [''],
    });

    this.id = +this.route.snapshot.params['id'];
    this.service.getById(this.id).subscribe((subject) => {
      this.form.patchValue(subject);
    });
  }

  update() {
    if (this.form.invalid) return;
    this.service.update(this.id, this.form.value).subscribe({
      next: () => this.router.navigate(['/subjects']),
      error: (err) => (this.error = err.error.message || 'Error updating subject'),
    });
  }
}
