import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Comment, LikePost, VoteComment, Post } from '../models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private readonly STORAGE_KEY = 'pediatric_forum_comments';
  private readonly API_BASE_URL = 'http://localhost:9091/stage'; // URL du backend PIDEV

  constructor(private http: HttpClient) {}

  // Pour le développement, utilisation de localStorage comme mock
  private getStoredComments(): Comment[] {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
  }

  private saveComments(comments: Comment[]): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(comments));
  }

  // Récupérer tous les commentaires
  getAllComments(): Observable<Comment[]> {
    return of(this.getStoredComments());
  }

  // Récupérer les commentaires d'un post
  getCommentsByPostId(postId: number): Observable<Comment[]> {
    const comments = this.getStoredComments();
    const postComments = comments.filter(comment => comment.postId === postId);
    return of(postComments);
  }

  // Ajouter un commentaire
  addComment(comment: Comment): Observable<Comment> {
    const comments = this.getStoredComments();
    const newComment: Comment = {
      ...comment,
      id: Date.now(),
      dateComment: new Date().toISOString(),
      voteComment: VoteComment.UpVote,
      reaction: undefined
    };
    
    comments.unshift(newComment);
    this.saveComments(comments);
    
    return of(newComment);
  }

    if (index !== -1) {
      comments[index] = { ...updatedComment, id: commentId };
      this.saveComments(comments);
      return of(comments[index]);
    }
    return of(updatedComment);
  }

  // Supprimer un commentaire
  deleteComment(commentId: number): Observable<void> {
    const comments = this.getStoredComments();
    const filteredComments = comments.filter(comment => comment.id !== commentId);
    this.saveComments(filteredComments);
    return of(void 0);
  }

  // Ajouter une réponse à un commentaire
  addReply(postId: number, commentId: number, replyText: string): Observable<Comment> {
    const comments = this.getStoredComments();
    const parentComment = this.findCommentRecursive(comments, commentId);
    
    if (parentComment) {
      const newReply: Comment = {
        id: Date.now(),
        description: replyText,
        dateComment: new Date().toISOString(),
        author: 'Utilisateur actuel',
        voteComment: VoteComment.UpVote,
        reaction: undefined,
        reponse: []
      };
      
      if (!parentComment.reponse) {
        parentComment.reponse = [];
      }
      parentComment.reponse.unshift(newReply);
      this.saveComments(comments);
      return of(newReply);
    }
    
    return of({
      id: 0,
      description: replyText,
      dateComment: new Date().toISOString(),
      author: 'Utilisateur actuel',
      voteComment: VoteComment.UpVote,
      reaction: undefined,
      reponse: []
    });
  }

  // Trouver un commentaire récursivement
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

  // Mettre à jour la réaction d'un commentaire
  updateCommentReaction(commentId: number, reaction: LikePost): Observable<any> {
    const comments = this.getStoredComments();
    const comment = comments.find(c => c.id === commentId);
    if (comment) {
      comment.reaction = reaction;
      this.saveComments(comments);
    }
    return of({ success: true });
  }

  // Mettre à jour la réaction d'une réponse
  updateReplyReaction(replyId: number, reaction: LikePost): Observable<any> {
    const comments = this.getStoredComments();
    
    // Chercher la réponse dans tous les commentaires et réponses
    const findReply = (commentList: Comment[]): Comment | undefined => {
      for (const comment of commentList) {
        if (comment.id === replyId) {
          return comment;
        }
        if (comment.reponse) {
          const found = findReply(comment.reponse);
          if (found) return found;
        }
      }
      return undefined;
    };
    
    const reply = findReply(comments);
    if (reply) {
      reply.reaction = reaction;
      this.saveComments(comments);
    }
    
    return of({ success: true });
  }

  // Voter pour un commentaire (UpVote/DownVote)
  voteComment(commentId: number, vote: VoteComment): Observable<any> {
    const comments = this.getStoredComments();
    const comment = comments.find(c => c.id === commentId);
    if (comment) {
      comment.voteComment = vote;
      this.saveComments(comments);
    }
    return of({ success: true });
  }

  // Supprimer un commentaire d'un post
  deleteCommentFromPost(postId: number, commentId: number): Observable<any> {
    const comments = this.getStoredComments();
    const comment = comments.find(c => c.id === commentId);
    
    if (comment) {
      // Supprimer le commentaire
      const filteredComments = comments.filter(c => c.id !== commentId);
      this.saveComments(filteredComments);
    }
    
    return of({ success: true });
  }

  // Appels HTTP au backend PIDEV (pour utilisation réelle)
  private addCommentToBackend(comment: Comment): Observable<Comment> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<Comment>(`${this.API_BASE_URL}/Comment/add`, comment, { headers });
  }

  private updateCommentInBackend(commentId: number, comment: Comment): Observable<Comment> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<Comment>(`${this.API_BASE_URL}/Comment/${commentId}`, comment, { headers });
  }

  private updateCommentReactionInBackend(commentId: number, reaction: string): Observable<any> {
    return this.http.put(`${this.API_BASE_URL}/Comment/comment/${commentId}/react?reaction=${reaction}`, {}, { responseType: 'text' });
  }

  private voteCommentInBackend(commentId: number, vote: string): Observable<any> {
    return this.http.put(`${this.API_BASE_URL}/Comment/${commentId}/vote?vote=${vote}`, {});
  }
}
