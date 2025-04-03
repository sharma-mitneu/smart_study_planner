// src/app/features/subjects/subject-form/subject-form.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, of } from 'rxjs';
import { finalize, switchMap } from 'rxjs/operators';
import { SubjectService } from '../../../core/services/subject.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Subject, Priority } from '../../../core/models/subject.model';

@Component({
  selector: 'app-subject-form',
  templateUrl: './subject-form.component.html',
  styleUrls: ['./subject-form.component.scss']
})
export class SubjectFormComponent implements OnInit {
  subjectForm!: FormGroup;
  priorities = Object.values(Priority);
  isEditMode = false;
  subjectId: number | null = null;
  loading = false;
  submitting = false;
  isAdmin = false;
  
  constructor(
    private formBuilder: FormBuilder,
    private subjectService: SubjectService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.initForm();
    
    this.authService.currentUser$.subscribe(user => {
      this.isAdmin = user?.role === 'ADMIN';
      
      if (!this.isAdmin) {
        this.snackBar.open('Only administrators can create or edit subjects', 'Close', { duration: 5000 });
        this.router.navigate(['/subjects']);
        return;
      }
      
      // Check if we're in edit mode
      this.route.paramMap.pipe(
        switchMap(params => {
          const id = params.get('id');
          if (id) {
            this.isEditMode = true;
            this.subjectId = +id;
            return this.subjectService.getSubjectById(+id);
          }
          return of(null);
        })
      ).subscribe(subject => {
        if (subject) {
          this.populateForm(subject);
        }
      });
    });
  }
  
  private initForm(): void {
    this.subjectForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      priority: [Priority.MEDIUM, Validators.required]
    });
  }
  
  private populateForm(subject: Subject): void {
    this.subjectForm.patchValue({
      name: subject.name,
      description: subject.description,
      priority: subject.priority
    });
  }
  
  onSubmit(): void {
    if (this.subjectForm.invalid) {
      return;
    }
    
    this.submitting = true;
    
    // Prepare subject object
    const subject: Subject = {
      name: this.subjectForm.value.name,
      description: this.subjectForm.value.description,
      priority: this.subjectForm.value.priority
    };
    
    let saveObservable: Observable<Subject>;
    
    if (this.isEditMode && this.subjectId) {
      saveObservable = this.subjectService.updateSubject(this.subjectId, subject);
    } else {
      saveObservable = this.subjectService.createSubject(subject);
    }
    
    saveObservable
      .pipe(finalize(() => this.submitting = false))
      .subscribe({
        next: () => {
          const message = this.isEditMode ? 'Subject updated successfully' : 'Subject created successfully';
          this.snackBar.open(message, 'Close', { duration: 3000 });
          this.router.navigate(['/subjects']);
        },
        error: error => {
          let errorMessage = 'Failed to save subject';
          if (error.error && error.error.message) {
            errorMessage = error.error.message;
          }
          this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
        }
      });
  }
}