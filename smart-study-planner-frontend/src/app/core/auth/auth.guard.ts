import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.authService.isLoggedIn()) {
      // Check if route requires admin role
      if (route.data['roles'] && route.data['roles'].includes('ADMIN') && !this.authService.isAdmin()) {
        this.router.navigate(['/dashboard']);
        return false;
      }
      
      // Check if route requires student role
      if (route.data['roles'] && route.data['roles'].includes('STUDENT') && !this.authService.isStudent()) {
        this.router.navigate(['/dashboard']);
        return false;
      }
      
      return true;
    }
    
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}