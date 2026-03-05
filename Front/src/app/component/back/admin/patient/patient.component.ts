import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-patient',
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
  templateUrl: './patient.component.html',
  styleUrls: ['./patient.component.css']
})
export class PatientComponent implements OnInit {
  patients: any[] = [];
  displayedColumns: string[] = ['id', 'name', 'medicalRecord', 'age', 'diagnosis', 'status', 'actions'];
  isLoading = false;
  searchTerm = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.patients = [
        { 
          id: 1, 
          name: 'Martin Dubois', 
          medicalRecord: 'MR-2024-001',
          age: 8,
          diagnosis: 'Insuffisance rénale chronique',
          status: 'active' 
        },
        { 
          id: 2, 
          name: 'Léa Bernard', 
          medicalRecord: 'MR-2024-002',
          age: 12,
          diagnosis: 'Néphrite aiguë',
          status: 'active' 
        },
        { 
          id: 3, 
          name: 'Thomas Martin', 
          medicalRecord: 'MR-2024-003',
          age: 6,
          diagnosis: 'Syndrome néphrotique',
          status: 'inactive' 
        }
      ];
      this.isLoading = false;
    }, 1000);
  }

  applyFilter(): void {
    // Logique de filtrage
  }

  addPatient(): void {
    this.router.navigate(['/admin/patients/add']);
  }

  editPatient(id: number): void {
    this.router.navigate([`/admin/patients/edit/${id}`]);
  }

  deletePatient(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce patient ?')) {
      this.patients = this.patients.filter(patient => patient.id !== id);
    }
  }

  toggleStatus(patient: any): void {
    patient.status = patient.status === 'active' ? 'inactive' : 'active';
  }

  viewDetails(id: number): void {
    this.router.navigate([`/admin/patients/details/${id}`]);
  }

  viewMedicalRecord(id: number): void {
    this.router.navigate([`/admin/patients/${id}/medical-record`]);
  }

  getDiagnosisClass(diagnosis: string): string {
    if (diagnosis.includes('Insuffisance')) return 'diagnosis-renal';
    if (diagnosis.includes('Néphrite')) return 'diagnosis-nephritis';
    if (diagnosis.includes('Syndrome')) return 'diagnosis-syndrome';
    return 'diagnosis-default';
  }

  getStatusClass(status: string): string {
    return status === 'active' ? 'status-active' : 'status-inactive';
  }

  get activePatientsCount(): number {
    return this.patients.filter(p => p.status === 'active').length;
  }

  get renalFailureCount(): number {
    return this.patients.filter(p => p.diagnosis.includes('Insuffisance')).length;
  }

  get transplantCount(): number {
    return this.patients.filter(p => p.diagnosis.includes('Syndrome')).length;
  }

  get monthlyConsultations(): number {
    return this.activePatientsCount * 2;
  }
}
