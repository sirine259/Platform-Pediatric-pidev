import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Post, Comment, LikePost, VoteComment } from '../models/comment.model';
import { environment } from '../../environments/environment';

export interface ForumStatistics {
  totalPosts: number;
  totalComments: number;
  totalUsers: number;
  approvedPosts: number;
  pendingPosts: number;
  avgCommentsPerPost: number;
  mostActiveUsers: UserActivity[];
  mostPopularPosts: PostPopularity[];
  trendingTopics: TopicTrend[];
  voteStatistics: VoteStatistics;
  activityStats: ActivityStatistics;
}

export interface UserActivity {
  userId: number;
  userName: string;
  firstName: string;
  lastName: string;
  postCount: number;
  commentCount: number;
  totalActivity: number;
}

export interface PostPopularity {
  postId: number;
  subject: string;
  commentCount: number;
  likeType: LikePost;
  popularityScore: number;
}

export interface TopicTrend {
  topic: string;
  count: number;
}

export interface VoteStatistics {
  commentVotes: { [key: string]: number };
  postLikes: { [key: string]: number };
  commentReactions: { [key: string]: number };
  totalCommentVotes: number;
  totalPostLikes: number;
  totalCommentReactions: number;
  netScore: number;
  upVotePercentage: number;
  downVotePercentage: number;
}

export interface ActivityStatistics {
  postsToday: number;
  postsThisWeek: number;
  postsThisMonth: number;
  commentsToday: number;
  commentsThisWeek: number;
  commentsThisMonth: number;
}

@Injectable({
  providedIn: 'root'
})
export class ForumStatisticsService {
  private readonly API_BASE_URL = environment.apiUrl + '/api/forum-statistics';

  constructor(private http: HttpClient) {}

  // Obtenir les statistiques générales du forum
  getGeneralStatistics(): Observable<ForumStatistics> {
    return this.http.get<ForumStatistics>(`${this.API_BASE_URL}/general`).pipe(
      catchError(error => {
        console.error('Error getting general statistics:', error);
        return of(this.getMockStatistics());
      })
    );
  }

  // Obtenir le dashboard complet
  getDashboardStatistics(): Observable<ForumStatistics> {
    return this.http.get<ForumStatistics>(`${this.API_BASE_URL}/dashboard`).pipe(
      catchError(error => {
        console.error('Error getting dashboard statistics:', error);
        return of(this.getMockStatistics());
      })
    );
  }

  // Obtenir les utilisateurs les plus actifs
  getMostActiveUsers(): Observable<UserActivity[]> {
    return this.http.get<UserActivity[]>(`${this.API_BASE_URL}/most-active-users`).pipe(
      catchError(error => {
        console.error('Error getting most active users:', error);
        return of([]);
      })
    );
  }

  // Obtenir les posts les plus populaires
  getMostPopularPosts(): Observable<PostPopularity[]> {
    return this.http.get<PostPopularity[]>(`${this.API_BASE_URL}/most-popular-posts`).pipe(
      catchError(error => {
        console.error('Error getting most popular posts:', error);
        return of([]);
      })
    );
  }

  // Obtenir les sujets tendance
  getTrendingTopics(): Observable<TopicTrend[]> {
    return this.http.get<TopicTrend[]>(`${this.API_BASE_URL}/trending-topics`).pipe(
      catchError(error => {
        console.error('Error getting trending topics:', error);
        return of([]);
      })
    );
  }

  // Obtenir les statistiques de votes
  getVoteStatistics(): Observable<VoteStatistics> {
    return this.http.get<VoteStatistics>(`${this.API_BASE_URL}/votes`).pipe(
      catchError(error => {
        console.error('Error getting vote statistics:', error);
        return of(this.getMockVoteStatistics());
      })
    );
  }

  // Obtenir les statistiques d'activité par période
  getActivityStatistics(): Observable<ActivityStatistics> {
    return this.http.get<ActivityStatistics>(`${this.API_BASE_URL}/activity`).pipe(
      catchError(error => {
        console.error('Error getting activity statistics:', error);
        return of(this.getMockActivityStatistics());
      })
    );
  }

  // Obtenir les statistiques par utilisateur
  getUserStatistics(userId: number): Observable<any> {
    return this.http.get<any>(`${this.API_BASE_URL}/user/${userId}`).pipe(
      catchError(error => {
        console.error('Error getting user statistics:', error);
        return of({});
      })
    );
  }

  // Obtenir les statistiques par post
  getPostStatistics(postId: number): Observable<any> {
    return this.http.get<any>(`${this.API_BASE_URL}/post/${postId}`).pipe(
      catchError(error => {
        console.error('Error getting post statistics:', error);
        return of({});
      })
    );
  }

  // Exporter les statistiques en CSV
  exportStatisticsToCsv(): Observable<string> {
    return this.http.get<string>(`${this.API_BASE_URL}/export/csv`).pipe(
      catchError(error => {
        console.error('Error exporting statistics:', error);
        return of('');
      })
    );
  }

  // Obtenir les statistiques en temps réel
  getRealTimeStatistics(): Observable<any> {
    return this.http.get<any>(`${this.API_BASE_URL}/realtime`).pipe(
      catchError(error => {
        console.error('Error getting real-time statistics:', error);
        return of({});
      })
    );
  }

  // Méthodes utilitaires pour le calcul local
  calculatePostPopularity(post: Post): number {
    let score = 0;
    
    // Points pour les commentaires
    score += (post.comments?.length || 0) * 2;
    
    // Points pour les likes
    if (post.likePost) {
      switch (post.likePost) {
        case LikePost.Love: score += 10; break;
        case LikePost.Support: score += 8; break;
        case LikePost.Celebrate: score += 7; break;
        case LikePost.Insightful: score += 6; break;
        case LikePost.Like: score += 5; break;
        case LikePost.Funny: score += 3; break;
        case LikePost.Dislike: score -= 1; break;
      }
    }
    
    // Bonus pour les posts récents
    const postDate = new Date(post.datePost);
    const daysSinceCreation = Math.floor((Date.now() - postDate.getTime()) / (1000 * 60 * 60 * 24));
    if (daysSinceCreation < 7) {
      score += 2;
    }
    
    return score;
  }

  calculateCommentScore(comment: Comment): number {
    let score = 0;
    
    // Points pour le vote
    if (comment.voteComment === VoteComment.UpVote) {
      score += 1;
    } else if (comment.voteComment === VoteComment.DownVote) {
      score -= 1;
    }
    
    // Points pour la réaction
    if (comment.reaction) {
      switch (comment.reaction) {
        case LikePost.Love: score += 2; break;
        case LikePost.Support: score += 1.5; break;
        case LikePost.Celebrate: score += 1.3; break;
        case LikePost.Insightful: score += 1.2; break;
        case LikePost.Like: score += 1; break;
        case LikePost.Funny: score += 0.8; break;
        case LikePost.Dislike: score -= 0.5; break;
      }
    }
    
    // Points pour les réponses
    if (comment.reponse && comment.reponse.length > 0) {
      score += comment.reponse.length * 0.5;
    }
    
    return Math.round(score * 10) / 10;
  }

  // Méthodes mock pour le développement
  private getMockStatistics(): ForumStatistics {
    return {
      totalPosts: 150,
      totalComments: 450,
      totalUsers: 85,
      approvedPosts: 120,
      pendingPosts: 25,
      avgCommentsPerPost: 3.0,
      mostActiveUsers: [],
      mostPopularPosts: [],
      trendingTopics: [],
      voteStatistics: this.getMockVoteStatistics(),
      activityStats: this.getMockActivityStatistics()
    };
  }

  private getMockVoteStatistics(): VoteStatistics {
    return {
      commentVotes: { UpVote: 320, DownVote: 45 },
      postLikes: { Like: 85, Love: 25, Support: 15, Celebrate: 10, Insightful: 8, Funny: 5, Dislike: 2 },
      commentReactions: { Like: 120, Love: 45, Support: 30, Celebrate: 20, Insightful: 15, Funny: 10, Dislike: 5 },
      totalCommentVotes: 365,
      totalPostLikes: 150,
      totalCommentReactions: 245,
      netScore: 275,
      upVotePercentage: 87.7,
      downVotePercentage: 12.3
    };
  }

  private getMockActivityStatistics(): ActivityStatistics {
    return {
      postsToday: 5,
      postsThisWeek: 28,
      postsThisMonth: 95,
      commentsToday: 15,
      commentsThisWeek: 85,
      commentsThisMonth: 320
    };
  }
}
