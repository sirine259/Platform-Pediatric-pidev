import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ForumService, Post, LikePost } from '../../../services/forum.service';
import { MaterialModule } from '../../../shared/material.module';

@Component({
  selector: 'app-forum',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    RouterModule,
    MaterialModule
  ],
  templateUrl: './forum.component.html',
  styleUrls: ['./forum.component.css']
})
export class ForumComponent {
  // Expose LikePost enum to template
  LikePost = LikePost;
  post: Post = {
    subject: '',
    content: '',
    date: new Date(),
    isAnonymous: false,
    picture: null,
    datePost: new Date().toISOString(),
    user: undefined,
    comments: [],
  };

  activePostId: number | null = null;

  reactions = [
    { type: LikePost.Like, icon: 'fa fa-thumbs-up', label: 'Like' },
    { type: LikePost.Celebrate, icon: 'fa fa-trophy', label: 'Celebrate' },
    { type: LikePost.Support, icon: 'fa fa-handshake-o', label: 'Support' },
    { type: LikePost.Love, icon: 'fa fa-heart', label: 'Love' },
    { type: LikePost.Insightful, icon: 'fa fa-lightbulb-o', label: 'Insightful' },
    { type: LikePost.Funny, icon: 'fa fa-smile-o', label: 'Funny' },
    { type: LikePost.Dislike, icon: 'fa fa-thumbs-down', label: 'Dislike' }
  ];
  
  currentPage: number = 1;
  itemsPerPage: number = 3;
  posts: Post[] = [];
  filteredPosts: Post[] = [];
  searchTerm: string = '';
  dashboardLoading: boolean = false;
  dashboardData: any = null;

  constructor(
    private postService: ForumService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }
  
  // Charger les posts
  loadPosts(): void {
    this.postService.getAllPosts().subscribe(
      (data) => {
        this.posts = (data || []).reverse();
        this.filteredPosts = this.posts.filter(post => post.status === 'Approved');
        this.reorderPosts();
        console.log('Posts chargés:', this.posts);
        console.log('Posts received:', this.filteredPosts);
      },
      (error) => {
        console.error('Erreur lors du chargement des posts', error);
      }
    );
  }

  filterPosts(): void {
    console.log('Search term:', this.searchTerm);
    if (this.searchTerm.trim() === '') {
      this.filteredPosts = this.posts.filter(post => post.status === 'Approved');
    } else {
      console.log('Calling API for search by title with term:', this.searchTerm);
      this.postService.searchPostsBySubject(this.searchTerm).subscribe(
        (data) => {
          console.log('Search results:', data);
          this.filteredPosts = data;
        },
        (error) => {
          console.error('Error searching posts', error);
        }
      );
    }
  }

  // Delete a post
  deletePost(id: number | undefined): void {
    if (id === undefined) {
      console.error('Invalid post ID');
      return;
    }

    if (confirm('Are you sure you want to delete this post?')) {
      this.postService.deletePost(id).subscribe(
        () => {
          alert('Post deleted successfully!');
          this.posts = this.posts.filter(post => post.id !== id);
          this.filteredPosts = this.posts;
        },
        (error) => {
          console.error('Error deleting post', error);
          alert('Error deleting post. Please try again.');
        }
      );
    }
  }

  // Navigate to Post Details Component (uses nested forum routes)
  getPostDetails(id: number | undefined): void {
    if (id !== undefined) {
      this.router.navigate(['/forum/post', id]);
    } else {
      console.error('Invalid post ID');
    }
  }
 
  updatePost(postId: number): void {
    this.router.navigate(['/forum/post-update', postId]);
  }

  likePost(postId: number, reactionType: LikePost) {
    this.postService.likePost(postId, reactionType).subscribe({
      next: () => {
        const post = this.posts.find(p => p.id === postId);
        if (post) {
          post.likePost = reactionType;
        }
        this.activePostId = null;
        console.log(`Post liked with ${reactionType}`);
      },
      error: (err) => {
        console.error('Error liking post:', err);
      }
    });
  }

  // Close reactions when clicking outside
  @HostListener('document:click')
  onDocumentClick() {
    this.activePostId = null;
  }

  // Close reactions on Escape
  @HostListener('document:keydown.escape')
  onEscape() {
    this.activePostId = null;
  }

  toggleReactions(postId: number) {
    this.activePostId = this.activePostId === postId ? null : postId;
  }

  getReactionIcon(reaction: string | null): string {
    const reactionObj = this.reactions.find(r => r.type === reaction);
    return reactionObj ? reactionObj.icon : 'fa fa-thumbs-up';
  }

  handleReactionClick(postId: number, reactionType: LikePost) {
    this.likePost(postId, reactionType);
    this.activePostId = null;
  }

  speakPost(post: Post): void {
    const textToSpeak = `${post.subject}. ${post.content}`;
    const synth = window.speechSynthesis;

    if (!synth || typeof SpeechSynthesisUtterance === 'undefined') {
      alert('La synthèse vocale n’est pas disponible dans ce navigateur.');
      return;
    }

    // Arrêter toute lecture en cours
    if (synth.speaking) {
      synth.cancel();
    }

    const utterance = new SpeechSynthesisUtterance(textToSpeak);
    // Adapter la langue à ton public
    utterance.lang = 'fr-FR';
    utterance.rate = 1;
    utterance.pitch = 1;

    synth.speak(utterance);
  }

  // Helper function
  getPinnedPostIds(): number[] {
    const pinned = localStorage.getItem('pinnedPosts');
    return pinned ? JSON.parse(pinned) : [];
  }

  pinPost(postId: number | undefined): void {
    if (postId === undefined) return;

    let pinned = this.getPinnedPostIds();
    if (!pinned.includes(postId)) {
      pinned.unshift(postId);
      localStorage.setItem('pinnedPosts', JSON.stringify(pinned));
      this.reorderPosts();
    }
  }

  unpinPost(postId: number | undefined): void {
    if (postId === undefined) return;

    let pinned = this.getPinnedPostIds();
    pinned = pinned.filter(id => id !== postId);
    localStorage.setItem('pinnedPosts', JSON.stringify(pinned));
    this.reorderPosts();
  }

  isPinned(postId: number | undefined): boolean {
    if (postId === undefined) return false;
    return this.getPinnedPostIds().includes(postId);
  }

  reorderPosts(): void {
    const pinnedIds = this.getPinnedPostIds();
    const pinnedPosts: any[] = [];
    const unpinnedPosts: any[] = [];

    for (let post of this.posts) {
      if (pinnedIds.includes(post.id!)) {
        pinnedPosts.push(post);
      } else {
        unpinnedPosts.push(post);
      }
    }

    pinnedPosts.sort((a, b) => pinnedIds.indexOf(a.id!) - pinnedIds.indexOf(b.id!));

    this.filteredPosts = [...pinnedPosts, ...unpinnedPosts];
  }

  getPaginatedPosts(): any[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredPosts.slice(start, end);
  }

  getTodayTrending(): any[] {
    return this.filteredPosts.slice(0, 5);
  }

  getTopUsersLocal(): any[] {
    return [];
  }

  getQuickStats(): any {
    return {
      posts: this.posts.length,
      comments: this.posts.reduce((sum, post) => sum + (post.comments?.length || 0), 0),
      likes: this.posts.filter(post => post.likePost).length,
      engagement: 0
    };
  }

  // Calculer le score d'un post (méthode manquante)
  calculatePostScore(post: any): number {
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
        case LikePost.Funny: score += 5; break;
        case LikePost.Like: score += 3; break;
        case LikePost.Dislike: score -= 2; break;
      }
    }
    
    return score;
  }
}
