import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-kidney-transplant-detail',
  templateUrl: './kidney-transplant-detail.component.html',
  styleUrls: ['./kidney-transplant-detail.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class KidneyTransplantDetailComponent implements OnInit {
  transplant: any = null;
  medicalRecords: any[] = [];
  followUps: any[] = [];
  isLoading = false;
  transplantId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.transplantId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.transplantId) {
      this.loadTransplantDetails();
      this.loadMedicalRecords();
      this.loadFollowUps();
    }
  }

  loadTransplantDetails(): void {
    this.isLoading = true;
    this.http.get(`http://localhost:8080/api/kidney-transplants/${this.transplantId}`).subscribe({
      next: (data: any) => {
        this.transplant = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement:', error);
        this.isLoading = false;
      }
    });
  }

  loadMedicalRecords(): void {
    this.http.get(`http://localhost:8080/api/kidney-transplants/${this.transplantId}/medical-records`).subscribe({
      next: (data: any) => {
        this.medicalRecords = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des dossiers médicaux:', error);
      }
    });
  }

  loadFollowUps(): void {
    this.http.get(`http://localhost:8080/api/kidney-transplants/${this.transplantId}/follow-ups`).subscribe({
      next: (data: any) => {
        this.followUps = data.reverse();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des suivis:', error);
      }
    });
  }

  createFollowUp(): void {
    this.router.navigate(['/kidney-transplant/post-transplant-follow-up', this.transplantId]);
  }

  updateFollowUp(followUpId: number): void {
    this.router.navigate(['/kidney-transplant/post-transplant-follow-up', followUpId]);
  }

  deleteFollowUp(followUpId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce suivi?')) {
      this.http.delete(`http://localhost:8080/api/kidney-transplants/${this.transplantId}/follow-ups/${followUpId}`).subscribe({
        next: () => {
          alert('Suivi supprimé avec succès!');
          this.loadFollowUps();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur lors de la suppression');
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/kidney-transplant/kidney-transplant-list']);
  }

  getStatusColor(status: string): string {
    switch(status) {
      case 'PENDING': return 'warning';
      case 'APPROVED': return 'success';
      case 'COMPLETED': return 'info';
      case 'CANCELLED': return 'danger';
      default: return 'secondary';
    }
  }

  getStatusLabel(status: string): string {
    switch(status) {
      case 'PENDING': return 'En attente';
      case 'APPROVED': return 'Approuvée';
      case 'COMPLETED': return 'Terminée';
      case 'CANCELLED': return 'Annulée';
      default: return 'Inconnue';
    }
  }

  getRecordTypeColor(type: string): string {
    switch(type) {
      case 'CONSULTATION': return 'primary';
      case 'ANALYSE': return 'success';
      case 'IMAGING': return 'warning';
      case 'BIOPSY': return 'info';
      default: return 'secondary';
    }
  }

  getRecordTypeLabel(type: string): string {
    switch(type) {
      case 'CONSULTATION': return 'Consultation';
      case 'ANALYSE': return 'Analyse';
      case 'IMAGING': return 'Imagerie';
      case 'BIOPSY': return 'Biopsie';
      default: return 'Autre';
    }
  }

  getFollowUpTypeColor(type: string): string {
    switch(type) {
      case 'POST_TRANSPLANT': return 'success';
      case 'REJECTION': return 'danger';
      case 'COMPLICATION': return 'warning';
      case 'RECOVERY': return 'info';
      default: return 'secondary';
    }
  }

  getFollowUpTypeLabel(type: string): string {
    switch(type) {
      case 'POST_TRANSPLANT': return 'Post-Transplantation';
      case 'REJECTION': return 'Rejet';
      case 'COMPLICATION': return 'Complication';
      case 'RECOVERY': return 'Rétablissement';
      default: return 'Autre';
    }
  }

  formatDate(date: string): string {
    const d = new Date(date);
    return d.toLocaleDateString('fr-FR');
  }
}
