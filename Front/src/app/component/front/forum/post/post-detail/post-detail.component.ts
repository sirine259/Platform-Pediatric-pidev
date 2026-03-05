import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ForumRoutingService } from '../../../../../services/forum-routing.service';

@Component({
  selector: 'app-post-detail',
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule]
})
export class PostDetailComponent implements OnInit {
  post: any = null;
  comments: any[] = [];
  isLoading = false;
  postId: number = 0;
  newComment: string = '';
  isSubmittingComment = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private forumService: ForumRoutingService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.postId) {
      this.loadPostDetails();
      this.loadComments();
    }
  }

  loadPostDetails(): void {
    this.isLoading = true;
    this.forumService.getPostById(this.postId).subscribe({
      next: (data: any) => {
        this.post = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement du post:', error);
        this.isLoading = false;
      }
    });
  }

  loadComments(): void {
    this.forumService.getCommentsByPostId(this.postId).subscribe({
      next: (data: any) => {
        this.comments = data.reverse();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des commentaires:', error);
      }
    });
  }

  addComment(): void {
    if (!this.newComment.trim()) {
      alert('Veuillez écrire un commentaire');
      return;
    }

    this.isSubmittingComment = true;
    
    const comment = {
      content: this.newComment,
      postId: this.postId
    };

    this.forumService.createComment(comment).subscribe({
      next: () => {
        this.newComment = '';
        this.isSubmittingComment = false;
        this.loadComments();
        alert('Commentaire ajouté avec succès!');
      },
      error: (error) => {
        this.isSubmittingComment = false;
        console.error('Erreur lors de l\'ajout du commentaire:', error);
        alert('Erreur lors de l\'ajout du commentaire');
      }
    });
  }

  updatePost(): void {
    this.router.navigate(['/forum/post-update', this.postId]);
  }

  deletePost(): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce post?')) {
      this.forumService.deletePost(this.postId).subscribe({
        next: () => {
          alert('Post supprimé avec succès!');
          this.router.navigate(['/forum']);
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur lors de la suppression');
        }
      });
    }
  }

  likePost(): void {
    this.http.post(`http://localhost:8080/api/forum/posts/${this.postId}/like`, {}).subscribe({
      next: () => {
        this.post.likeCount = (this.post.likeCount || 0) + 1;
      },
      error: (error) => {
        console.error('Erreur lors du like:', error);
      }
    });
  }

  deleteComment(commentId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce commentaire?')) {
      this.forumService.deleteComment(commentId).subscribe({
        next: () => {
          this.comments = this.comments.filter(c => c.id !== commentId);
          alert('Commentaire supprimé avec succès!');
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur lors de la suppression');
        }
      });
    }
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

  getTimeAgo(date: string): string {
    const now = new Date();
    const postDate = new Date(date);
    const diffTime = Math.abs(now.getTime() - postDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 1) return 'Hier';
    if (diffDays < 7) return `Il y a ${diffDays} jours`;
    if (diffDays < 30) return `Il y a ${Math.floor(diffDays / 7)} semaines`;
    if (diffDays < 365) return `Il y a ${Math.floor(diffDays / 30)} mois`;
    return `Il y a ${Math.floor(diffDays / 365)} ans`;
  }

  getPostTypeIcon(postType: string): string {
    switch(postType) {
      case 'FORUM': return 'fa-comments';
      case 'MEDICAL_UPDATE': return 'fa-heartbeat';
      case 'FOLLOW_UP': return 'fa-calendar-check';
      case 'ANNOUNCEMENT': return 'fa-bullhorn';
      default: return 'fa-file-alt';
    }
  }
}
