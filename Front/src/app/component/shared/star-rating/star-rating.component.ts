import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostRatingService } from '../../../services/post-rating.service';
import { AuthService } from '../../../services/auth.service';
import { PostRatingSummary } from '../../../models/rating.model';

@Component({
  selector: 'app-star-rating',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './star-rating.component.html',
  styleUrls: ['./star-rating.component.css']
})
export class StarRatingComponent implements OnInit, OnChanges {
  @Input() postId!: number;
  @Input() currentRating: number = 0;
  @Input() averageRating: number = 0;
  @Input() totalRatings: number = 0;
  @Input() readonly: boolean = false;
  @Input() showStats: boolean = true;
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() orientation: 'horizontal' | 'vertical' = 'horizontal';
  @Output() ratingChange = new EventEmitter<number>();
  @Output() ratingSubmitted = new EventEmitter<number>();

  hoverRating: number = 0;
  isSubmitting: boolean = false;
  ratingSummary: PostRatingSummary | null = null;

  constructor(
    private ratingService: PostRatingService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadRatingSummary();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['postId'] && !changes['postId'].firstChange) {
      this.loadRatingSummary();
    }
  }

  loadRatingSummary(): void {
    if (this.postId) {
      this.ratingService.getPostRatingSummary(this.postId).subscribe({
        next: (summary: any) => {
          this.ratingSummary = summary;
          this.averageRating = summary.averageRating;
          this.totalRatings = summary.totalRatings;
          this.loadUserRating();
        },
        error: () => {
          this.ratingSummary = {
            postId: this.postId,
            averageRating: 0,
            totalRatings: 0,
            ratingDistribution: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 }
          };
        }
      });
    }
  }

  loadUserRating(): void {
    if (this.postId && this.authService.isAuthenticated()) {
      this.ratingService.getUserRating(this.postId).subscribe({
        next: (userRating: any) => {
          if (userRating) {
            this.currentRating = userRating.rating;
            this.ratingChange.emit(this.currentRating);
          }
        },
        error: () => {}
      });
    }
  }

  onStarClick(rating: number): void {
    if (this.readonly || this.isSubmitting) return;
    if (!this.authService.isAuthenticated()) return;
    this.isSubmitting = true;
    this.currentRating = rating;
    this.ratingChange.emit(rating);
    this.ratingService.ratePost(this.postId, rating).subscribe({
      next: () => {
        this.ratingSubmitted.emit(rating);
        this.loadRatingSummary();
        this.isSubmitting = false;
      },
      error: () => { this.isSubmitting = false; }
    });
  }

  onStarHover(rating: number): void {
    if (!this.readonly) this.hoverRating = rating;
  }

  onMouseLeave(): void { this.hoverRating = 0; }

  isStarFilled(starIndex: number): boolean {
    return starIndex <= (this.hoverRating || this.currentRating);
  }

  getStarsArray(): number[] { return [1, 2, 3, 4, 5]; }

  getRatingPercentage(star: number): number {
    if (!this.ratingSummary || this.totalRatings === 0) return 0;
    const dist = this.ratingSummary.ratingDistribution;
    const count = dist[star as 1|2|3|4|5] || 0;
    return (count / this.totalRatings) * 100;
  }

  resetRating(): void {
    if (this.readonly || this.isSubmitting) return;
    if (!this.authService.isAuthenticated()) return;
    this.currentRating = 0;
    this.ratingChange.emit(0);
    this.ratingService.deleteRating(this.postId).subscribe({
      next: () => this.loadRatingSummary(),
      error: () => {}
    });
  }
}
