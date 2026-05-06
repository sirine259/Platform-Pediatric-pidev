import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ForumService } from '../../../../../services/forum.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-post-update',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule],
  templateUrl: './post-update.component.html',
  styleUrls: ['./post-update.component.css']
})
export class PostUpdateComponent implements OnInit {
  post: any = null;
  postId: number = 0;
  isLoading = false;
  isSubmitting = false;
  apiUrl = environment.apiUrl;
  selectedFile: File | null = null;
  selectedVideoFile: File | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private forumService: ForumService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.postId) {
      this.loadPost();
    }
  }

  loadPost(): void {
    this.isLoading = true;
    this.forumService.getPostById(this.postId).subscribe({
      next: (data: any) => {
        if (!data) {
          alert('Post non trouvé');
          this.router.navigate(['/forum']);
          return;
        }
        this.post = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur lors du chargement du post:', error);
        this.isLoading = false;
        alert('Erreur lors du chargement du post');
        this.router.navigate(['/forum']);
      }
    });
  }

  onFileSelected(event: any): void {
    if (!this.post) {
      alert('Le post n\'est pas encore chargé. Veuillez attendre.');
      return;
    }
    
    const file = event.target.files[0];
    if (file) {
      // Vérifier le type de fichier
      if (file.type.startsWith('image/')) {
        this.selectedFile = file;
      } else if (file.type.startsWith('video/')) {
        this.selectedVideoFile = file;
      } else {
        alert('Type de fichier non supporté. Veuillez sélectionner une image ou une vidéo.');
        return;
      }

      // Créer une URL locale pour prévisualiser
      const reader = new FileReader();
      reader.onload = (e: any) => {
        if (file.type.startsWith('image/')) {
          this.post.picture = e.target.result; // Base64 pour affichage immédiat
        } else if (file.type.startsWith('video/')) {
          this.post.videoUrl = e.target.result;
        }
        this.cdr.detectChanges();
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage(): void {
    if (confirm('Voulez-vous vraiment supprimer cette image?')) {
      this.post.picture = null;
      this.selectedFile = null;
    }
  }

  removeVideo(): void {
    if (confirm('Voulez-vous vraiment supprimer cette vidéo?')) {
      this.post.videoUrl = null;
      this.selectedVideoFile = null;
    }
  }

  onSubmit(): void {
    if (!this.post) {
      alert('Erreur: Post non chargé');
      return;
    }
    
    const title = this.post.title || this.post.subject || '';
    const content = this.post.content || '';
    
    if (!title.trim() || !content.trim()) {
      alert('Veuillez remplir tous les champs obligatoires (titre et contenu)');
      return;
    }

    this.isSubmitting = true;
    
    const updatedPost: any = {
      ...this.post,
      title: title,
      subject: title,
      content: content,
      picture: this.post.picture || null,
      videoUrl: this.post.videoUrl || null,
      datePost: this.post.datePost || new Date().toISOString()
    };

    this.forumService.updatePostLocal(this.postId, updatedPost).subscribe({
      next: () => {
        this.isSubmitting = false;
        alert('Post mis à jour avec succès!');
        this.router.navigate(['/forum']);
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Erreur lors de la mise à jour:', error);
        alert('Erreur lors de la mise à jour du post');
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/forum']);
  }

  goBack(): void {
    this.router.navigate(['/forum']);
  }

  getPostTypeLabel(postType: string): string {
    switch(postType) {
      case 'FORUM': return 'Forum';
      case 'MEDICAL_UPDATE': return 'Mise à jour médicale';
      case 'FOLLOW_UP': return 'Suivi';
      case 'ANNOUNCEMENT': return 'Annonce';
      default: return 'Autre';
    }
  }

  getPostTypeColor(postType: string): string {
    switch(postType) {
      case 'FORUM': return 'primary';
      case 'MEDICAL_UPDATE': return 'success';
      case 'FOLLOW_UP': return 'warning';
      case 'ANNOUNCEMENT': return 'info';
      default: return 'secondary';
    }
  }
}
