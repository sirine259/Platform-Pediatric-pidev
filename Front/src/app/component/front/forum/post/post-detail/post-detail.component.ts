import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { SafeUrlPipe } from '../../../../../shared/safe-url.pipe';
import { ForumService } from '../../../../../services/forum.service';
import { PostRatingService } from '../../../../../services/post-rating.service';
import { AuthService } from '../../../../../services/auth.service';
import { environment } from '../../../../../../environments/environment';

@Component({
  selector: 'app-post-detail',
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule, SafeUrlPipe]
})
export class PostDetailComponent implements OnInit {
  post: any = null;
  comments: any[] = [];
  isLoading = false;
  postId: number = 0;
  apiUrl = environment.apiUrl;
  newComment: string = '';
  isSubmittingComment = false;
  
  // Rating properties
  averageRating: number = 0;
  totalRatings: number = 0;
  userRating: number = 0;
  userId: number | null = null;
  ratingDistribution: { [key: number]: number } = {1: 0, 2: 0, 3: 0, 4: 0, 5: 0};
  animatedPercentages: { [key: number]: number } = {1: 0, 2: 0, 3: 0, 4: 0, 5: 0};
  isRatingAnimating = false;
  stars: number[] = [1, 2, 3, 4, 5];

  isYouTube(url: string | undefined | null): boolean {
    if (!url) {
      return false;
    }
    return url.includes('youtube.com') || url.includes('youtu.be');
  }

  getYouTubeEmbedUrl(url: string | undefined | null): string {
    if (!url) {
      return '';
    }
    let videoId = '';
    if (url.includes('youtu.be/')) {
      const parts = url.split('youtu.be/');
      videoId = parts[1].split(/[?&]/)[0];
    } else if (url.includes('watch?v=')) {
      const params = new URL(url).searchParams;
      videoId = params.get('v') || '';
    }
    return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
  }

  resolveVideoUrl(url: string | undefined | null): string {
    if (!url) {
      return '';
    }
    // Si le backend renvoie une URL relative (/api/...), on la rend absolue vers l'API
    if (url.startsWith('/api/')) {
      return `${environment.apiUrl}${url}`;
    }
    return url;
  }

  isDirectVideo(url: string | undefined | null): boolean {
    if (!url) return false;
    // Anything served by our backend videos endpoint should be played with <video>
    if (url.includes('/videos/')) return true;
    // External direct video files
    const lowered = url.toLowerCase();
    return lowered.endsWith('.mp4') || lowered.endsWith('.webm') || lowered.endsWith('.ogg');
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private forumService: ForumService,
    private http: HttpClient,
    private ratingService: PostRatingService,
    private authService: AuthService
  ) {}

ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    this.userId = this.resolveCurrentUserId();
    if (this.postId) {
      this.loadPostDetails();
      this.loadComments();
    }
  }

  private resolveCurrentUserId(): number | null {
    const currentUser = this.authService.getCurrentUser();
    return currentUser?.id ?? null;
  }

  loadPostDetails(): void {
    this.isLoading = true;
    this.forumService.getPostById(this.postId).subscribe({
      next: (data: any) => {
        this.post = data;
        if (!this.post) {
          this.isLoading = false;
          return;
        }
        this.loadPostRating();
        this.isLoading = false;
        this.loadComments();
      },
      error: (error) => {
        console.error('Erreur lors du chargement du post:', error);
        this.isLoading = false;
      }
    });
  }

  loadPostRating(): void {
    this.ratingService.getPostRatingSummary(this.postId).subscribe({
      next: (summary: any) => {
        this.averageRating = Number(summary.averageRating || 0);
        this.totalRatings = Number(summary.totalRatings || 0);
        this.ratingDistribution = summary.ratingDistribution || {1: 0, 2: 0, 3: 0, 4: 0, 5: 0};
        this.animateRatingBars();
      }
    });

    if (this.userId !== null) {
      this.ratingService.getUserRating(this.postId).subscribe({
        next: (rating: any) => {
          this.userRating = rating?.rating || 0;
        }
      });
    } else {
      this.userRating = 0;
    }
  }

  setRating(rating: number): void {
    if (this.userId === null) {
      alert('Veuillez vous connecter pour noter ce post.');
      return;
    }
    this.ratingService.ratePost(this.postId, rating).subscribe({
      next: () => {
        this.userRating = rating;
        this.loadPostRating();
      },
      error: (err) => {
        console.error('Error setting rating:', err);
      }
    });
  }

  getRatingPercentage(star: number): number {
    const count = this.ratingDistribution[star] || 0;
    return this.totalRatings > 0 ? (count / this.totalRatings) * 100 : 0;
  }

  getRatingCount(star: number): number {
    return Number(this.ratingDistribution[star] || 0);
  }

  getAnimatedRatingPercentage(star: number): number {
    return Number(this.animatedPercentages[star] || 0);
  }

  private animateRatingBars(): void {
    const targets: { [key: number]: number } = {
      1: this.getRatingPercentage(1),
      2: this.getRatingPercentage(2),
      3: this.getRatingPercentage(3),
      4: this.getRatingPercentage(4),
      5: this.getRatingPercentage(5)
    };

    if (this.totalRatings === 0) {
      this.animatedPercentages = {1: 0, 2: 0, 3: 0, 4: 0, 5: 0};
      this.isRatingAnimating = false;
      return;
    }

    this.animatedPercentages = {1: 0, 2: 0, 3: 0, 4: 0, 5: 0};
    this.isRatingAnimating = true;
    const duration = 1800;
    const start = performance.now();

    const tick = (now: number) => {
      const progress = Math.min((now - start) / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);

      this.animatedPercentages = {
        1: targets[1] * eased,
        2: targets[2] * eased,
        3: targets[3] * eased,
        4: targets[4] * eased,
        5: targets[5] * eased
      };

      if (progress < 1) {
        requestAnimationFrame(tick);
      } else {
        this.isRatingAnimating = false;
      }
    };

    // Force one frame at 0%, then animate to target values.
    requestAnimationFrame(() => requestAnimationFrame(tick));
  }

  onRatingSubmitted(rating: number): void {
    this.userRating = rating;
    this.loadPostRating();
  }

  toggleRatingSection(): void {
  }

  loadComments(): void {
    if (this.post?.comments) {
      this.comments = [...this.post.comments].reverse();
    } else {
      this.comments = [];
    }
  }

  addComment(): void {
    if (!this.newComment.trim()) {
      alert('Veuillez Ã©crire un commentaire');
      return;
    }

    this.isSubmittingComment = true;
    this.forumService.addComment(this.postId, this.newComment).subscribe({
      next: (newComment: any) => {
        if (this.post) {
          this.post.comments = this.post.comments || [];
          this.post.comments.push(newComment);
        }
        this.newComment = '';
        this.isSubmittingComment = false;
        this.loadComments();
        alert('Commentaire ajoutÃ© avec succÃ¨s!');
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
    if (confirm('ÃŠtes-vous sÃ»r de vouloir supprimer ce post?')) {
      this.forumService.deletePost(this.postId).subscribe({
        next: () => {
          alert('Post supprimÃ© avec succÃ¨s!');
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
    this.http.post(`${environment.apiUrl}/api/forum/posts/${this.postId}/like`, {}).subscribe({
      next: () => {
        this.post.likeCount = (this.post.likeCount || 0) + 1;
      },
      error: (error) => {
        console.error('Erreur lors du like:', error);
      }
    });
  }

  deleteComment(commentId: number): void {
    if (confirm('ÃŠtes-vous sÃ»r de vouloir supprimer ce commentaire?')) {
      this.forumService.deleteComment(this.postId, commentId).subscribe({
        next: () => {
          if (this.post) {
            this.post.comments = this.post.comments?.filter((c: any) => c.id !== commentId) || [];
          }
          this.loadComments();
          alert('Commentaire supprimÃ© avec succÃ¨s!');
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
      case 'MEDICAL_UPDATE': return 'Mise Ã  jour mÃ©dicale';
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

