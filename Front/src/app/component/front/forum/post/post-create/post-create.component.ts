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
import { ForumService } from '../../../../../services/forum.service';

@Component({
  selector: 'app-post-create',
  templateUrl: './post-create.component.html',
  styleUrls: ['./post-create.component.css'],
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    ReactiveFormsModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatCardModule
  ]
})
export class PostCreateComponent implements OnInit {
  postForm: FormGroup;
  isLoading = false;
  isSubmitting = false;

  // Options pour les types de posts
  postTypes = [
    { value: 'question', label: 'Question' },
    { value: 'experience', label: 'Expérience' },
    { value: 'conseil', label: 'Conseil' },
    { value: 'information', label: 'Information' },
    { value: 'soutien', label: 'Soutien' }
  ];

  // Getters pour faciliter l'accès aux contrôles
  get title() { return this.postForm.get('title'); }
  get content() { return this.postForm.get('content'); }
  get postType() { return this.postForm.get('postType'); }
  get forumId() { return this.postForm.get('forumId'); }

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar,
    private forumService: ForumService
  ) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      content: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(2000)]],
      postType: ['question', Validators.required],
      forumId: ['1', Validators.required], // Forum par défaut
      isAnonymous: [false],
      tags: ['']
    });
  }

  ngOnInit(): void {
    // Initialisation
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      this.showErrorMessage('Veuillez corriger les erreurs dans le formulaire');
      return;
    }

    this.isSubmitting = true;
    this.isLoading = true;
    
    const post = {
      subject: this.postForm.value.title,
      content: this.postForm.value.content,
      isAnonymous: !!this.postForm.value.isAnonymous,
      picture: null,
      datePost: new Date().toISOString(),
      user: this.postForm.value.isAnonymous
        ? { userName: 'Anonymous' }
        : { userName: 'Utilisateur' },
      comments: []
    };

    this.forumService.addPost(post).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.isLoading = false;
        this.showSuccessMessage('Post créé avec succès!');
        // Retour vers la page principale des posts
        setTimeout(() => this.router.navigate(['/forum']), 500);
      },
      error: (err) => {
        console.error('Erreur lors de la création du post:', err);
        this.isSubmitting = false;
        this.isLoading = false;
        this.showErrorMessage('Erreur lors de la création du post. Veuillez réessayer.');
      }
    });
  }

  cancel(): void {
    if (this.postForm.dirty) {
      if (confirm('Êtes-vous sûr de vouloir annuler? Les données non sauvegardées seront perdues.')) {
        this.router.navigate(['/forum']);
      }
    } else {
      this.router.navigate(['/forum']);
    }
  }

  resetForm(): void {
    if (confirm('Êtes-vous sûr de vouloir réinitialiser le formulaire?')) {
      this.postForm.reset({
        postType: 'question',
        forumId: '1',
        isAnonymous: false
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
}
