// src/app/features/tasks/task-list/task-list.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

// Angular Material imports
import { MatTableModule } from '@angular/material/table';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule } from '@angular/material/dialog';

import { TaskService } from '../../../core/services/task.service';
import { SubjectService } from '../../../core/services/subject.service';
import { SchedulerService } from '../../../core/services/scheduler.service';
import { Task } from '../../../core/models/task.model';
import { Subject } from '../../../core/models/subject.model';

import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { PriorityLabelPipe } from '../../../shared/pipes/priority-label.pipe';
import { HighlightOverdueDirective } from '../../../shared/directives/highlight-overdue.directive';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    PriorityLabelPipe,
    HighlightOverdueDirective
  ],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  subjects: Subject[] = [];
  dataSource = new MatTableDataSource<Task>([]);
  displayedColumns: string[] = [
    'title', 'subjectName', 'dueDate', 'priority', 'completed', 'actions'
  ];
  loading = false;
  selectedFilter = 'all';
  selectedSort = 'due_date_asc';
  selectedSubject: number | null = null;
  
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  
  constructor(
    private taskService: TaskService,
    private subjectService: SubjectService,
    private schedulerService: SchedulerService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.loadSubjects();
    this.loadTasks();
  }
  
  loadSubjects(): void {
    this.subjectService.getAllSubjects().subscribe(subjects => {
      this.subjects = subjects;
    });
  }
  
  loadTasks(): void {
    this.loading = true;
    this.taskService.getAllTasks(this.selectedSort).subscribe(tasks => {
      this.tasks = tasks;
      this.applyFilters();
      this.loading = false;
    });
  }
  
  applyFilters(): void {
    let filteredTasks = [...this.tasks];
    
    // Filter by status
    if (this.selectedFilter === 'completed') {
      filteredTasks = filteredTasks.filter(task => task.completed);
    } else if (this.selectedFilter === 'pending') {
      filteredTasks = filteredTasks.filter(task => !task.completed);
    } else if (this.selectedFilter === 'overdue') {
      filteredTasks = filteredTasks.filter(task => task.overdue && !task.completed);
    }
    
    // Filter by subject
    if (this.selectedSubject) {
      filteredTasks = filteredTasks.filter(task => task.subjectId === this.selectedSubject);
    }
    
    this.dataSource = new MatTableDataSource(filteredTasks);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }
  
  onFilterChange(): void {
    this.applyFilters();
  }
  
  onSortChange(): void {
    this.loadTasks();
  }
  
  applySearchFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  
  markTaskAsCompleted(taskId: number): void {
    this.taskService.markTaskAsCompleted(taskId).subscribe(() => {
      // Update task in the list
      const index = this.tasks.findIndex(task => task.id === taskId);
      if (index >= 0) {
        this.tasks[index].completed = true;
        this.applyFilters();
      }
      
      this.snackBar.open('Task marked as completed', 'Close', { duration: 3000 });
    });
  }
  
  deleteTask(taskId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Task',
        message: 'Are you sure you want to delete this task?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.taskService.deleteTask(taskId).subscribe(() => {
          // Remove task from the list
          this.tasks = this.tasks.filter(task => task.id !== taskId);
          this.applyFilters();
          
          this.snackBar.open('Task deleted successfully', 'Close', {
            duration: 3000
          });
        });
      }
    });
  }
  
  createRecurringInstances(taskId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Create Recurring Tasks',
        message: 'This will create instances for all future occurrences until the end date. Continue?',
        confirmText: 'Create',
        cancelText: 'Cancel'
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.schedulerService.createRecurringTaskInstances(taskId).subscribe(newTasks => {
          // Add new tasks to the list
          this.tasks = [...this.tasks, ...newTasks];
          this.applyFilters();
          
          this.snackBar.open(`Created ${newTasks.length} recurring task instances`, 'Close', {
            duration: 3000
          });
        });
      }
    });
  }
}