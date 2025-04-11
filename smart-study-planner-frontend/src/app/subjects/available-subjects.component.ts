import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubjectsService } from './subjects.service';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-available-subjects',
  templateUrl: './available-subjects.component.html',
  imports: [CommonModule, RouterModule],
})
export class AvailableSubjectsComponent implements OnInit {
  subjects: any[] = [];
  enrolledIds: Set<number> = new Set();
  message: string | null = null;

  constructor(private subjectService: SubjectsService) {}

  ngOnInit(): void {
    this.subjectService.getAll().subscribe((data) => {
      this.subjects = data;
    });

    // Optional: Fetch already enrolled subjects to show "Enrolled" state
    this.subjectService.getEnrolledSubjectIds().subscribe((ids) => {
      this.enrolledIds = new Set(ids);
    });
  }

  enroll(subjectId: number) {
    this.subjectService.enroll(subjectId).subscribe({
      next: () => {
        this.enrolledIds.add(subjectId);
        this.message = 'Successfully enrolled!';
      },
      error: (err) =>
        (this.message = err.error.message || 'Enrollment failed'),
    });
  }
}
