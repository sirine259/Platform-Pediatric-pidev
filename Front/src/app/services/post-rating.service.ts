import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PostRating } from '../models/rating.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PostRatingService {
  private readonly apiUrl = `${environment.apiUrl}/api/forum`;

  constructor(private http: HttpClient) {}

  ratePost(postId: number, rating: number): Observable<PostRating> {
    const params = new HttpParams().set('rating', String(rating));
    return this.http.post<PostRating>(`${this.apiUrl}/posts/${postId}/rating`, null, { params });
  }

  getPostRatingSummary(postId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/posts/${postId}/rating`).pipe(
      catchError(() =>
        of({
          postId,
          averageRating: 0,
          totalRatings: 0,
          ratingDistribution: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 }
        })
      )
    );
  }

  getPostRating(postId: number): Observable<any> {
    return this.getPostRatingSummary(postId);
  }

  getUserRating(postId: number): Observable<PostRating | null> {
    return this.http
      .get<PostRating>(`${this.apiUrl}/posts/${postId}/rating/me`)
      .pipe(catchError(() => of(null)));
  }

  deleteRating(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/posts/${postId}/rating`);
  }

  getTopRatedPosts(limit: number = 10): Observable<any[]> {
    const params = new HttpParams().set('limit', String(limit));
    return this.http.get<any[]>(`${this.apiUrl}/posts/ratings/top`, { params });
  }
}