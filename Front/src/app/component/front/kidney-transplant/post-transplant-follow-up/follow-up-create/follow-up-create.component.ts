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
  selector: 'app-post-transplant-follow-up',
  templateUrl: './follow-up-create.component.html',
  styleUrls: ['./follow-up-create.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatDatepickerModule],
})
export class PostTransplantFollowUpComponent implements OnInit {
  transplantId: number = 0;
  followUp: any = {
    date: '',
    type: 'POST_TRANSPLANT',
    doctor: '',
    notes: ''
  };
  isLoading = false;
  isSubmitting = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.transplantId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.transplantId) {
      this.loadTransplantDetails();
    }
  }

  loadTransplantDetails(): void {
    this.isLoading = true;
    this.http.get(`http://localhost:8080/api/kidney-transplants/${this.transplantId}`).subscribe({
      next: (data: any) => {
        this.transplantId = data.id;
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
      transplantId: this.transplantId,
      date: this.followUp.date,
      type: this.followUp.type,
      doctor: this.followUp.doctor,
      notes: this.followUp.notes
    };

    this.http.post(`http://localhost:8080/api/kidney-transplants/${this.transplantId}/follow-ups`, followUpData).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        alert('Suivi de transplantation créé avec succès!');
        this.router.navigate(['/kidney-transplant/kidney-transplant-detail', this.transplantId]);
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Erreur lors de la création:', error);
        alert('Erreur lors de la création du suivi');
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/kidney-transplant/kidney-transplant-detail', this.transplantId]);
  }
}
