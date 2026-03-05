import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-patient',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  templateUrl: './patient.component.html',
  styleUrls: ['./patient.component.css']
})
export class PatientComponent implements OnInit {
  currentUser: any = {};
  medicalRecords: any[] = [];
  appointments: any[] = [];
  isLoading = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadMedicalRecords();
    this.loadAppointments();
  }

  loadCurrentUser(): void {
    this.currentUser = {
      id: 1,
      name: 'Martin Dubois',
      role: 'PATIENT',
      age: 12,
      bloodType: 'O+'
    };
  }

  loadMedicalRecords(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.medicalRecords = [
        { id: 1, date: '2024-01-15', type: 'Consultation', doctor: 'Dr. Martin', status: 'Completed' },
        { id: 2, date: '2024-02-20', type: 'Examen', doctor: 'Dr. Sophie', status: 'Pending' }
      ];
      this.isLoading = false;
    }, 1000);
  }

  loadAppointments(): void {
    this.appointments = [
      { id: 1, date: '2024-03-10', time: '10:00', doctor: 'Dr. Martin', type: 'Consultation' },
      { id: 2, date: '2024-03-15', time: '14:30', doctor: 'Dr. Sophie', type: 'Examen' }
    ];
  }

  viewMedicalRecord(record: any): void {
    this.router.navigate(['/front/patient/medical-record', record.id]);
  }

  bookAppointment(): void {
    this.router.navigate(['/front/patient/book-appointment']);
  }

  viewProfile(): void {
    this.router.navigate(['/front/patient/profile']);
  }

  viewTransplantInfo(): void {
    this.router.navigate(['/front/patient/transplant']);
  }
}
