import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Comment, VoteComment, StatusComplaint } from '../models/comment.model';
import { environment } from '../../environments/environment';

// Exporter LikePost pour l'utiliser dans les composants
export enum LikePost {
  Like = 'Like',
  Celebrate = 'Celebrate',
  Support = 'Support',
  Love = 'Love',
  Insightful = 'Insightful',
  Funny = 'Funny',
  Dislike = 'Dislike'
}

export interface PostRating {
  id: number;
  postId: number;
  userId: string;
  rating: number; // 1-5
  createdAt: string;
}

export interface Post {
  id?: number;
  // Ancien modèle (mock local)
  subject?: string;
  content?: string;
  date?: Date;
  isAnonymous: boolean;
  datePost?: string;
  user?: any;
  comments: Comment[];
  picture: string | null;
  videoUrl?: string | null;
  status?: StatusComplaint;
  likePost?: LikePost;
  archivedReason?: string;

  // Champs utilisés par le backend PIDEV
  title?: string;
  createdAt?: string;
  author?: {
    userName?: string;
    firstName?: string;
    lastName?: string;
  };
  
  // Champs additionnels pour le forum
  category?: string;
  authorName?: string;
  isPrivate?: boolean;
  allowAnonymous?: boolean;
  requireModeration?: boolean;
  
  // Système de notation
  ratings?: PostRating[];
  averageRating?: number;
  ratingCount?: number;
  ratingDistribution?: { [key: number]: number }; // distribution en % par étoile (1-5)
}

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  // Pour le développement, utilisation de localStorage comme mock
  private readonly STORAGE_KEY = 'pediatric_forum_posts';
  private readonly API_BASE_URL = 'http://localhost:9091/stage/Post'; // URL du backend PIDEV
  private readonly VIDEO_API_URL = `${environment.apiUrl}/api/videos`;

  constructor(private http: HttpClient) {}

  // Upload vidéo vers le backend Spring
  uploadVideo(file: File): Observable<{ url: string; fileName: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string; fileName: string }>(`${this.VIDEO_API_URL}/upload`, formData);
  }

  // Ajouter un post
  addPost(post: Post, file?: File): Observable<Post> {
    const posts = this.getStoredPosts();
    const newPost: Post = {
      ...post,
      id: Date.now(), // ID temporaire basé sur le timestamp
      datePost: new Date().toISOString(),
      status: StatusComplaint.Approved,
      comments: [],
      picture: file ? `temp_${file.name}` : null,
      likePost: undefined,
      archivedReason: undefined
    };
    
    posts.unshift(newPost); // Ajouter au début
    this.savePosts(posts);
    
    return of(newPost);
  }

  // Récupérer tous les posts
  getAllPosts(): Observable<Post[]> {
    const posts = this.getStoredPosts();
    return of(posts.filter(post => post.status === StatusComplaint.Approved));
  }

  // Pagination côté client
  getPostsWithPagination(page: number, size: number): Observable<{ content: Post[]; totalElements: number; totalPages: number }> {
    const posts = this.getStoredPosts().filter(post => post.status === StatusComplaint.Approved);
    const totalElements = posts.length;
    const totalPages = Math.ceil(totalElements / size);
    const startIndex = page * size;
    const content = posts.slice(startIndex, startIndex + size);
    return of({ content, totalElements, totalPages });
  }

  // Alias pour createPost (utilisé par post-create)
  createPost(post: Partial<Post>): Observable<Post> {
    return this.addPost(post as Post);
  }

  // Supprimer un post
  deletePost(id: number): Observable<void> {
    const posts = this.getStoredPosts();
    const filteredPosts = posts.filter(post => post.id !== id);
    this.savePosts(filteredPosts);
    return of(void 0);
  }

  // Mettre à jour un post
  updatePost(id: number, post: Post): Observable<Post> {
    const posts = this.getStoredPosts();
    const index = posts.findIndex(p => p.id === id);
    if (index !== -1) {
      posts[index] = { ...post, id };
      this.savePosts(posts);
      return of(posts[index]);
    }
    return of(post);
  }

  // Alias pour la mise à jour locale
  updatePostLocal(id: number, post: Post): Observable<Post> {
    return this.updatePost(id, post);
  }

  // Récupérer un post par ID
  getPostById(id: number): Observable<Post | null> {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === id);
    return of(post || null);
  }

  // Supprimer un commentaire d'un post
  deleteComment(postId: number, commentId: number): Observable<void> {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    if (post?.comments) {
      post.comments = post.comments.filter(comment => comment.id !== commentId);
      this.savePosts(posts);
    }
    return of(void 0);
  }

  // Rechercher des posts par titre (subject)
  searchPostsBySubject(searchTerm: string): Observable<Post[]> {
    const posts = this.getStoredPosts();
    const filtered = posts.filter(post => 
      post.status === StatusComplaint.Approved && 
      post.subject?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    return of(filtered);
  }

  // Like un post (système PIDEV)
  likePost(postId: number, likeType: LikePost): Observable<any> {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    if (post) {
      post.likePost = likeType;
      this.savePosts(posts);
    }
    return of({ success: true });
  }

  // Ajouter un commentaire (système PIDEV)
  addComment(postId: number, comment: string): Observable<Comment> {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    if (post) {
      const newComment: Comment = {
        id: Date.now(),
        description: comment,
        dateComment: new Date().toISOString(),
        voteComment: VoteComment.UpVote,
        postId: postId,
        reponse: []
      };
      
      if (!post.comments) {
        post.comments = [];
      }
      post.comments.push(newComment);
      this.savePosts(posts);
      return of(newComment);
    }
    return of({} as Comment);
  }

  // Répondre à un commentaire (système PIDEV)
  addReplyToComment(postId: number, commentId: number, replyText: string): Observable<Comment> {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    if (post) {
      const comment = post.comments?.find(c => c.id === commentId);
      if (comment) {
        const reply: Comment = {
          id: Date.now(),
          description: replyText,
          dateComment: new Date().toISOString(),
          voteComment: VoteComment.UpVote,
          postId: postId,
          reponse: []
        };
        
        if (!comment.reponse) {
          comment.reponse = [];
        }
        comment.reponse.push(reply);
        this.savePosts(posts);
        return of(reply);
      }
    }
    return of({} as Comment);
  }

  // Like un commentaire (système PIDEV)
  likeComment(commentId: number, reactionType: string): Observable<any> {
    return of({ success: true });
  }

  // Like une réponse (système PIDEV)
  likeReply(replyId: number, likeType: string): Observable<any> {
    return of({ success: true });
  }

  // Voter pour un commentaire (UpVote/DownVote)
  voteComment(commentId: number, vote: VoteComment): Observable<any> {
    const posts = this.getStoredPosts();
    
    // Chercher le commentaire dans tous les posts
    const findComment = (comments: Comment[]): Comment | undefined => {
      for (const comment of comments) {
        if (comment.id === commentId) {
          return comment;
        }
        if (comment.reponse) {
          const found = findComment(comment.reponse);
          if (found) return found;
        }
      }
      return undefined;
    };
    
    for (const post of posts) {
      if (post.comments) {
        const comment = findComment(post.comments);
        if (comment) {
          comment.voteComment = vote;
          this.savePosts(posts);
          return of({ success: true });
        }
      }
    }
    
    return of({ success: false });
  }

  // Mettre à jour le statut d'un post
  updatePostStatus(postId: number, newStatus: StatusComplaint): Observable<Post> {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    if (post) {
      post.status = newStatus;
      this.savePosts(posts);
      return of(post);
    }
    return of({} as Post);
  }

  // Méthodes privées pour le localStorage
  private getStoredPosts(): Post[] {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored ? JSON.parse(stored) : this.getDefaultPosts();
  }

  private savePosts(posts: Post[]): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(posts));
  }

  private getDefaultPosts(): Post[] {
    return [
      {
        id: 1,
        subject: 'Transplantation rénale chez les enfants',
        content: 'Je cherche des informations sur la transplantation rénale pédiatrique. Mon fils de 8 ans est en attente d\'une greffe et j\'aimerais partager notre expérience.',
        date: new Date('2024-01-15'),
        isAnonymous: false,
        datePost: '2024-01-15T10:00:00',
        user: { userName: 'Marie P.', firstName: 'Marie', lastName: 'Parent' },
        comments: [],
        picture: null,
        status: StatusComplaint.Approved,
        likePost: LikePost.Like
      },
      {
        id: 2,
        subject: 'Dialyse péritonéale à domicile',
        content: 'Nous avons commencé la dialyse péritonéale à domicile il y a 6 mois. C\'est un grand changement mais on s\'y fait. Des conseils pour les parents ?',
        date: new Date('2024-02-20'),
        isAnonymous: true,
        datePost: '2024-02-20T14:30:00',
        user: { userName: 'Anonymous', firstName: 'Anonyme', lastName: '' },
        comments: [],
        picture: null,
        status: StatusComplaint.Approved,
        likePost: undefined
      }
    ];
  }

  // === Méthodes utilitaires privées ===
  private findCommentRecursive(comments: Comment[], commentId: number): Comment | null {
    for (const comment of comments) {
      if (comment.id === commentId) {
        return comment;
      }
      if (comment.reponse) {
        const found = this.findCommentRecursive(comment.reponse, commentId);
        if (found) return found;
      }
    }
    return null;
  }

  // === Système de notation (Rating 1-5) ===
  
  // Ajouter ou mettre à jour une note pour un post
  ratePost(postId: number, rating: number, userId: string): Observable<Post> {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    
    if (!post) {
      return of({} as Post);
    }
    
    if (!post.ratings) {
      post.ratings = [];
    }
    
    // Clé unique: userId + postId
    const existingRatingIndex = post.ratings.findIndex(r => r.userId === userId);
    
    if (existingRatingIndex >= 0) {
      // Mettre à jour la note existante (clé unique garantit 1 seul rating par user par post)
      post.ratings[existingRatingIndex].rating = rating;
      post.ratings[existingRatingIndex].createdAt = new Date().toISOString();
    } else {
      // Ajouter une nouvelle note avec clé unique (userId, postId)
      const newRating: PostRating = {
        id: Date.now(),
        postId: postId,
        userId: userId,
        rating: rating,
        createdAt: new Date().toISOString()
      };
      post.ratings.push(newRating);
    }
    
    // Calculer la moyenne et la distribution
    const stats = this.calculateRatingStats(post.ratings);
    post.averageRating = stats.average;
    post.ratingCount = stats.total;
    post.ratingDistribution = stats.distribution;
    
    this.savePosts(posts);
    return of(post);
  }
  
  // Calculer les statistiques (moyenne, total, distribution %)
  private calculateRatingStats(ratings: PostRating[]): { average: number; total: number; distribution: { [key: number]: number } } {
    if (ratings.length === 0) {
      return { average: 0, total: 0, distribution: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 } };
    }
    
    const total = ratings.length;
    const sum = ratings.reduce((acc, r) => acc + r.rating, 0);
    const average = Math.round((sum / total) * 10) / 10;
    
    // Distribution en pourcentage pour chaque étoile
    const distribution: { [key: number]: number } = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
    for (let i = 1; i <= 5; i++) {
      const count = ratings.filter(r => r.rating === i).length;
      distribution[i] = Math.round((count / total) * 100);
    }
    
    return { average, total, distribution };
  }
  
  // Obtenir la note d'un utilisateur pour un post
  getUserRating(postId: number, userId: string): number {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    
    if (post && post.ratings) {
      const userRating = post.ratings.find(r => r.userId === userId);
      return userRating ? userRating.rating : 0;
    }
    return 0;
  }
  
  // Calculer la note moyenne
  private calculateAverageRating(ratings: PostRating[]): number {
    if (ratings.length === 0) return 0;
    const sum = ratings.reduce((acc, r) => acc + r.rating, 0);
    return Math.round((sum / ratings.length) * 10) / 10; // 1 décimale
  }
  
  // Obtenir tous les ratings d'un post
  getPostRatings(postId: number): PostRating[] {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    return post?.ratings || [];
  }

  // Obtenir les statistiques de notation (moyenne, total, distribution en %)
  getRatingStats(postId: number): { average: number; total: number; distribution: { [key: number]: number } } {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    
    if (!post || !post.ratings || post.ratings.length === 0) {
      return { average: 0, total: 0, distribution: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 } };
    }
    
    const total = post.ratings.length;
    const sum = post.ratings.reduce((acc, r) => acc + r.rating, 0);
    const average = Math.round((sum / total) * 10) / 10;
    
    // Calculer la distribution en pourcentage
    const distribution: { [key: number]: number } = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
    for (let i = 1; i <= 5; i++) {
      const count = post.ratings.filter(r => r.rating === i).length;
      distribution[i] = Math.round((count / total) * 100);
    }
    
    return { average, total, distribution };
  }

  // Obtenir la note de l'utilisateur actuel pour un post
  getUserRatingForCurrentUser(postId: number): number {
    const userId = this.getCurrentUserId();
    return this.getUserRating(postId, userId);
  }

  // Obtenir l'ID de l'utilisateur actuel
  private getCurrentUserId(): string {
    let userId = localStorage.getItem('current_user_id');
    if (!userId) {
      userId = 'user_' + Math.random().toString(36).substr(2, 9);
      localStorage.setItem('current_user_id', userId);
    }
    return userId;
  }

  // Effacer tous les ratings d'un post (pour test)
  clearRatings(postId: number): Observable<void> {
    const posts = this.getStoredPosts();
    const post = posts.find(p => p.id === postId);
    if (post) {
      post.ratings = [];
      post.averageRating = 0;
      post.ratingCount = 0;
      post.ratingDistribution = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
      this.savePosts(posts);
    }
    return of(void 0);
  }
}
