// src/app/features/analytics/analytics.component.ts
import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { AnalyticsService } from '../../core/services/analytics.service';
import { SubjectService } from '../../core/services/subject.service';
import { Subject } from '../../core/models/subject.model';
import { StudyTime, TaskCompletion, SubjectStudyTime, SubjectCompletion } from '../../core/models/progress.model';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit {
  // Date range for analytics
  startDate = new FormControl(new Date(new Date().setDate(new Date().getDate() - 30)));
  endDate = new FormControl(new Date());
  
  // Data models
  studyTime: StudyTime | null = null;
  taskCompletion: TaskCompletion | null = null;
  studyStreak = 0;
  productivityScore = 0;
  subjects: Subject[] = [];
  
  // Charts data
  dailyStudyData: any[] = [];
  subjectTimeData: any[] = [];
  subjectCompletionData: any[] = [];
  
  // Loading states
  loading = false;
  
  constructor(
    private analyticsService: AnalyticsService,
    private subjectService: SubjectService
  ) {}
  
  ngOnInit(): void {
    this.loadSubjects();
    this.loadAnalytics();
  }
  
  loadSubjects(): void {
    this.subjectService.getAllSubjects().subscribe(subjects => {
      this.subjects = subjects;
    });
  }
  
  loadAnalytics(): void {
    this.loading = true;
    
    // Load all analytics data using forkJoin
    forkJoin({
      studyTime: this.analyticsService.getStudyTimeAnalytics(
        this.startDate.value!,
        this.endDate.value!
      ),
      taskCompletion: this.analyticsService.getTaskCompletionAnalytics(),
      studyStreak: this.analyticsService.getCurrentStudyStreak(),
      productivityScore: this.analyticsService.getProductivityScore()
    })
    .pipe(finalize(() => this.loading = false))
    .subscribe(results => {
      this.studyTime = results.studyTime;
      this.taskCompletion = results.taskCompletion;
      this.studyStreak = results.studyStreak;
      this.productivityScore = results.productivityScore;
      
      // Prepare chart data
      this.prepareDailyStudyChart();
      this.prepareSubjectTimeChart();
      this.prepareSubjectCompletionChart();
    });
  }
  
  onDateRangeChange(): void {
    this.loadAnalytics();
  }
  
  private prepareDailyStudyChart(): void {
    if (!this.studyTime || !this.studyTime.dailyBreakdown) {
      this.dailyStudyData = [];
      return;
    }
    
    this.dailyStudyData = this.studyTime.dailyBreakdown.map(day => {
      return {
        name: new Date(day.date).toLocaleDateString(),
        value: day.minutes / 60 // Convert to hours
      };
    });
  }
  
  private prepareSubjectTimeChart(): void {
    if (!this.studyTime || !this.studyTime.subjectBreakdown) {
      this.subjectTimeData = [];
      return;
    }
    
    this.subjectTimeData = this.studyTime.subjectBreakdown.map(subject => {
      return {
        name: subject.subjectName,
        value: subject.minutes / 60 // Convert to hours
      };
    });
  }
  
  private prepareSubjectCompletionChart(): void {
    if (!this.taskCompletion || !this.taskCompletion.subjectBreakdown) {
      this.subjectCompletionData = [];
      return;
    }
    
    this.subjectCompletionData = this.taskCompletion.subjectBreakdown.map(subject => {
      return {
        name: subject.subjectName,
        value: subject.completionRate
      };
    });
  }
  
  getStudyTimePercentage(subject: SubjectStudyTime): string {
    return subject.percentage.toFixed(1) + '%';
  }
  
  getCompletionRatePercentage(subject: SubjectCompletion): string {
    return subject.completionRate.toFixed(1) + '%';
  }
  
  getTotalStudyHours(): number {
    if (!this.studyTime) {
      return 0;
    }
    return Math.round((this.studyTime.totalMinutes / 60) * 10) / 10;
  }
}