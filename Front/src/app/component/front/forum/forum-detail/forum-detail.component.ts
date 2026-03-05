import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-forum-detail',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, DatePipe],
  templateUrl: './forum-detail.component.html',
  styleUrls: ['./forum-detail.component.css']
})
export class ForumDetailComponent implements OnInit {
  forum: any = null;
  posts: any[] = [];
  isLoading = false;
  forumId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.forumId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.forumId) {
      this.loadForumDetails();
      this.loadForumPosts();
    }
  }

  loadForumDetails(): void {
    this.isLoading = true;
    this.http.get(`http://localhost:8080/api/forums/${this.forumId}`).subscribe({
      next: (data: any) => {
        this.forum = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement du forum:', error);
        this.isLoading = false;
      }
    });
  }

  loadForumPosts(): void {
    this.http.get(`http://localhost:8080/api/forum/posts/forum/${this.forumId}`).subscribe({
      next: (data: any) => {
        this.posts = data.reverse(); // Plus récents d'abord
      },
      error: (error) => {
        console.error('Erreur lors du chargement des posts:', error);
      }
    });
  }

  createPost(): void {
    this.router.navigate(['/forum/post-create', { forumId: this.forumId }]);
  }

  viewPostDetails(postId: number): void {
    this.router.navigate(['/forum/post-detail', postId]);
  }

  updatePost(postId: number): void {
    this.router.navigate(['/forum/post-update', postId]);
  }

  deletePost(postId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce post?')) {
      this.http.delete(`http://localhost:8080/api/forum/posts/${postId}`).subscribe({
        next: () => {
          alert('Post supprimé avec succès!');
          this.loadForumPosts(); // Recharger la liste
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur lors de la suppression');
        }
      });
    }
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

  goBack(): void {
    this.router.navigate(['/forum']);
  }
}
