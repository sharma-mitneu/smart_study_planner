// src/app/features/analytics/productivity-chart/productivity-chart.component.ts
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-productivity-chart',
  templateUrl: './productivity-chart.component.html',
  styleUrls: ['./productivity-chart.component.scss']
})
export class ProductivityChartComponent implements OnChanges {
  @Input() chartData: any[] = [];
  @Input() chartType: 'pie' | 'bar' = 'pie';
  @Input() chartLabel: string = '';
  @Input() gradient: boolean = false;
  
  // Chart options
  view: [number, number] = [500, 300];
  showXAxis = true;
  showYAxis = true;
  showLegend = true;
  showXAxisLabel = true;
  showYAxisLabel = true;
  xAxisLabel = 'Subject';
  yAxisLabel = this.chartLabel;
  legendPosition: 'right' | 'below' = 'right';
  legendTitle = '';
  
  colorScheme = {
    domain: ['#5AA454', '#E44D25', '#CFC0BB', '#7aa3e5', '#a8385d', '#aae3f5']
  };
  
  // Data for the chart
  chartDataFormatted: any[] = [];
  
  constructor() {}
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chartData'] && this.chartData) {
      this.chartDataFormatted = this.chartData.map(item => ({
        name: item.name,
        value: item.value
      }));
      
      // Update axis label
      this.yAxisLabel = this.chartLabel;
    }
  }
  
  onResize(event: any): void {
    const containerWidth = event.target.innerWidth;
    
    if (containerWidth < 400) {
      this.view = [containerWidth, 300];
      this.legendPosition = 'below';
    } else {
      this.view = [500, 300];
      this.legendPosition = 'right';
    }
  }
}