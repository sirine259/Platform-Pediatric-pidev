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
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-transplant',
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
  templateUrl: './transplant.component.html',
  styleUrls: ['./transplant.component.css']
})
export class TransplantComponent implements OnInit {
  transplants: any[] = [];
  donors: any[] = [];
  recipients: any[] = [];
  displayedColumns: string[] = ['id', 'patient', 'doctor', 'date', 'status', 'type', 'actions'];
  isLoading = false;
  searchTerm = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadTransplants();
    this.loadDonors();
    this.loadRecipients();
  }

  loadTransplants(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.transplants = [
        { 
          id: 1, 
          patient: 'Martin Dubois',
          doctor: 'Dr. Sophie Martin',
          date: '2024-01-15',
          status: 'completed',
          type: 'Rein'
        },
        { 
          id: 2, 
          patient: 'Léa Bernard',
          doctor: 'Dr. Pierre Durand',
          date: '2024-02-20',
          status: 'planned',
          type: 'Rein'
        },
        { 
          id: 3, 
          patient: 'Thomas Martin',
          doctor: 'Dr. Marie Bernard',
          date: '2024-03-10',
          status: 'in-progress',
          type: 'Rein'
        }
      ];
      this.isLoading = false;
    }, 1000);
  }

  loadDonors(): void {
    this.donors = [
      { id: 1, name: 'Donateur A', bloodType: 'O+', status: 'available' },
      { id: 2, name: 'Donateur B', bloodType: 'A+', status: 'matched' },
      { id: 3, name: 'Donateur C', bloodType: 'B+', status: 'screening' }
    ];
  }

  loadRecipients(): void {
    this.recipients = [
      { id: 1, name: 'Martin Dubois', urgency: 'high', status: 'waiting' },
      { id: 2, name: 'Léa Bernard', urgency: 'medium', status: 'scheduled' },
      { id: 3, name: 'Thomas Martin', urgency: 'low', status: 'completed' }
    ];
  }

  applyFilter(): void {
    // Logique de filtrage
  }

  addTransplant(): void {
    this.router.navigate(['/admin/transplant/add']);
  }

  editTransplant(id: number): void {
    this.router.navigate([`/admin/transplant/edit/${id}`]);
  }

  deleteTransplant(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette transplantation ?')) {
      this.transplants = this.transplants.filter(transplant => transplant.id !== id);
    }
  }

  updateStatus(transplant: any): void {
    const statuses = ['planned', 'in-progress', 'completed', 'cancelled'];
    const currentIndex = statuses.indexOf(transplant.status);
    transplant.status = statuses[(currentIndex + 1) % statuses.length];
  }

  viewDetails(id: number): void {
    this.router.navigate([`/admin/transplant/details/${id}`]);
  }

  viewDonor(id: number): void {
    this.router.navigate([`/admin/transplant/donor/${id}`]);
  }

  viewRecipient(id: number): void {
    this.router.navigate([`/admin/transplant/recipient/${id}`]);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'completed': return 'status-completed';
      case 'planned': return 'status-planned';
      case 'in-progress': return 'status-in-progress';
      case 'cancelled': return 'status-cancelled';
      default: return 'status-default';
    }
  }

  getUrgencyClass(urgency: string): string {
    switch (urgency) {
      case 'high': return 'urgency-high';
      case 'medium': return 'urgency-medium';
      case 'low': return 'urgency-low';
      default: return 'urgency-default';
    }
  }

  get availableDonorsCount(): number {
    return this.donors.filter(d => d.status === 'available').length;
  }

  get waitingRecipientsCount(): number {
    return this.recipients.filter(r => r.status === 'waiting').length;
  }

  get activeTransplantsCount(): number {
    return this.transplants.filter(t => t.status === 'in-progress').length;
  }
}
