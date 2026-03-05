import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';

export interface KidneyTransplant {
  id: string;
  patientName: string;
  patientAge: number;
  diagnosis: string;
  transplantDate: Date;
  donorType: string;
  hospital: string;
  surgeon: string;
  status: string;
  createdAt: Date;
}

@Component({
  selector: 'app-kidney-transplant-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatCardModule
  ],
  templateUrl: './kidney-transplant-list.component.html',
  styleUrls: ['./kidney-transplant-list.component.css']
})
export class KidneyTransplantListComponent implements OnInit {
  transplants: KidneyTransplant[] = [];
  isLoading = false;
  currentUser: any;

  constructor(
    private router: Router
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
  }

  ngOnInit(): void {
    this.loadTransplants();
  }

  loadTransplants(): void {
    this.isLoading = true;
    
    // Simulation de chargement avec données mock
    setTimeout(() => {
      this.transplants = this.getMockTransplants();
      this.isLoading = false;
    }, 1000);
  }

  private getMockTransplants(): KidneyTransplant[] {
    // Données mock depuis localStorage ou données par défaut
    const savedTransplants = JSON.parse(localStorage.getItem('kidneyTransplants') || '[]');
    
    if (savedTransplants.length > 0) {
      return savedTransplants;
    }
    
    // Données mock par défaut
    return [
      {
        id: '1',
        patientName: 'Martin Dupont',
        patientAge: 8,
        diagnosis: 'Insuffisance Rénale Chronique',
        transplantDate: new Date('2024-01-15'),
        donorType: 'Donneur Vivant (père)',
        hospital: 'Hôpital Necker-Enfants Malades',
        surgeon: 'Dr. Sophie Martin',
        status: 'Actif',
        createdAt: new Date('2024-01-15')
      },
      {
        id: '2',
        patientName: 'Sophie Bernard',
        patientAge: 12,
        diagnosis: 'Syndrome Néphrotique',
        transplantDate: new Date('2023-12-20'),
        donorType: 'Donneur Décédé',
        hospital: 'CHU de Rouen',
        surgeon: 'Dr. Pierre Durand',
        status: 'Actif',
        createdAt: new Date('2023-12-20')
      },
      {
        id: '3',
        patientName: 'Lucas Petit',
        patientAge: 5,
        diagnosis: 'Maladie Polykystique',
        transplantDate: new Date('2024-02-10'),
        donorType: 'Donneur Vivant (mère)',
        hospital: 'Hospices Civils de Lyon',
        surgeon: 'Dr. Marie Leroy',
        status: 'En suivi',
        createdAt: new Date('2024-02-10')
      }
    ];
  }

  createTransplant(): void {
    this.router.navigate(['/kidney-transplant/create']);
  }

  viewTransplant(id: string): void {
    this.router.navigate([`/kidney-transplant/kidney-transplant-detail/${id}`]);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'Actif': return 'status-active';
      case 'En suivi': return 'status-followup';
      case 'En attente': return 'status-pending';
      default: return 'status-default';
    }
  }

  getAgeInYears(transplantDate: Date): number {
    const today = new Date();
    const diffTime = Math.abs(today.getTime() - transplantDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    const diffYears = Math.floor(diffDays / 365);
    return diffYears;
  }
}
