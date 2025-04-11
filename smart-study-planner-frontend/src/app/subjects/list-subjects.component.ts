import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubjectsService } from './subjects.service';
import { RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';

@Component({
  standalone: true,
  selector: 'app-list-subjects',
  templateUrl: './list-subjects.component.html',
  imports: [CommonModule, RouterModule, NgxPaginationModule],
})
export class ListSubjectsComponent implements OnInit {
  subjects: any[] = [];
  page = 1;

  constructor(private subjectsService: SubjectsService) {}

  ngOnInit(): void {
    this.subjectsService.getAll().subscribe((data) => {
      this.subjects = data;
    });
  }
}
