import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-nurse',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './nurse.component.html',
  styleUrls: ['./nurse.component.css']
})
export class NurseComponent implements OnInit {
  nurses: any[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'department', 'license', 'status', 'actions'];
  isLoading = false;
  searchTerm = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadNurses();
  }

  loadNurses(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.nurses = [
        { 
          id: 1, 
          name: 'Sophie Laurent', 
          email: 'laurent@pediatric.com', 
          department: 'Néphrologie', 
          license: 'INF-12345',
          status: 'active' 
        },
        { 
          id: 2, 
          name: 'Marie Petit', 
          email: 'petit@pediatric.com', 
          department: 'Urgences pédiatriques', 
          license: 'INF-67890',
          status: 'active' 
        },
        { 
          id: 3, 
          name: 'Claude Dubois', 
          email: 'dubois@pediatric.com', 
          department: 'Chirurgie', 
          license: 'INF-11111',
          status: 'inactive' 
        }
      ];
      this.isLoading = false;
    }, 1000);
  }

  applyFilter(): void {
    // Logique de filtrage
  }

  addNurse(): void {
    this.router.navigate(['/admin/nurses/add']);
  }

  editNurse(id: number): void {
    this.router.navigate([`/admin/nurses/edit/${id}`]);
  }

  deleteNurse(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet infirmier ?')) {
      this.nurses = this.nurses.filter(nurse => nurse.id !== id);
    }
  }

  toggleStatus(nurse: any): void {
    nurse.status = nurse.status === 'active' ? 'inactive' : 'active';
  }

  viewDetails(id: number): void {
    this.router.navigate([`/admin/nurses/details/${id}`]);
  }

  getDepartmentClass(department: string): string {
    if (department.includes('Néphrologie')) return 'dept-nephrology';
    if (department.includes('Urgences')) return 'dept-emergency';
    if (department.includes('Chirurgie')) return 'dept-surgery';
    return 'dept-default';
  }

  getActiveNursesCount(): number {
    return this.nurses.filter(n => n.status === 'active').length;
  }

  getNephrologyNursesCount(): number {
    return this.nurses.filter(n => n.department.includes('Néphrologie')).length;
  }

  getEmergencyNursesCount(): number {
    return this.nurses.filter(n => n.department.includes('Urgences')).length;
  }

  getMonthlyShiftsCount(): number {
    return this.getActiveNursesCount() * 4;
  }

  getTrainingSessionsCount(): number {
    return this.nurses.length * 2;
  }

  getStatusClass(status: string): string {
    return status === 'active' ? 'status-active' : 'status-inactive';
  }

  viewSchedule(id: number): void {
    this.router.navigate([`/admin/nurses/schedule/${id}`]);
  }
}
