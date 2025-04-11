import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TasksService } from './tasks.service';
import { RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';

@Component({
  standalone: true,
  selector: 'app-list-tasks',
  templateUrl: './list-tasks.component.html',
  imports: [CommonModule, RouterModule, NgxPaginationModule],
})
export class ListTasksComponent implements OnInit {
  tasks: any[] = [];
  page = 1;

  constructor(private tasksService: TasksService) {}

  ngOnInit(): void {
    this.tasksService.getAll().subscribe((data) => {
      this.tasks = data;
    });
  }
}
