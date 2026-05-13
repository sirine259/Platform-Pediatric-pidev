import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-doctor',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    FormsModule,
    MatProgressSpinnerModule,
    MatTooltipModule
  ],
  templateUrl: './doctor.component.html',
  styleUrls: ['./doctor.component.css']
})
export class DoctorComponent implements OnInit {
  doctors: any[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'specialization', 'license', 'status', 'actions'];
  isLoading = false;
  searchTerm = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadDoctors();
  }

  loadDoctors(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.doctors = [
        { 
          id: 1, 
          name: 'Dr. Sophie Martin', 
          email: 'martin@pediatric.com', 
          specialization: 'Néphrologie pédiatrique', 
          license: 'MED-12345',
          status: 'active' 
        },
        { 
          id: 2, 
          name: 'Dr. Pierre Durand', 
          email: 'durand@pediatric.com', 
          specialization: 'Chirurgie pédiatrique', 
          license: 'MED-67890',
          status: 'active' 
        },
        { 
          id: 3, 
          name: 'Dr. Marie Bernard', 
          email: 'bernard@pediatric.com', 
          specialization: 'Pédiatrie générale', 
          license: 'MED-11111',
          status: 'inactive' 
        }
      ];
      this.isLoading = false;
    }, 1000);
  }

  applyFilter(): void {
    // Logique de filtrage
  }

  addDoctor(): void {
    this.router.navigate(['/admin/doctors/add']);
  }

  editDoctor(id: number): void {
    this.router.navigate([`/admin/doctors/edit/${id}`]);
  }

  deleteDoctor(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce médecin ?')) {
      this.doctors = this.doctors.filter(doctor => doctor.id !== id);
    }
  }

  toggleStatus(doctor: any): void {
    doctor.status = doctor.status === 'active' ? 'inactive' : 'active';
  }

  viewDetails(id: number): void {
    this.router.navigate([`/admin/doctors/details/${id}`]);
  }

  viewSchedule(id: number): void {
    this.router.navigate([`/admin/doctors/schedule/${id}`]);
  }

  // Computed properties for statistics
  getActiveDoctorsCount(): number {
    return this.doctors.filter(d => d.status === 'active').length;
  }

  getNephrologyDoctorsCount(): number {
    return this.doctors.filter(d => d.specialization.includes('Néphrologie')).length;
  }

  getSurgeryDoctorsCount(): number {
    return this.doctors.filter(d => d.specialization.includes('Chirurgie')).length;
  }

  getMonthlyConsultations(): number {
    return this.getActiveDoctorsCount() * 5;
  }

  getTrainingSessions(): number {
    return this.doctors.length * 3;
  }

  getStatusClass(status: string): string {
    return status === 'active' ? 'status-active' : 'status-inactive';
  }

  getSpecializationClass(specialization: string): string {
    if (specialization.includes('Néphrologie')) return 'spec-nephrology';
    if (specialization.includes('Chirurgie')) return 'spec-surgery';
    if (specialization.includes('Pédiatrie')) return 'spec-pediatrics';
    return 'spec-default';
  }
}
