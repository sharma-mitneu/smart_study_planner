import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProgressService } from './progress.service';
import { NgxPaginationModule } from 'ngx-pagination';

@Component({
  standalone: true,
  selector: 'app-view-progress',
  templateUrl: './view-progress.component.html',
  imports: [CommonModule, NgxPaginationModule],
})
export class ViewProgressComponent implements OnInit {
  entries: any[] = [];
  page = 1;

  constructor(private progressService: ProgressService) {}

  ngOnInit(): void {
    this.progressService.getAll().subscribe((data) => {
      this.entries = data;
    });
  }
}
