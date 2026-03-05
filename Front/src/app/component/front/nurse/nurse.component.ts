import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-nurse',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  templateUrl: './nurse.component.html',
  styleUrls: ['./nurse.component.css']
})
export class NurseComponent implements OnInit {
  currentUser: any = {};
  patients: any[] = [];
  schedules: any[] = [];
  isLoading = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadPatients();
    this.loadSchedules();
  }

  loadCurrentUser(): void {
    this.currentUser = {
      id: 1,
      name: 'Infirmière Dubois',
      role: 'NURSE',
      department: 'Pédiatrie'
    };
  }

  loadPatients(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.patients = [
        { id: 1, name: 'Martin Dubois', age: 12, room: 'A101', condition: 'Stable' },
        { id: 2, name: 'Sophie Martin', age: 8, room: 'B203', condition: 'Critical' }
      ];
      this.isLoading = false;
    }, 1000);
  }

  loadSchedules(): void {
    this.schedules = [
      { id: 1, date: '2024-03-10', shift: 'Matin', patients: 5 },
      { id: 2, date: '2024-03-10', shift: 'Soir', patients: 3 }
    ];
  }

  viewPatient(patient: any): void {
    this.router.navigate(['/front/nurse/patient-details', patient.id]);
  }

  updateVitals(patient: any): void {
    this.router.navigate(['/front/nurse/vitals', patient.id]);
  }

  viewSchedule(): void {
    this.router.navigate(['/front/nurse/schedule']);
  }

  viewMedications(): void {
    this.router.navigate(['/front/nurse/medications']);
  }
}
