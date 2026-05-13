import { Component, OnInit } from '@angular/core';
import { MLService, ClusterResult } from '../../services/ml.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-ml-clustering',
  templateUrl: './ml-clustering.component.html',
  styleUrls: ['./ml-clustering.component.css']
})
export class MLClusteringComponent implements OnInit {
  forumClusters: { [key: number]: ClusterResult } | null = null;
  kidneyClusters: { [key: number]: ClusterResult } | null = null;
  activeTab: 'forum' | 'kidney' = 'forum';
  loading = false;
  error: string | null = null;

  constructor(private mlService: MLService) {}

  ngOnInit(): void {
    this.loadClustering();
  }

  loadClustering(): void {
    this.loading = true;
    this.error = null;
    forkJoin({
      forum: this.mlService.getForumClusters(),
      kidney: this.mlService.getTransplantClusters()
    }).subscribe({
      next: (data) => {
        this.forumClusters = data.forum;
        this.kidneyClusters = data.kidney;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du clustering';
        this.loading = false;
        console.error(err);
      }
    });
  }

  getClusterEntries(clusters: any): ClusterResult[] {
    return clusters ? Object.values(clusters) : [];
  }

  getClusterKeys(clusters: any): number[] {
    return clusters ? Object.keys(clusters).map(Number) : [];
  }
}
