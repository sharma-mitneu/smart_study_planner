import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartConfiguration, ChartType } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { DashboardService } from './dashboard.service';

@Component({
  standalone: true,
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  imports: [CommonModule, NgChartsModule],
})
export class DashboardComponent implements OnInit {
  totalTasks = 0;
  completedTasks = 0;
  totalSubjects = 0;

  chartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['Subjects', 'Tasks'],
    datasets: [
      { data: [0, 0], label: 'Overview' },
    ],
  };

  chartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
  };

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe((data) => {
      this.totalTasks = data.totalTasks;
      this.completedTasks = data.completedTasks;
      this.totalSubjects = data.totalSubjects;

      this.chartData.datasets[0].data = [
        this.totalSubjects,
        this.totalTasks,
      ];
    });
  }
}
