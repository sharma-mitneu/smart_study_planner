import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class HeaderComponent {
  
  constructor(
    public authService: AuthService,
    private router: Router
  ) { }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}