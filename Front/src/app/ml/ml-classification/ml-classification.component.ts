import { Component } from '@angular/core';
import { MLService, ClassificationResult } from '../../services/ml.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-ml-classification',
  templateUrl: './ml-classification.component.html',
  styleUrls: ['./ml-classification.component.css']
})
export class MLClassificationComponent {
  postId: number | null = null;
  transplantId: number | null = null;
  contentTitle: string = '';
  contentText: string = '';

  postResult: ClassificationResult | null = null;
  transplantResult: ClassificationResult | null = null;
  textResult: ClassificationResult | null = null;
  riskResult: ClassificationResult | null = null;
  complicationResult: ClassificationResult | null = null;

  loading = false;
  error: string | null = null;
  activeForumTab: 'classify' | 'predict' = 'classify';

  constructor(private mlService: MLService) {}

  classifyPost(): void {
    if (!this.postId) return;
    this.loading = true;
    this.error = null;
    this.mlService.classifyPost(this.postId).subscribe({
      next: (data) => { this.postResult = data; this.loading = false; },
      error: () => { this.error = 'Erreur de classification du post'; this.loading = false; }
    });
  }

  classifyText(): void {
    this.loading = true;
    this.error = null;
    this.mlService.classifyText(this.contentTitle, this.contentText).subscribe({
      next: (data) => { this.textResult = data; this.loading = false; },
      error: () => { this.error = "Erreur de classification du texte"; this.loading = false; }
    });
  }

  classifyTransplant(): void {
    if (!this.transplantId) return;
    this.loading = true;
    this.error = null;
    forkJoin({
      outcome: this.mlService.classifyTransplantOutcome(this.transplantId),
      risk: this.mlService.classifyTransplantRisk(this.transplantId),
      complication: this.mlService.classifyComplicationRisk(this.transplantId)
    }).subscribe({
      next: (data) => {
        this.transplantResult = data.outcome;
        this.riskResult = data.risk;
        this.complicationResult = data.complication;
        this.loading = false;
      },
      error: () => { this.error = 'Erreur de classification de la greffe'; this.loading = false; }
    });
  }

  getConfidenceClass(confidence: number): string {
    if (confidence > 0.7) return 'high';
    if (confidence > 0.4) return 'medium';
    return 'low';
  }

  getProbabilitiesEntries(prob: any): { key: string; value: number }[] {
    return Object.keys(prob || {}).map(k => ({ key: k, value: prob[k] }));
  }
}
