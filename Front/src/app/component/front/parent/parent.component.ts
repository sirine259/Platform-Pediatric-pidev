import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-parent',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  templateUrl: './parent.component.html',
  styleUrls: ['./parent.component.css']
})
export class ParentComponent implements OnInit {
  currentUser: any = {};
  children: any[] = [];
  notifications: any[] = [];
  isLoading = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadChildren();
    this.loadNotifications();
  }

  loadCurrentUser(): void {
    this.currentUser = {
      id: 1,
      name: 'Parent Martin',
      role: 'PARENT',
      childrenCount: 2
    };
  }

  loadChildren(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.children = [
        { id: 1, name: 'Pierre Martin', age: 12, class: '6ème', school: 'École Primaire' },
        { id: 2, name: 'Marie Martin', age: 8, class: 'CM2', school: 'École Primaire' }
      ];
      this.isLoading = false;
    }, 1000);
  }

  loadNotifications(): void {
    this.notifications = [
      { id: 1, type: 'medical', message: 'Rendez-vous médical prévu', date: '2024-03-10', read: false },
      { id: 2, type: 'school', message: 'Bulletin de notes disponible', date: '2024-03-08', read: true }
    ];
  }

  viewChild(child: any): void {
    this.router.navigate(['/front/parent/child-details', child.id]);
  }

  viewMedicalRecords(child: any): void {
    this.router.navigate(['/front/parent/medical-records', child.id]);
  }

  viewAppointments(): void {
    this.router.navigate(['/front/parent/appointments']);
  }

  viewSchoolInfo(): void {
    this.router.navigate(['/front/parent/school-info']);
  }

  markNotificationAsRead(notification: any): void {
    notification.read = true;
  }
}
