import { Component, OnInit } from '@angular/core';
import { MLService, ClusterResult, RecommendedItem } from '../../services/ml.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-ml-dashboard',
  templateUrl: './ml-dashboard.component.html',
  styleUrls: ['./ml-dashboard.component.css']
})
export class MLDashboardComponent implements OnInit {
  forumClusters: { [key: number]: ClusterResult } | null = null;
  kidneyClusters: { [key: number]: ClusterResult } | null = null;
  trendingPosts: RecommendedItem[] | null = null;
  loadingForumClusters = false;
  loadingKidneyClusters = false;
  loadingTrending = false;
  errorForumClusters: string | null = null;
  errorKidneyClusters: string | null = null;
  errorTrending: string | null = null;

  constructor(private mlService: MLService) {}

  ngOnInit(): void {
    this.loadForumClusters();
    this.loadKidneyClusters();
    this.loadTrendingPosts();
  }

  loadForumClusters(): void {
    this.loadingForumClusters = true;
    this.errorForumClusters = null;
    this.mlService.getForumClusters().pipe(
      catchError(err => {
        this.errorForumClusters = 'Erreur chargement clusters forum';
        console.error('Forum clusters error:', err);
        return of(null);
      })
    ).subscribe(data => {
      this.forumClusters = data;
      this.loadingForumClusters = false;
    });
  }

  loadKidneyClusters(): void {
    this.loadingKidneyClusters = true;
    this.errorKidneyClusters = null;
    this.mlService.getTransplantClusters().pipe(
      catchError(err => {
        this.errorKidneyClusters = 'Erreur chargement clusters greffe';
        console.error('Kidney clusters error:', err);
        return of(null);
      })
    ).subscribe(data => {
      this.kidneyClusters = data;
      this.loadingKidneyClusters = false;
    });
  }

  loadTrendingPosts(): void {
    this.loadingTrending = true;
    this.errorTrending = null;
    this.mlService.getTrendingPosts(5).pipe(
      catchError(err => {
        this.errorTrending = 'Erreur chargement posts tendance';
        console.error('Trending posts error:', err);
        return of(null);
      })
    ).subscribe(data => {
      this.trendingPosts = data;
      this.loadingTrending = false;
    });
  }

  getClusterKeys(clusters: any): number[] {
    return clusters ? Object.keys(clusters).map(Number) : [];
  }

  getClusterEntries(clusters: { [key: number]: ClusterResult } | null): ClusterResult[] {
    return clusters ? Object.values(clusters) : [];
  }
}
