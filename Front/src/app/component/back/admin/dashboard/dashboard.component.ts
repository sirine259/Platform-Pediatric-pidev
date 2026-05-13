import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatGridListModule,
    MatProgressSpinnerModule,
    MatChipsModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: any = {};
  recentActivities: any[] = [];
  alerts: any[] = [];
  isLoading = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.stats = {
        totalPatients: 156,
        activeDoctors: 12,
        pendingTransplants: 8,
        forumPosts: 34,
        totalUsers: 89,
        activeUsers: 67,
        todayAppointments: 24,
        urgentCases: 3
      };

      this.recentActivities = [
        { id: 1, type: 'patient', message: 'Nouveau patient enregistré: Martin Dubois', time: 'il y a 5 min', icon: 'person_add' },
        { id: 2, type: 'transplant', message: 'Transplantation planifiée pour Léa Bernard', time: 'il y a 15 min', icon: 'favorite' },
        { id: 3, type: 'forum', message: 'Nouveau post dans le forum: Questions dialyse', time: 'il y a 30 min', icon: 'forum' },
        { id: 4, type: 'doctor', message: 'Dr. Sophie Martin a mis à jour son planning', time: 'il y a 1 heure', icon: 'medical_services' },
        { id: 5, type: 'urgent', message: 'Cas urgent: Insuffisance rénale aiguë', time: 'il y a 2 heures', icon: 'warning' }
      ];

      this.alerts = [
        { id: 1, type: 'urgent', message: '3 patients en attente de transplantation urgente', severity: 'high' },
        { id: 2, type: 'info', message: 'Mise à jour du système prévue demain', severity: 'medium' },
        { id: 3, type: 'success', message: 'Formation du personnel terminée avec succès', severity: 'low' }
      ];

      this.isLoading = false;
    }, 1000);
  }

  navigateToSection(section: string): void {
    this.router.navigate([`/admin/${section}`]);
  }

  getActivityIcon(activity: any): string {
    return activity.icon || 'info';
  }

  getActivityClass(activity: any): string {
    switch (activity.type) {
      case 'patient': return 'activity-patient';
      case 'transplant': return 'activity-transplant';
      case 'forum': return 'activity-forum';
      case 'doctor': return 'activity-doctor';
      case 'urgent': return 'activity-urgent';
      default: return 'activity-default';
    }
  }

  getAlertClass(alert: any): string {
    switch (alert.severity) {
      case 'high': return 'alert-high';
      case 'medium': return 'alert-medium';
      case 'low': return 'alert-low';
      default: return 'alert-default';
    }
  }

  getAlertIcon(alert: any): string {
    switch (alert.type) {
      case 'urgent': return 'warning';
      case 'info': return 'info';
      case 'success': return 'check_circle';
      default: return 'info';
    }
  }
}
