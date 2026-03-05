import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Comment, VoteComment, StatusComplaint } from '../models/comment.model';

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
}

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  // Pour le développement, utilisation de localStorage comme mock
  private readonly STORAGE_KEY = 'pediatric_forum_posts';
  private readonly API_BASE_URL = 'http://localhost:9091/stage/Post'; // URL du backend PIDEV

  constructor(private http: HttpClient) {}

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
}
