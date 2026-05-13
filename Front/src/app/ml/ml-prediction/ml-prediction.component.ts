import { Component } from '@angular/core';
import { MLService, PredictionResult, RecommendedItem } from '../../services/ml.service';

@Component({
  selector: 'app-ml-prediction',
  templateUrl: './ml-prediction.component.html',
  styleUrls: ['./ml-prediction.component.css']
})
export class MLPredictionComponent {
  postId: number | null = null;
  transplantId: number | null = null;
  survivalTransplantId: number | null = null;
  creatinineTransplantId: number | null = null;

  popularityResult: PredictionResult | null = null;
  engagementResult: PredictionResult | null = null;
  trendResult: PredictionResult | null = null;
  survivalResult: PredictionResult | null = null;
  rejectionResult: PredictionResult | null = null;
  creatinineResult: PredictionResult | null = null;
  readmissionResult: PredictionResult | null = null;

  trendingPosts: RecommendedItem[] | null = null;
  loading = false;
  error: string | null = null;

  constructor(private mlService: MLService) {}

  loadPopularity(): void {
    if (!this.postId) return;
    this.loading = true;
    this.error = null;
    this.mlService.predictPostPopularity(this.postId).subscribe({
      next: (data) => { this.popularityResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur prdiction popularit'; this.loading = false; }
    });
  }

  loadEngagement(): void {
    if (!this.postId) return;
    this.loading = true;
    this.error = null;
    this.mlService.predictPostEngagement(this.postId).subscribe({
      next: (data) => { this.engagementResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur prdiction engagement'; this.loading = false; }
    });
  }

  loadTrend(): void {
    if (!this.postId) return;
    this.loading = true;
    this.error = null;
    this.mlService.predictTrendPotential(this.postId).subscribe({
      next: (data) => { this.trendResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur prdiction tendance'; this.loading = false; }
    });
  }

  loadSurvival(): void {
    if (!this.survivalTransplantId) return;
    this.loading = true;
    this.error = null;
    this.mlService.predictGraftSurvival(this.survivalTransplantId).subscribe({
      next: (data) => { this.survivalResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur prdiction survie'; this.loading = false; }
    });
  }

  loadRejectionRisk(): void {
    if (!this.transplantId) return;
    this.loading = true;
    this.error = null;
    this.mlService.predictRejectionRisk(this.transplantId).subscribe({
      next: (data) => { this.rejectionResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur prdiction rejet'; this.loading = false; }
    });
  }

  loadCreatinineTrend(): void {
    if (!this.creatinineTransplantId) return;
    this.loading = true;
    this.error = null;
    this.mlService.predictCreatinineTrend(this.creatinineTransplantId).subscribe({
      next: (data) => { this.creatinineResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur prdiction cratinine'; this.loading = false; }
    });
  }

  loadReadmissionRisk(): void {
    if (!this.transplantId) return;
    this.loading = true;
    this.error = null;
    this.mlService.predictReadmissionRisk(this.transplantId).subscribe({
      next: (data) => { this.readmissionResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur prdiction rhospitalisation'; this.loading = false; }
    });
  }

  loadTrendingPosts(): void {
    this.loading = true;
    this.error = null;
    this.mlService.getTrendingPosts(5).subscribe({
      next: (data) => { this.trendingPosts = data; this.loading = false; },
      error: () => { this.error = 'Erreur chargement tendances'; this.loading = false; }
    });
  }

  getDetailValue(details: any, key: string): any {
    return details ? details[key] : null;
  }

  getRiskLevel(value: number): string {
    if (value > 0.5) return 'high';
    if (value > 0.2) return 'medium';
    return 'low';
  }
}
