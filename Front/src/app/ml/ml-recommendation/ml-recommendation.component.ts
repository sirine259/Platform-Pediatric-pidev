import { Component } from '@angular/core';
import { MLService, RecommendedItem } from '../../services/ml.service';

@Component({
  selector: 'app-ml-recommendation',
  templateUrl: './ml-recommendation.component.html',
  styleUrls: ['./ml-recommendation.component.css']
})
export class MLRecommendationComponent {
  userId: number | null = null;
  postId: number | null = null;
  transplantId: number | null = null;

  postsResult: RecommendedItem[] | null = null;
  similarResult: RecommendedItem[] | null = null;
  followUpResult: RecommendedItem[] | null = null;
  immunosuppressionResult: RecommendedItem[] | null = null;
  lifestyleResult: RecommendedItem[] | null = null;
  similarTransplantResult: RecommendedItem[] | null = null;

  loading = false;
  error: string | null = null;

  constructor(private mlService: MLService) {}

  recommendPosts(): void {
    if (!this.userId) return;
    this.loading = true;
    this.error = null;
    this.mlService.recommendPostsForUser(this.userId).subscribe({
      next: (data) => { this.postsResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur recommandation posts'; this.loading = false; }
    });
  }

  recommendSimilar(): void {
    if (!this.postId) return;
    this.loading = true;
    this.error = null;
    this.mlService.getSimilarPosts(this.postId).subscribe({
      next: (data) => { this.similarResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur recommandation similaire'; this.loading = false; }
    });
  }

  recommendFollowUp(): void {
    if (!this.transplantId) return;
    this.loading = true;
    this.error = null;
    this.mlService.recommendFollowUp(this.transplantId).subscribe({
      next: (data) => { this.followUpResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur recommandation suivi'; this.loading = false; }
    });
  }

  recommendImmunosuppression(): void {
    if (!this.transplantId) return;
    this.loading = true;
    this.error = null;
    this.mlService.recommendImmunosuppression(this.transplantId).subscribe({
      next: (data) => { this.immunosuppressionResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur recommandation traitement'; this.loading = false; }
    });
  }

  recommendLifestyle(): void {
    if (!this.transplantId) return;
    this.loading = true;
    this.error = null;
    this.mlService.recommendLifestyle(this.transplantId).subscribe({
      next: (data) => { this.lifestyleResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur recommandation lifestyle'; this.loading = false; }
    });
  }

  findSimilarTransplants(): void {
    if (!this.transplantId) return;
    this.loading = true;
    this.error = null;
    this.mlService.findSimilarTransplants(this.transplantId).subscribe({
      next: (data) => { this.similarTransplantResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur recherche similaire'; this.loading = false; }
    });
  }

  getScoreClass(score: number): string {
    if (score > 0.6) return 'high';
    if (score > 0.3) return 'medium';
    return 'low';
  }
}
