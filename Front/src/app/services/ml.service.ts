import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ClusterResult {
  clusterId: number;
  itemIds: number[];
  topTerms: string[];
  size: number;
  centroid: { [key: string]: number };
}

export interface ClassificationResult {
  predictedClass: string;
  classProbabilities: { [key: string]: number };
  confidence: number;
  details?: string;
}

export interface PredictionResult {
  predictionType: string;
  predictedValue: number;
  confidence: number;
  unit: string;
  details?: { [key: string]: any };
}

export interface RecommendedItem {
  id: number;
  name: string;
  score: number;
  reason?: string;
}

export interface RecommendationResult {
  recommendationType: string;
  targetItemId?: number;
  targetItemName?: string;
  relevanceScore: number;
  reason?: string;
  items: RecommendedItem[];
  metadata?: { [key: string]: any };
}

@Injectable({
  providedIn: 'root'
})
export class MLService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ========== FORUM AI ==========

  getForumClusters(): Observable<{ [key: number]: ClusterResult }> {
    return this.http.get<{ [key: number]: ClusterResult }>(`${this.apiUrl}/api/forum/ai/clusters`);
  }

  getPostCluster(postId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/forum/ai/posts/${postId}/cluster`);
  }

  classifyText(title: string, content: string): Observable<ClassificationResult> {
    return this.http.post<ClassificationResult>(`${this.apiUrl}/api/forum/ai/classify?title=${encodeURIComponent(title)}&content=${encodeURIComponent(content)}`, {});
  }

  classifyPost(postId: number): Observable<ClassificationResult> {
    return this.http.get<ClassificationResult>(`${this.apiUrl}/api/forum/ai/posts/${postId}/classify`);
  }

  predictPostPopularity(postId: number): Observable<PredictionResult> {
    return this.http.get<PredictionResult>(`${this.apiUrl}/api/forum/ai/posts/${postId}/predict-popularity`);
  }

  predictPostEngagement(postId: number): Observable<PredictionResult> {
    return this.http.get<PredictionResult>(`${this.apiUrl}/api/forum/ai/posts/${postId}/predict-engagement`);
  }

  predictTrendPotential(postId: number): Observable<PredictionResult> {
    return this.http.get<PredictionResult>(`${this.apiUrl}/api/forum/ai/posts/${postId}/predict-trend`);
  }

  recommendPostsForUser(userId: number, limit: number = 5): Observable<RecommendedItem[]> {
    return this.http.get<RecommendedItem[]>(`${this.apiUrl}/api/forum/ai/recommendations/user/${userId}?limit=${limit}`);
  }

  getSimilarPosts(postId: number, limit: number = 5): Observable<RecommendedItem[]> {
    return this.http.get<RecommendedItem[]>(`${this.apiUrl}/api/forum/ai/posts/${postId}/similar?limit=${limit}`);
  }

  getTrendingPosts(limit: number = 5): Observable<RecommendedItem[]> {
    return this.http.get<RecommendedItem[]>(`${this.apiUrl}/api/forum/ai/trending?limit=${limit}`);
  }

  // ========== KIDNEY TRANSPLANT AI ==========

  getTransplantClusters(): Observable<{ [key: number]: ClusterResult }> {
    return this.http.get<{ [key: number]: ClusterResult }>(`${this.apiUrl}/api/kidney-transplants/ai/clusters`);
  }

  getTransplantCluster(transplantId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/cluster`);
  }

  classifyTransplantOutcome(transplantId: number): Observable<ClassificationResult> {
    return this.http.get<ClassificationResult>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/classify-outcome`);
  }

  classifyTransplantRisk(transplantId: number): Observable<ClassificationResult> {
    return this.http.get<ClassificationResult>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/classify-risk`);
  }

  classifyComplicationRisk(transplantId: number): Observable<ClassificationResult> {
    return this.http.get<ClassificationResult>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/classify-complication`);
  }

  predictGraftSurvival(transplantId: number): Observable<PredictionResult> {
    return this.http.get<PredictionResult>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/predict-survival`);
  }

  predictRejectionRisk(transplantId: number): Observable<PredictionResult> {
    return this.http.get<PredictionResult>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/predict-rejection`);
  }

  predictCreatinineTrend(transplantId: number): Observable<PredictionResult> {
    return this.http.get<PredictionResult>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/predict-creatinine`);
  }

  predictReadmissionRisk(transplantId: number): Observable<PredictionResult> {
    return this.http.get<PredictionResult>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/predict-readmission`);
  }

  recommendFollowUp(transplantId: number): Observable<RecommendedItem[]> {
    return this.http.get<RecommendedItem[]>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/recommend-followup`);
  }

  recommendImmunosuppression(transplantId: number): Observable<RecommendedItem[]> {
    return this.http.get<RecommendedItem[]>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/recommend-immunosuppression`);
  }

  recommendLifestyle(transplantId: number): Observable<RecommendedItem[]> {
    return this.http.get<RecommendedItem[]>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/recommend-lifestyle`);
  }

  findSimilarTransplants(transplantId: number, limit: number = 5): Observable<RecommendedItem[]> {
    return this.http.get<RecommendedItem[]>(`${this.apiUrl}/api/kidney-transplants/ai/transplants/${transplantId}/similar?limit=${limit}`);
  }
}
