import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';

export interface Forum {
  id?: string;
  title: string;
  description: string;
  category: string;
  isPrivate: boolean;
  createdBy: string;
  createdAt?: Date;
  updatedAt?: Date;
  memberCount?: number;
  postCount?: number;
}

@Component({
  selector: 'app-forum-create',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatCardModule
  ],
  templateUrl: './forum-create.component.html',
  styleUrls: ['./forum-create.component.css']
})
export class ForumCreateComponent implements OnInit {
  forumForm: FormGroup;
  isLoading = false;
  isSubmitting = false;

  // Options pour les catégories de forums pédiatriques
  categories = [
    { value: 'transplantation-renale', label: 'Transplantation Rénale' },
    { value: 'dialyse-peritoneale', label: 'Dialyse Péritonéale' },
    { value: 'hemodialyse', label: 'Hémodialyse' },
    { value: 'nutrition-pediatrique', label: 'Nutrition Pédiatrique' },
    { value: 'maladies-renales', label: 'Maladies Rénales Héréditaires' },
    { value: 'suivi-medical', label: 'Suivi Médical' },
    { value: 'vie-quotidienne', label: 'Vie Quotidienne' },
    { value: 'soutien-psychologique', label: 'Soutien Psychologique' },
    { value: 'experiences-parents', label: 'Expériences Parents' },
    { value: 'conseils-medicaux', label: 'Conseils Médicaux' }
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.forumForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      category: ['transplantation-renale', Validators.required],
      isPrivate: [false],
      allowAnonymous: [true],
      requireModeration: [false]
    });
  }

  ngOnInit(): void {}

  // Getters pour faciliter l'accès aux contrôles
  get title() { return this.forumForm.get('title'); }
  get description() { return this.forumForm.get('description'); }
  get category() { return this.forumForm.get('category'); }

  onSubmit(): void {
    if (this.forumForm.invalid) {
      this.forumForm.markAllAsTouched();
      this.showErrorMessage('Veuillez corriger les erreurs dans le formulaire');
      return;
    }

    this.isSubmitting = true;
    this.isLoading = true;
    
    const forumData: Forum = {
      ...this.forumForm.value,
      createdBy: 'Utilisateur',
      createdAt: new Date(),
      updatedAt: new Date(),
      memberCount: 1,
      postCount: 0
    };

    // Simulation de sauvegarde
    this.saveForum(forumData);
  }

  private saveForum(forumData: Forum): void {
    setTimeout(() => {
      try {
        // Simuler une sauvegarde réussie
        console.log('Forum créé:', forumData);
        
        // Stocker dans localStorage pour la démo
        const existingForums = JSON.parse(localStorage.getItem('forums') || '[]');
        forumData.id = Date.now().toString();
        existingForums.push(forumData);
        localStorage.setItem('forums', JSON.stringify(existingForums));
        
        this.isSubmitting = false;
        this.isLoading = false;
        
        this.showSuccessMessage('Forum créé avec succès!');
        
        // Redirection vers la liste des forums après un court délai
        setTimeout(() => {
          this.router.navigate(['/forum/list']);
        }, 1500);
        
      } catch (error) {
        console.error('Erreur lors de la sauvegarde:', error);
        this.isSubmitting = false;
        this.isLoading = false;
        this.showErrorMessage('Erreur lors de la création du forum. Veuillez réessayer.');
      }
    }, 2000);
  }

  cancel(): void {
    if (this.forumForm.dirty) {
      if (confirm('Êtes-vous sûr de vouloir annuler? Les données non sauvegardées seront perdues.')) {
        this.router.navigate(['/forum/list']);
      }
    } else {
      this.router.navigate(['/forum/list']);
    }
  }

  resetForm(): void {
    if (confirm('Êtes-vous sûr de vouloir réinitialiser le formulaire?')) {
      this.forumForm.reset({
        category: 'transplantation-renale',
        isPrivate: false,
        allowAnonymous: true,
        requireModeration: false
      });
    }
  }

  // Méthodes pour les messages
  private showSuccessMessage(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['success-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  private showErrorMessage(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 5000,
      panelClass: ['error-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  // Validation personnalisée
  validateTitle(): boolean {
    const title = this.forumForm.get('title')?.value;
    return title && title.trim().length >= 3 && title.trim().length <= 100;
  }

  validateDescription(): boolean {
    const description = this.forumForm.get('description')?.value;
    return description && description.trim().length >= 10 && description.trim().length <= 500;
  }

  // Méthode pour obtenir le label de la catégorie
  getCategoryLabel(categoryValue: string): string {
    const category = this.categories.find(cat => cat.value === categoryValue);
    return category ? category.label : 'Non spécifiée';
  }
}
