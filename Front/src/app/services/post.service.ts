import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post, PostType, Forum, ForumComment, User, Transplant } from '../models/post.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = environment.apiUrl + '/api/posts';

  constructor(private http: HttpClient) {}

  // CRUD Posts
  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }

  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  createPost(post: Partial<Post>): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, post);
  }

  updatePost(id: number, post: Partial<Post>): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, post);
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Posts par type (pour postulations médicales)
  getPostulationPosts(): Observable<Post[]> {
    // Récupère les posts de type FOLLOW_UP et MEDICAL_UPDATE
    return this.http.get<Post[]>(`${this.apiUrl}/postulations`);
  }

  getForumPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/type/${PostType.FORUM}`);
  }

  getMedicalPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/medical`);
  }

  getFollowUpPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/follow-up`);
  }

  getAnnouncements(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/announcements`);
  }

  // Posts par forum
  getPostsByForum(forumId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/forum/${forumId}`);
  }

  // Posts par transplant
  getPostsByTransplant(transplantId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/transplant/${transplantId}`);
  }

  // Posts par auteur
  getPostsByAuthor(authorId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/author/${authorId}`);
  }

  // Recherche
  searchPosts(query: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/search?q=${query}`);
  }
}
