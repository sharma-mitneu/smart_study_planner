// src/app/features/subjects/subject-enrollment/subject-enrollment.component.ts
import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { SubjectService } from '../../../core/services/subject.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Subject, SubjectEnrollment } from '../../../core/models/subject.model';

@Component({
  selector: 'app-subject-enrollment',
  templateUrl: './subject-enrollment.component.html',
  styleUrls: ['./subject-enrollment.component.scss']
})
export class SubjectEnrollmentComponent implements OnInit {
  availableSubjects: Subject[] = [];
  enrolledSubjects: Subject[] = [];
  loading = false;
  enrollingSubjectId: number | null = null;
  
  constructor(
    private subjectService: SubjectService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user?.role === 'ADMIN') {
        this.snackBar.open('Administrators do not need to enroll in subjects', 'Close', { duration: 5000 });
        this.router.navigate(['/subjects']);
        return;
      }
      
      this.loadSubjects();
    });
  }
  
  loadSubjects(): void {
    this.loading = true;
    
    // Load both available and enrolled subjects
    this.subjectService.getAllSubjects().subscribe(allSubjects => {
      this.subjectService.getEnrolledSubjects().subscribe(enrolledSubjects => {
        this.enrolledSubjects = enrolledSubjects;
        
        // Filter out subjects that the student is already enrolled in
        const enrolledIds = enrolledSubjects.map(subject => subject.id);
        this.availableSubjects = allSubjects.filter(subject => 
          !enrolledIds.includes(subject.id)
        );
        
        this.loading = false;
      });
    });
  }
  
  enrollInSubject(subjectId: number): void {
    this.enrollingSubjectId = subjectId;
    
    this.subjectService.enrollInSubject(subjectId)
      .pipe(finalize(() => this.enrollingSubjectId = null))
      .subscribe({
        next: (enrollment: SubjectEnrollment) => {
          // Move the subject from available to enrolled
          const enrolledSubject = this.availableSubjects.find(s => s.id === subjectId);
          if (enrolledSubject) {
            this.availableSubjects = this.availableSubjects.filter(s => s.id !== subjectId);
            this.enrolledSubjects.push(enrolledSubject);
          }
          
          this.snackBar.open(`Enrolled in ${enrollment.subjectName} successfully`, 'Close', {
            duration: 3000
          });
        },
        error: error => {
          let errorMessage = 'Failed to enroll in subject';
          if (error.error && error.error.message) {
            errorMessage = error.error.message;
          }
          this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
        }
      });
  }
}