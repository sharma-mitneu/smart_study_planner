import { Pipe, PipeTransform } from '@angular/core';
import { Priority } from '../../core/models/subject.model';

@Pipe({
  name: 'priorityLabel',
  standalone: true
})
export class PriorityLabelPipe implements PipeTransform {
  
  transform(priority: Priority): string {
    switch (priority) {
      case Priority.HIGH:
        return 'High';
      case Priority.MEDIUM:
        return 'Medium';
      case Priority.LOW:
        return 'Low';
      default:
        return '';
    }
  }
}