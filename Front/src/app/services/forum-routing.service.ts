import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ForumRoutingService {
  private baseUrl = environment.apiUrl + '/api';

  constructor(private http: HttpClient) {}

  // Upload vidéo vers le backend Spring (stockage interne plateforme)
  uploadVideo(file: File): Observable<{ url: string; fileName: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string; fileName: string }>(`${this.baseUrl}/videos/upload`, formData);
  }

  // Forums
  getForums(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/forums`);
  }

  getForumById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/forums/${id}`);
  }

  createForum(forum: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/forums`, forum);
  }

  updateForum(id: number, forum: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/forums/${id}`, forum);
  }

  deleteForum(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/forums/${id}`);
  }

  // Posts
  getPosts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/forum/posts`);
  }

  getPostById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/forum/posts/${id}`);
  }

  getPostsByForumId(forumId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/forum/posts/forum/${forumId}`);
  }

  createPost(post: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/forum/posts`, post);
  }

  updatePost(id: number, post: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/forum/posts/${id}`, post);
  }

  deletePost(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/forum/posts/${id}`);
  }

  searchPostsByTitle(title: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/forum/posts/search/title?title=${title}`);
  }

  // Pagination
  getPostsWithPagination(page: number = 0, size: number = 5, sortBy: string = 'createdAt', sortDir: string = 'DESC'): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/forum/posts/page?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`);
  }

  searchPostsWithPagination(keyword: string, category: string, page: number = 0, size: number = 5, sortBy: string = 'createdAt', sortDir: string = 'DESC'): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/forum/posts/search?keyword=${keyword}&category=${category}&page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`);
  }

  // Comments
  getCommentsByPostId(postId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/forum/comments/post/${postId}`);
  }

  createComment(comment: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/forum/comments`, comment);
  }

  deleteComment(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/forum/comments/${id}`);
  }

  // Image upload
  uploadPostImage(postId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', file);
    return this.http.post<any>(`${this.baseUrl}/forum/posts/${postId}/upload`, formData);
  }

  // Statistics
  getForumStats(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/forum/stats`);
  }

  getPostStats(postId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/forum/posts/${postId}/stats`);
  }
}
