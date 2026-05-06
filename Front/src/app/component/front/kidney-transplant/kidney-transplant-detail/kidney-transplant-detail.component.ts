import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

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
  apiUrl = environment.apiUrl;

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
    this.http.get(`${this.apiUrl}/api/kidney-transplants/${this.transplantId}`).subscribe({
      next: (data: any) => {
        this.transplant = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement:', error);
        // Charger les données mock si l'API échoue
        this.transplant = this.getMockTransplantDetail(this.transplantId);
        this.isLoading = false;
      }
    });
  }

  private getMockTransplantDetail(id: number): any {
    const mockData: { [key: number]: any } = {
      1: {
        id: 1,
        patientName: 'Martin Dupont',
        patientAge: 8,
        bloodType: 'O+',
        diagnosis: 'Insuffisance Rénale Chronique',
        transplantDate: '2024-01-15T00:00:00',
        donorName: 'Jean Dupont (père)',
        donorType: 'Vivant',
        status: 'Actif',
        hospital: 'Hôpital Necker-Enfants Malades',
        surgeon: 'Dr. Sophie Martin',
        createdAt: '2024-01-15T00:00:00'
      },
      2: {
        id: 2,
        patientName: 'Sophie Bernard',
        patientAge: 12,
        bloodType: 'A+',
        diagnosis: 'Syndrome Néphrotique',
        transplantDate: '2023-12-20T00:00:00',
        donorName: 'Donneur Décédé',
        donorType: 'Décédé',
        status: 'Actif',
        hospital: 'CHU de Rouen',
        surgeon: 'Dr. Pierre Durand',
        createdAt: '2023-12-20T00:00:00'
      },
      3: {
        id: 3,
        patientName: 'Lucas Petit',
        patientAge: 5,
        bloodType: 'B+',
        diagnosis: 'Maladie Polykystique',
        transplantDate: '2024-02-10T00:00:00',
        donorName: 'Marie Petit (mère)',
        donorType: 'Vivant',
        status: 'En suivi',
        hospital: 'Hospices Civils de Lyon',
        surgeon: 'Dr. Marie Leroy',
        createdAt: '2024-02-10T00:00:00'
      }
    };
    
    return mockData[id] || mockData[1]; // Retourne le premier par défaut
  }

  loadMedicalRecords(): void {
    this.http.get(`${this.apiUrl}/api/kidney-transplants/${this.transplantId}/medical-records`).subscribe({
      next: (data: any) => {
        this.medicalRecords = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des dossiers médicaux:', error);
        // Charger des données mock
        this.medicalRecords = this.getMockMedicalRecords();
      }
    });
  }

  loadFollowUps(): void {
    this.http.get(`${this.apiUrl}/api/kidney-transplants/${this.transplantId}/follow-ups`).subscribe({
      next: (data: any) => {
        this.followUps = data.reverse();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des suivis:', error);
        // Charger des données mock
        this.followUps = this.getMockFollowUps().reverse();
      }
    });
  }

  private getMockMedicalRecords(): any[] {
    return [
      {
        id: 1,
        transplantId: this.transplantId,
        type: 'PRE_TRANSPLANT',
        notes: 'Évaluation complète du patient',
        date: '2024-01-10T00:00:00',
        doctor: 'Dr. Jean Martin'
      },
      {
        id: 2,
        transplantId: this.transplantId,
        type: 'SURGERY',
        notes: 'Transplantation rénale réussie',
        date: '2024-01-15T00:00:00',
        doctor: 'Dr. Sophie Martin'
      }
    ];
  }

  private getMockFollowUps(): any[] {
    return [
      {
        id: 1,
        transplantId: this.transplantId,
        date: '2024-02-15T00:00:00',
        type: 'ROUTINE',
        creatinineLevel: 1.2,
        gfr: 65,
        bloodPressure: '120/80',
        doctor: 'Dr. Sophie Martin',
        notes: 'Patient en bon état, greffon fonctionnel',
        isFollowUpComplete: true
      },
      {
        id: 2,
        transplantId: this.transplantId,
        date: '2024-03-15T00:00:00',
        type: 'ROUTINE',
        creatinineLevel: 1.15,
        gfr: 68,
        bloodPressure: '118/78',
        doctor: 'Dr. Sophie Martin',
        notes: 'Évolution positive, continuer le traitement',
        isFollowUpComplete: true
      }
    ];
  }

  createFollowUp(): void {
    this.router.navigate([`/kidney-transplant/post-transplant-follow-up/${this.transplantId}/follow-up-create`]);
  }

  updateFollowUp(followUpId: number): void {
    this.router.navigate([`/kidney-transplant/post-transplant-follow-up/${this.transplantId}/follow-up-update/${followUpId}`]);
  }

  deleteFollowUp(followUpId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce suivi?')) {
      this.http.delete(`${this.apiUrl}/api/kidney-transplants/${this.transplantId}/follow-ups/${followUpId}`).subscribe({
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
    if (!status) return 'secondary';
    switch(status.toUpperCase()) {
      case 'ACTIF':
      case 'ACTIVE':
        return 'success';
      case 'EN SUIVI':
      case 'PENDING':
        return 'warning';
      case 'COMPLETED':
        return 'info';
      case 'CANCELLED':
        return 'danger';
      default: return 'secondary';
    }
  }

  getRecordTypeColor(type: string): string {
    if (!type) return 'secondary';
    switch(type) {
      case 'PRE_TRANSPLANT': return 'info';
      case 'SURGERY': return 'warning';
      case 'FOLLOW_UP': return 'success';
      default: return 'secondary';
    }
  }

  getRecordTypeLabel(type: string): string {
    if (!type) return 'N/A';
    switch(type) {
      case 'PRE_TRANSPLANT': return 'Pré-transplantation';
      case 'SURGERY': return 'Chirurgie';
      case 'FOLLOW_UP': return 'Suivi';
      default: return type;
    }
  }

  getFollowUpTypeColor(type: string): string {
    if (!type) return 'secondary';
    switch(type) {
      case 'ROUTINE': return 'success';
      case 'URGENT': return 'warning';
      case 'EMERGENCY': return 'danger';
      default: return 'secondary';
    }
  }

  getFollowUpTypeLabel(type: string): string {
    if (!type) return 'N/A';
    switch(type) {
      case 'ROUTINE': return 'Suivi Routinier';
      case 'URGENT': return 'Suivi Urgent';
      case 'EMERGENCY': return 'Urgence';
      default: return type;
    }
  }

  getStatusLabel(status: string): string {
    if (!status) return 'N/A';
    switch(status.toUpperCase()) {
      case 'ACTIF':
      case 'ACTIVE':
        return 'Actif';
      case 'EN SUIVI':
      case 'PENDING':
        return 'En suivi';
      case 'COMPLETED':
        return 'Terminée';
      case 'CANCELLED':
        return 'Annulée';
      default: return status;
    }
  }

  formatDate(date: string): string {
    const d = new Date(date);
    return d.toLocaleDateString('fr-FR');
  }
}
