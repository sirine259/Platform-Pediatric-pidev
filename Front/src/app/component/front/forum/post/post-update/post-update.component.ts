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
import { environment } from '../../../../environments/environment';

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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.postId) {
      this.loadPost();
    }
  }

  loadPost(): void {
    this.isLoading = true;
    this.http.get(`${environment.apiUrl}/api/forum/posts/${this.postId}`).subscribe({
      next: (data: any) => {
        this.post = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement du post:', error);
        this.isLoading = false;
        alert('Erreur lors du chargement du post');
      }
    });
  }

  onSubmit(): void {
    if (!this.post.title || !this.post.content) {
      alert('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isSubmitting = true;
    
    const updatedPost = {
      title: this.post.title,
      content: this.post.content,
      postType: this.post.postType || 'FORUM'
    };

    this.http.put(`${environment.apiUrl}/api/forum/posts/${this.postId}`, updatedPost).subscribe({
      next: (data: any) => {
        this.isSubmitting = false;
        alert('Post mis à jour avec succès!');
        this.router.navigate(['/forum/post-detail', this.postId]);
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Erreur lors de la mise à jour:', error);
        alert('Erreur lors de la mise à jour du post');
      }
    });
  }

  onImageUpload(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const formData = new FormData();
      formData.append('image', file);
      
      this.http.post(`${environment.apiUrl}/api/forum/posts/${this.postId}/upload`, formData).subscribe({
        next: (response: any) => {
          this.post.picture = response.filename;
          alert('Image uploadée avec succès!');
        },
        error: (error) => {
          console.error('Erreur lors de l\'upload:', error);
          alert('Erreur lors de l\'upload de l\'image');
        }
      });
    }
  }

  removeImage(): void {
    if (confirm('Voulez-vous vraiment supprimer cette image?')) {
      this.post.picture = null;
    }
  }

  cancel(): void {
    this.router.navigate(['/forum/post-detail', this.postId]);
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
