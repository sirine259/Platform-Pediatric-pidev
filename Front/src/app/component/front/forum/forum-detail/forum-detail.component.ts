import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PostRatingService } from '../../../../services/post-rating.service';
import { AuthService } from '../../../../services/auth.service';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-forum-detail',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, MatPaginatorModule, MatProgressSpinnerModule, DatePipe],
  templateUrl: './forum-detail.component.html',
  styleUrls: ['./forum-detail.component.css']
})
export class ForumDetailComponent implements OnInit {
  forum: any = null;
  posts: any[] = [];
  isLoading = false;
  forumId: number = 0;
  apiUrl = environment.apiUrl;
  postRatings: { [postId: number]: { averageRating: number, totalRatings: number, ratingDistribution: { [key: number]: number } } } = {};
  userRatings: { [postId: number]: number } = {};
  expandedPostId: number | null = null;
  userRating: number = 0;
  userId: number = 0;
  stars: number[] = [1, 2, 3, 4, 5];
  
  pageIndex = 0;
  pageSize = 5;
  totalElements = 0;
  totalPages = 0;
  length = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private ratingService: PostRatingService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.forumId = Number(this.route.snapshot.paramMap.get('id'));
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.userId = currentUser.id || 0;
    }
    if (this.forumId) {
      this.loadForumDetails();
      this.loadForumPosts();
    }
  }

  loadForumDetails(): void {
    this.isLoading = true;
    this.http.get(`${this.apiUrl}/api/forums/${this.forumId}`).subscribe({
      next: (data: any) => {
        this.forum = data;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement du forum:', error);
        this.isLoading = false;
      }
    });
  }

  loadForumPosts(): void {
    this.isLoading = true;
    this.http.get<any>(`${this.apiUrl}/api/forum/posts/forum/${this.forumId}?page=${this.pageIndex}&size=${this.pageSize}&sortBy=createdAt&sortDir=DESC`).subscribe({
      next: (data: any) => {
        this.posts = data.content || [];
        this.totalElements = data.totalElements || 0;
        this.totalPages = data.totalPages || 0;
        this.length = data.totalElements || 0;
        this.isLoading = false;
        this.loadPostRatings();
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des posts:', error);
        this.isLoading = false;
      }
    });
  }
  
  loadPostRatings(): void {
    this.posts.forEach((post: any) => {
      const numericPostId = Number(post.id);
      this.ratingService.getPostRatingSummary(numericPostId).subscribe({
        next: (summary: any) => {
          this.postRatings[numericPostId] = {
            averageRating: summary.averageRating,
            totalRatings: summary.totalRatings,
            ratingDistribution: summary.ratingDistribution || {1: 0, 2: 0, 3: 0, 4: 0, 5: 0}
          };
        },
        error: (err: any) => console.error('Erreur chargement rating:', err)
      });
    });
  }

  togglePostRating(postId: number): void {
    if (this.expandedPostId === postId) {
      this.expandedPostId = null;
    } else {
      this.expandedPostId = postId;
      if (!this.userId) {
        this.userRating = 0;
        return;
      }
      this.ratingService.getUserRating(postId).subscribe({
        next: (userRating: any) => {
          if (userRating) {
            this.userRating = userRating.rating;
          } else {
            this.userRating = 0;
          }
        },
        error: () => this.userRating = 0
      });
    }
  }

  setRating(postId: number, rating: number): void {
    if (!this.userId) {
      alert('Veuillez vous connecter pour noter ce post.');
      return;
    }
    const numericPostId = Number(postId);
    this.ratingService.ratePost(numericPostId, rating).subscribe({
      next: () => {
        this.userRatings[numericPostId] = rating;
        this.userRating = rating;
        this.loadPostRatings();
        alert('Vote sauvegardé: ' + rating + ' étoiles');
      },
      error: () => {
        alert('Erreur lors de la sauvegarde du vote.');
      }
    });
  }

  getRatingPercentage(postId: number, star: number): number {
    const rating = this.postRatings[postId];
    if (!rating || !rating.ratingDistribution) return 0;
    const count = rating.ratingDistribution[star] || 0;
    return rating.totalRatings > 0 ? (count / rating.totalRatings) * 100 : 0;
  }

  getUserRating(postId: number): number {
    return this.userRating;
  }

  getUserRatingForPost(postId: number): number {
    return this.userRatings[Number(postId)] || 0;
  }
  
  getPostRating(postId: number): { averageRating: number, totalRatings: number } {
    return this.postRatings[Number(postId)] || { averageRating: 0, totalRatings: 0 };
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.length = event.length;
    this.loadForumPosts();
  }

  createPost(): void {
    this.router.navigate(['/forum/post-create', { forumId: this.forumId }]);
  }

  viewPostDetails(postId: number): void {
    this.router.navigate(['/forum/post', postId]);
  }

  updatePost(postId: number): void {
    this.router.navigate(['/forum/post-update', postId]);
  }

  deletePost(postId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce post?')) {
      this.http.delete(`${this.apiUrl}/api/forum/posts/${postId}`).subscribe({
        next: () => {
          alert('Post supprimé avec succès!');
          this.loadForumPosts();
        },
        error: (error: any) => {
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