import { Directive, ElementRef, Input, OnInit, Renderer2 } from '@angular/core';

@Directive({
  selector: '[appHighlightOverdue]',
  standalone: true
})
export class HighlightOverdueDirective implements OnInit {
  @Input() appHighlightOverdue: boolean = false;
  
  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) { }
  
  ngOnInit() {
    if (this.appHighlightOverdue) {
      this.renderer.addClass(this.el.nativeElement, 'overdue-task');
      this.renderer.setStyle(this.el.nativeElement, 'background-color', 'rgba(231, 76, 60, 0.1)');
      this.renderer.setStyle(this.el.nativeElement, 'border-left', '4px solid #e74c3c');
    }
  }
}