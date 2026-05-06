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
import { ForumRoutingService } from '../../../../../services/forum-routing.service';
import { ForumService } from '../../../../../services/forum.service';
import { environment } from '../../../../../../environments/environment';

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
  selectedVideoFile: File | null = null;

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
  get videoUrl() { return this.postForm.get('videoUrl'); }

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
      tags: [''],
      videoUrl: ['', [Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    // Initialisation
  }

  onVideoFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedVideoFile = input.files[0];
    } else {
      this.selectedVideoFile = null;
    }
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      this.showErrorMessage('Veuillez corriger les erreurs dans le formulaire');
      return;
    }

    this.isSubmitting = true;
    this.isLoading = true;
    
    const finalizePostCreation = (videoUrl?: string | null) => {
      const post = {
        subject: this.postForm.value.title,
        title: this.postForm.value.title,
        content: this.postForm.value.content,
        isAnonymous: !!this.postForm.value.isAnonymous,
        picture: null,
        videoUrl: videoUrl || this.postForm.value.videoUrl || null,
        datePost: new Date().toISOString()
      };

      this.forumService.createPost(post).subscribe({
        next: (newPost: any) => {
          this.isSubmitting = false;
          this.isLoading = false;
          
          // Sauvegarder l'ID du post créé pour l'utilisateur actuel
          const currentUserId = 'user_' + Math.random().toString(36).substr(2, 9);
          const userPosts = JSON.parse(localStorage.getItem('user_posts_' + currentUserId) || '[]');
          if (newPost.id) {
            userPosts.push(newPost.id);
            localStorage.setItem('user_posts_' + currentUserId, JSON.stringify(userPosts));
          }
          
          this.showSuccessMessage('Post créé avec succès!');
          setTimeout(() => this.router.navigate(['/forum']), 500);
        },
        error: (err) => {
          console.error('Erreur lors de la création du post:', err);
          this.isSubmitting = false;
          this.isLoading = false;
          this.showErrorMessage('Erreur lors de la création du post. Veuillez réessayer.');
        }
      });
    };

    if (this.selectedVideoFile) {
      // Upload vers le backend, puis on sauvegarde l'URL interne du fichier (dans la plateforme)
      this.forumService.uploadVideo(this.selectedVideoFile).subscribe({
        next: (res) => {
          const absoluteUrl = res.url.startsWith('http') ? res.url : `${environment.apiUrl}${res.url}`;
          finalizePostCreation(absoluteUrl);
        },
        error: (err) => {
          console.error('Erreur lors de l\'upload vidéo:', err);
          this.showErrorMessage('Erreur lors de l\'upload vidéo. Le post sera créé sans vidéo.');
          finalizePostCreation(null);
        }
      });
    } else {
      finalizePostCreation();
    }
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
