import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../../environments/environment';

@Component({
  selector: 'app-follow-up-detail',
  templateUrl: './follow-up-detail.component.html',
  styleUrls: ['./follow-up-detail.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class FollowUpDetailComponent implements OnInit {
getStatusLabel(arg0: any) {
throw new Error('Method not implemented.');
}
getStatusColor(arg0: any) {
throw new Error('Method not implemented.');
}
  followUp: any = null;
  transplant: any = null;
  isLoading = false;
  followUpId: number = 0;
  transplantId: number = 0;
  apiUrl = environment.apiUrl;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.followUpId = Number(this.route.snapshot.paramMap.get('fid'));
    this.transplantId = Number(this.route.snapshot.paramMap.get('id'));
    
    if (this.followUpId && this.transplantId) {
      this.loadFollowUpDetails();
      this.loadTransplantDetails();
    }
  }

  loadFollowUpDetails(): void {
    this.isLoading = true;
    this.http.get(`${this.apiUrl}/api/kidney-transplants/${this.transplantId}/follow-ups/${this.followUpId}`).subscribe({
      next: (data: any) => {
        this.followUp = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement:', error);
        this.isLoading = false;
      }
    });
  }

  loadTransplantDetails(): void {
    this.http.get(`${this.apiUrl}/api/kidney-transplants/${this.transplantId}`).subscribe({
      next: (data: any) => {
        this.transplant = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de la transplantation:', error);
      }
    });
  }

  updateFollowUp(): void {
    this.router.navigate(['/kidney-transplant/post-transplant-follow-up/follow-up-update', this.transplantId, this.followUpId]);
  }

  deleteFollowUp(): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce suivi?')) {
      this.http.delete(`${this.apiUrl}/api/kidney-transplants/${this.transplantId}/follow-ups/${this.followUpId}`).subscribe({
        next: () => {
          alert('Suivi supprimé avec succès!');
          this.router.navigate(['/kidney-transplant/kidney-transplant-detail', this.transplantId]);
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur lors de la suppression');
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/kidney-transplant/kidney-transplant-detail', this.transplantId]);
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
