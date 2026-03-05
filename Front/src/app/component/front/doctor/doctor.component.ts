import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-doctor',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  templateUrl: './doctor.component.html',
  styleUrls: ['./doctor.component.css']
})
export class DoctorComponent implements OnInit {
  currentUser: any = {};
  doctors: any[] = [];
  isLoading = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadDoctors();
  }

  loadCurrentUser(): void {
    // TODO: Charger depuis AuthService
    this.currentUser = {
      id: 1,
      name: 'Dr. Martin',
      role: 'DOCTOR',
      specialty: 'Néphrologie'
    };
  }

  loadDoctors(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.doctors = [
        { id: 1, name: 'Dr. Martin', specialty: 'Néphrologie', email: 'martin@hospital.com', available: true },
        { id: 2, name: 'Dr. Sophie', specialty: 'Pédiatrie', email: 'sophie@hospital.com', available: false },
        { id: 3, name: 'Dr. Bernard', specialty: 'Chirurgie', email: 'bernard@hospital.com', available: true }
      ];
      this.isLoading = false;
    }, 1000);
  }

  viewDoctorProfile(doctor: any): void {
    this.router.navigate(['/front/doctor/profile', doctor.id]);
  }

  contactDoctor(doctor: any): void {
    // TODO: Implémenter la messagerie
    console.log('Contacter le médecin:', doctor.name);
  }

  viewAppointments(): void {
    this.router.navigate(['/front/doctor/appointments']);
  }

  viewPatients(): void {
    this.router.navigate(['/front/doctor/patients']);
  }

  viewSchedule(): void {
    this.router.navigate(['/front/doctor/schedule']);
  }
}
