import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';

export interface Forum {
  id: number;
  title: string;
  description: string;
  category: string;
  createdAt: string;
  posts: Post[];
}

export interface Post {
  id: number;
  title: string;
  content: string;
  author: string;
  createdAt: string;
  forumId: number;
  comments: Comment[];
}

export interface Comment {
  id: number;
  content: string;
  author: string;
  createdAt: string;
  postId: number;
}

@Injectable({
  providedIn: 'root'
})
export class MockForumService {
  private forums: Forum[] = [
    {
      id: 1,
      title: 'Transplantation rénale pédiatrique',
      description: 'Discussions sur les transplantations rénales chez les enfants',
      category: 'Médical',
      createdAt: '2024-01-15T10:00:00Z',
      posts: [
        {
          id: 1,
          title: 'Nouvelles techniques de transplantation',
          content: 'Les dernières avancées dans les techniques de transplantation rénale pédiatrique...',
          author: 'Dr. Martin',
          createdAt: '2024-01-20T14:30:00Z',
          forumId: 1,
          comments: [
            {
              id: 1,
              content: 'Très intéressant, merci pour le partage',
              author: 'Dr. Dupont',
              createdAt: '2024-01-21T09:15:00Z',
              postId: 1
            }
          ]
        }
      ]
    },
    {
      id: 2,
      title: 'Soutien aux patients',
      description: 'Espace de soutien et d\'entraide pour les patients et leurs familles',
      category: 'Support',
      createdAt: '2024-01-10T16:00:00Z',
      posts: []
    }
  ];

  getAllForums(): Observable<Forum[]> {
    return of(this.forums).pipe(delay(500));
  }

  getForumById(id: number): Observable<Forum | null> {
    return of(this.forums.find(f => f.id === id) || null).pipe(delay(300));
  }

  createForum(forum: Partial<Forum>): Observable<Forum> {
    const newForum: Forum = {
      id: this.forums.length + 1,
      title: forum.title || '',
      description: forum.description || '',
      category: forum.category || 'Général',
      createdAt: new Date().toISOString(),
      posts: []
    };
    this.forums.push(newForum);
    return of(newForum).pipe(delay(300));
  }

  getPostsByForumId(forumId: number): Observable<Post[]> {
    const forum = this.forums.find(f => f.id === forumId);
    return of(forum?.posts || []).pipe(delay(300));
  }

  createPost(post: Partial<Post>): Observable<Post> {
    const newPost: Post = {
      id: Math.floor(Math.random() * 1000),
      title: post.title || '',
      content: post.content || '',
      author: post.author || 'Anonymous',
      createdAt: new Date().toISOString(),
      forumId: post.forumId || 0,
      comments: []
    };

    const forum = this.forums.find(f => f.id === post.forumId);
    if (forum) {
      forum.posts.push(newPost);
    }

    return of(newPost).pipe(delay(300));
  }

  addComment(postId: number, comment: Partial<Comment>): Observable<Comment> {
    const newComment: Comment = {
      id: Math.floor(Math.random() * 1000),
      content: comment.content || '',
      author: comment.author || 'Anonymous',
      createdAt: new Date().toISOString(),
      postId: postId
    };

    for (const forum of this.forums) {
      const post = forum.posts.find(p => p.id === postId);
      if (post) {
        post.comments.push(newComment);
        break;
      }
    }

    return of(newComment).pipe(delay(200));
  }
}
