// src/app/features/analytics/study-time-chart/study-time-chart.component.ts
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-study-time-chart',
  templateUrl: './study-time-chart.component.html',
  styleUrls: ['./study-time-chart.component.scss']
})
export class StudyTimeChartComponent implements OnChanges {
  @Input() chartData: any[] = [];
  @Input() xAxisLabel: string = '';
  @Input() yAxisLabel: string = '';
  @Input() gradient: boolean = false;
  
  // Chart options
  view: [number, number] = [700, 300];
  showXAxis = true;
  showYAxis = true;
  showLegend = false;
  showXAxisLabel = true;
  showYAxisLabel = true;
  timeline = false;
  colorScheme = {
    domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA']
  };
  
  // Single series data
  singleSeries: any[] = [];
  
  constructor() {}
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chartData'] && this.chartData) {
      this.singleSeries = [{
        name: 'Study Hours',
        series: this.chartData.map(item => ({
          name: item.name,
          value: item.value
        }))
      }];
    }
  }
  
  onResize(event: any): void {
    const containerWidth = event.target.innerWidth;
    this.view = containerWidth < 700 ? [containerWidth, 300] : [700, 300];
  }
}