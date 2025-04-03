import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { TaskDto } from '../../core/models/task.model';
import { StudyTimeDto, TaskCompletionDto } from '../../core/models/progress.model';
import { SubjectDTO } from '../../core/models/subject.model';
import { TaskService } from '../../core/services/task.service';
import { AnalyticsService } from '../../core/services/analytics.service';
import { SubjectService } from '../../core/services/subject.service';
import { AuthService } from '../../core/auth/auth.service';
import { DurationPipe } from '../../shared/pipes/duration.pipe';
import { PriorityLabelPipe } from '../../shared/pipes/priority-label.pipe';
import { HighlightOverdueDirective } from '../../shared/directives/highlight-overdue.directive';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDividerModule,
    MatChipsModule,
    DurationPipe,
    PriorityLabelPipe,
    HighlightOverdueDirective
  ]
})
export class DashboardComponent implements OnInit {
  // Data properties
  upcomingTasks: TaskDto[] = [];
  overdueTasks: TaskDto[] = [];
  studyTime: StudyTimeDto | null = null;
  taskCompletion: TaskCompletionDto | null = null;
  subjects: SubjectDTO[] = [];
  studyStreak: number = 0;
  productivityScore: number = 0;
  
  // Loading states
  loadingTasks = true;
  loadingAnalytics = true;
  loadingSubjects = true;
  
  constructor(
    private taskService: TaskService,
    private analyticsService: AnalyticsService,
    private subjectService: SubjectService,
    public authService: AuthService
  ) { }
  
  ngOnInit(): void {
    this.loadDashboardData();
  }
  
  loadDashboardData(): void {
    // Load upcoming tasks
    this.taskService.getUpcomingTasks().subscribe({
      next: (tasks) => {
        this.upcomingTasks = tasks.slice(0, 5); // Show only 5 upcoming tasks
        this.loadingTasks = false;
      },
      error: () => {
        this.loadingTasks = false;
      }
    });
    
    // Load overdue tasks
    this.taskService.getOverdueTasks().subscribe({
      next: (tasks) => {
        this.overdueTasks = tasks.slice(0, 5); // Show only 5 overdue tasks
      }
    });
    
    // Load study time analytics (last 7 days)
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);
    
    this.analyticsService.getStudyTimeAnalytics(
      sevenDaysAgo.toISOString().slice(0, 10)
    ).subscribe({
      next: (data) => {
        this.studyTime = data;
      }
    });
    
    // Load task completion analytics
    this.analyticsService.getTaskCompletionAnalytics().subscribe({
      next: (data) => {
        this.taskCompletion = data;
      }
    });
    
    // Load subjects
    if (this.authService.isAdmin()) {
      this.subjectService.getAllSubjects().subscribe({
        next: (subjects) => {
          this.subjects = subjects.slice(0, 5); // Show only 5 subjects
          this.loadingSubjects = false;
        },
        error: () => {
          this.loadingSubjects = false;
        }
      });
    } else {
      this.subjectService.getEnrolledSubjects().subscribe({
        next: (subjects) => {
          this.subjects = subjects.slice(0, 5); // Show only 5 subjects
          this.loadingSubjects = false;
        },
        error: () => {
          this.loadingSubjects = false;
        }
      });
    }
    
    // Load study streak
    this.analyticsService.getCurrentStudyStreak().subscribe({
      next: (streak) => {
        this.studyStreak = streak;
      }
    });
    
    // Load productivity score
    this.analyticsService.getProductivityScore().subscribe({
      next: (score) => {
        this.productivityScore = score;
        this.loadingAnalytics = false;
      },
      error: () => {
        this.loadingAnalytics = false;
      }
    });
  }
  
  // Helper method to get color based on completion percentage
  getCompletionColor(percentage: number): string {
    if (percentage < 30) return 'warn';
    if (percentage < 70) return 'accent';
    return 'primary';
  }
  
  // Helper method to format date
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
  
  // Helper method to get color based on task priority
  getPriorityColor(priority: string): string {
    switch(priority) {
      case 'HIGH': return 'warn';
      case 'MEDIUM': return 'accent';
      case 'LOW': return 'primary';
      default: return '';
    }
  }
}