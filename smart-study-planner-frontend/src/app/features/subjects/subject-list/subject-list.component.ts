// src/app/features/subjects/subject-list/subject-list.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { SubjectService } from '../../../core/services/subject.service';
import { AuthService } from '../../../core/auth/auth.service';
import { Subject, Priority } from '../../../core/models/subject.model';

@Component({
  selector: 'app-subject-list',
  templateUrl: './subject-list.component.html',
  styleUrls: ['./subject-list.component.scss']
})
export class SubjectListComponent implements OnInit {
  subjects: Subject[] = [];
  dataSource = new MatTableDataSource<Subject>([]);
  displayedColumns: string[] = [
    'name', 'priority', 'taskCount', 'completionPercentage', 'actions'
  ];
  loading = false;
  isAdmin = false;
  
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  
  constructor(
    private subjectService: SubjectService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.isAdmin = user?.role === 'ADMIN';
      this.loadSubjects();
    });
  }
  
  loadSubjects(): void {
    this.loading = true;
    
    const loadMethod = this.isAdmin ? 
      this.subjectService.getAllSubjects() : 
      this.subjectService.getEnrolledSubjects();
    
    loadMethod.subscribe({
      next: subjects => {
        this.subjects = subjects;
        this.dataSource = new MatTableDataSource(subjects);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.loading = false;
      },
      error: error => {
        this.snackBar.open('Failed to load subjects', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }
  
  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  
  deleteSubject(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Subject',
        message: 'Are you sure you want to delete this subject? All associated tasks will also be deleted.',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.subjectService.deleteSubject(id).subscribe({
          next: () => {
            this.subjects = this.subjects.filter(subject => subject.id !== id);
            this.dataSource.data = this.subjects;
            this.snackBar.open('Subject deleted successfully', 'Close', { duration: 3000 });
          },
          error: error => {
            this.snackBar.open('Failed to delete subject', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
  
  unenrollFromSubject(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Unenroll from Subject',
        message: 'Are you sure you want to unenroll from this subject?',
        confirmText: 'Unenroll',
        cancelText: 'Cancel'
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.subjectService.unenrollFromSubject(id).subscribe({
          next: () => {
            this.subjects = this.subjects.filter(subject => subject.id !== id);
            this.dataSource.data = this.subjects;
            this.snackBar.open('Unenrolled from subject successfully', 'Close', { duration: 3000 });
          },
          error: error => {
            this.snackBar.open('Failed to unenroll from subject', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
}