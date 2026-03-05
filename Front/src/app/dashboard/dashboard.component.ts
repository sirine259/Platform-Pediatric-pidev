import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatCardModule, MatButtonModule, MatToolbarModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  currentUser: any;
  userRole: string = '';

  constructor(
    public authService: AuthService,
    private router: Router
  ) {
    this.currentUser = this.authService.getCurrentUser();
  }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();
    this.userRole = this.currentUser?.role || '';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getWelcomeMessage(): string {
    const firstName = this.currentUser?.firstName || '';
    const role = this.userRole;
    
    switch (role) {
      case 'ADMIN':
        return `Bienvenue Administrateur ${firstName}`;
      case 'DOCTOR':
      case 'SURGEON':
        return `Bienvenue Docteur ${firstName}`;
      case 'PATIENT':
        return `Bienvenue ${firstName}`;
      default:
        return `Bienvenue ${firstName}`;
    }
  }

  getRoleLabel(): string {
    switch (this.userRole) {
      case 'ADMIN':
        return 'Administrateur';
      case 'DOCTOR':
        return 'Docteur';
      case 'SURGEON':
        return 'Chirurgien';
      case 'PATIENT':
        return 'Patient';
      default:
        return 'Utilisateur';
    }
  }
}
