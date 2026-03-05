import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';

@Component({
  selector: 'app-follow-up-update',
  templateUrl: './follow-up-update.component.html',
  styleUrls: ['./follow-up-update.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatDatepickerModule],
})
export class FollowUpUpdateComponent implements OnInit {
  followUp: any = {
    date: '',
    type: 'POST_TRANSPLANT',
    doctor: '',
    notes: ''
  };
  isLoading = false;
  isSubmitting = false;
  followUpId: number = 0;
  transplantId: number = 0;

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
    }
  }

  loadFollowUpDetails(): void {
    this.isLoading = true;
    this.http.get(`http://localhost:8080/api/kidney-transplants/${this.transplantId}/follow-ups/${this.followUpId}`).subscribe({
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

  onSubmit(): void {
    if (!this.followUp.date || !this.followUp.doctor || !this.followUp.notes) {
      alert('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isSubmitting = true;

    const followUpData = {
      date: this.followUp.date,
      type: this.followUp.type,
      doctor: this.followUp.doctor,
      notes: this.followUp.notes
    };

    this.http.put(`http://localhost:8080/api/kidney-transplants/${this.transplantId}/follow-ups/${this.followUpId}`, followUpData).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        alert('Suivi de transplantation mis à jour avec succès!');
        this.router.navigate(['/kidney-transplant/post-transplant-follow-up/follow-up-detail', this.transplantId, this.followUpId]);
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Erreur lors de la mise à jour:', error);
        alert('Erreur lors de la mise à jour du suivi');
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/kidney-transplant/post-transplant-follow-up/follow-up-detail', this.transplantId, this.followUpId]);
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
}
