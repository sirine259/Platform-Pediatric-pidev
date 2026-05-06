import { Component, HostListener, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ForumService, Post, LikePost } from '../../../services/forum.service';
import { MaterialModule } from '../../../shared/material.module';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { PostRatingService } from '../../../services/post-rating.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-forum',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    RouterModule,
    MaterialModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatChipsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './forum.component.html',
  styleUrls: ['./forum.component.css']
})
export class ForumComponent {
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
  currentUserId: string = '';
  currentUserNumericId: number | null = null;
  userRatingsByPost: { [postId: number]: number } = {};
  
  // Système de notation par étoiles
  starRatings = [1, 2, 3, 4, 5];

  reactions = [
    { type: LikePost.Like, icon: 'fa fa-thumbs-up', label: 'Like' },
    { type: LikePost.Celebrate, icon: 'fa fa-trophy', label: 'Celebrate' },
    { type: LikePost.Support, icon: 'fa fa-handshake-o', label: 'Support' },
    { type: LikePost.Love, icon: 'fa fa-heart', label: 'Love' },
    { type: LikePost.Insightful, icon: 'fa fa-lightbulb-o', label: 'Insightful' },
    { type: LikePost.Funny, icon: 'fa fa-smile-o', label: 'Funny' },
    { type: LikePost.Dislike, icon: 'fa fa-thumbs-down', label: 'Dislike' }
  ];
  
  currentPage: number = 0;
  pageSize: number = 5;
  totalElements: number = 0;
  totalPages: number = 0;
  length: number = 0;
  
  posts: Post[] = [];
  filteredPosts: Post[] = [];
  searchTerm: string = '';
  searchColumns: string[] = ['title', 'subject', 'content'];
  selectedCategory: string = '';
  dateFrom: string | null = null;
  dateTo: string | null = null;
  sortOrder: 'newest' | 'oldest' | 'az' | 'za' = 'newest';
  
  searchColumnOptions = [
    { value: 'title', label: 'Titre' },
    { value: 'subject', label: 'Sujet' },
    { value: 'content', label: 'Contenu' },
    { value: 'authorName', label: 'Auteur' }
  ];
  
  sortOptions = [
    { value: 'newest', label: 'Plus récents' },
    { value: 'oldest', label: 'Plus anciens' },
    { value: 'az', label: 'A - Z' },
    { value: 'za', label: 'Z - A' }
  ];
  
  categories = [
    { value: '', label: 'Toutes les catégories' },
    { value: 'transplantation-renale', label: 'Transplantation Rénale' },
    { value: 'dialyse-peritoneale', label: 'Dialyse Péritonéale' },
    { value: 'hemodialyse', label: 'Hémodialyse' },
    { value: 'nutrition-pediatrique', label: 'Nutrition Pédiatrique' }
  ];

  dashboardLoading: boolean = false;
  dashboardData: any = null;
  isLoading = false;
  apiUrl = environment.apiUrl;

  constructor(
    private postService: ForumService,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private postRatingService: PostRatingService,
    private authService: AuthService
  ) {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser?.id !== undefined && currentUser?.id !== null) {
      this.currentUserNumericId = currentUser.id;
      this.currentUserId = `user_${currentUser.id}`;
    } else {
      this.currentUserNumericId = null;
      this.currentUserId = 'guest';
    }
  }

  // Générer un nouvel ID utilisateur (pour tester avec différents users)
  generateNewUserId(): string {
    return 'user_' + Math.random().toString(36).substr(2, 9) + '_' + Date.now();
  }

  // Méthode pour réinitialiser les ratings d'un post (pour test)
  resetRatings(postId: number): void {
    if (confirm('Voulez-vous vraiment supprimer tous les ratings de ce post?')) {
      this.postService.clearRatings(postId).subscribe(() => {
        this.loadPosts();
      });
    }
  }

  ngOnInit(): void {
    this.loadPosts();
  }
  
  loadPosts(): void {
    this.isLoading = true;
    
    this.postService.getAllPosts().subscribe({
      next: (posts) => {
        this.posts = posts.reverse();
        this.filteredPosts = this.posts;
        this.totalElements = this.posts.length;
        this.totalPages = Math.ceil(this.totalElements / this.pageSize);
        this.length = this.totalElements;
        this.isLoading = false;
        this.loadRatingsForVisiblePosts();
        this.cdr.detectChanges();
        console.log('Posts chargés:', this.posts);
        console.log('Total elements:', this.totalElements);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des posts', error);
        this.isLoading = false;
      }
    });
  }

  private loadRatingsForVisiblePosts(): void {
    this.posts.forEach((post) => {
      if (!post.id) return;

      this.postRatingService.getPostRatingSummary(post.id).subscribe({
        next: (summary) => {
          post.averageRating = summary.averageRating || 0;
          post.ratingCount = summary.totalRatings || 0;
          post.ratingDistribution = summary.ratingDistribution || { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
        }
      });

      if (this.currentUserNumericId !== null) {
        this.postRatingService.getUserRating(post.id).subscribe({
          next: (rating) => {
            this.userRatingsByPost[post.id!] = rating?.rating || 0;
          }
        });
      } else {
        this.userRatingsByPost[post.id] = 0;
      }
    });
  }

  getPaginatorData(): Post[] {
    if (!this.filteredPosts || this.filteredPosts.length === 0) return [];
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.filteredPosts.slice(startIndex, endIndex);
  }

  onPageChange(event: PageEvent): void {
    const previousPage = this.currentPage;
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    
    // Valider que la page est dans les limites
    const totalPages = Math.ceil(this.filteredPosts.length / this.pageSize);
    if (this.currentPage >= totalPages) {
      this.currentPage = Math.max(0, totalPages - 1);
    }
    
    // Forcer le re-render complet
    if (this.currentPage !== previousPage) {
      this.cdr.markForCheck();
      setTimeout(() => {
        this.cdr.detectChanges();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }, 50);
    }
  }

  // Methode pour recharger la page actuelle
  refreshCurrentPage(): void {
    this.cdr.markForCheck();
    this.cdr.detectChanges();
  }

  filterPosts(): void {
    let filtered = [...this.posts];
    
    if (this.searchTerm.trim() !== '' && this.searchColumns.length > 0) {
      const keyword = this.searchTerm.toLowerCase();
      filtered = filtered.filter(post => {
        return this.searchColumns.some(column => {
          switch (column) {
            case 'title':
              return (post.title || '').toLowerCase().includes(keyword);
            case 'subject':
              return (post.subject || '').toLowerCase().includes(keyword);
            case 'content':
              return (post.content || '').toLowerCase().includes(keyword);
            case 'authorName':
              return (post.authorName || post.author?.userName || '').toLowerCase().includes(keyword);
            default:
              return false;
          }
        });
      });
    }
    
    if (this.selectedCategory) {
      filtered = filtered.filter(post => post.category === this.selectedCategory);
    }
    
    if (this.dateFrom) {
      const fromDate = new Date(this.dateFrom);
      filtered = filtered.filter(post => post.datePost && new Date(post.datePost) >= fromDate);
    }
    
    if (this.dateTo) {
      const toDate = new Date(this.dateTo);
      filtered = filtered.filter(post => post.datePost && new Date(post.datePost) <= toDate);
    }
    
    filtered = this.sortPosts(filtered);
    
    this.filteredPosts = filtered;
    this.totalElements = filtered.length;
    this.totalPages = Math.ceil(this.totalElements / this.pageSize);
    this.currentPage = 0;
  }

  sortPosts(posts: Post[]): Post[] {
    return [...posts].sort((a, b) => {
      switch (this.sortOrder) {
        case 'newest':
          return new Date(b.datePost || 0).getTime() - new Date(a.datePost || 0).getTime();
        case 'oldest':
          return new Date(a.datePost || 0).getTime() - new Date(b.datePost || 0).getTime();
        case 'az':
          return (a.title || a.subject || '').localeCompare(b.title || b.subject || '');
        case 'za':
          return (b.title || b.subject || '').localeCompare(a.title || a.subject || '');
        default:
          return 0;
      }
    });
  }

  toggleColumn(column: string): void {
    const index = this.searchColumns.indexOf(column);
    if (index > -1) {
      if (this.searchColumns.length > 1) {
        this.searchColumns.splice(index, 1);
      }
    } else {
      this.searchColumns.push(column);
    }
  }

  isColumnSelected(column: string): boolean {
    return this.searchColumns.includes(column);
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedCategory = '';
    this.dateFrom = null;
    this.dateTo = null;
    this.sortOrder = 'newest';
    this.searchColumns = ['title', 'subject', 'content'];
    this.filteredPosts = this.posts;
    this.totalElements = this.posts.length;
    this.currentPage = 0;
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

  // === Système de notation ===
  
  getUserRating(postId: number): number {
    if (!postId) return 0;
    return this.userRatingsByPost[postId] || 0;
  }

  // Vérifier si l'utilisateur a déjà noté ce post
  hasUserRated(postId: number): boolean {
    return this.getUserRating(postId) > 0;
  }

  getPostRatingInfo(post: Post): { average: number, count: number, distribution: { [key: number]: number } } {
    const avg = post.averageRating || 0;
    const count = post.ratingCount || post.ratings?.length || 0;
    const distribution = post.ratingDistribution || { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
    return { average: avg, count, distribution };
  }

  isRatingInProgress: { [postId: number]: boolean } = {};

  ratePost(postId: number, rating: number): void {
    if (!postId) return;

    if (this.currentUserNumericId === null) {
      alert('Veuillez vous connecter pour noter ce post.');
      return;
    }
    
    // Empêcher les clics multiples rapides
    if (this.isRatingInProgress[postId]) {
      return;
    }
    
    const post = this.posts.find(p => p.id === postId);
    if (post && this.isOwnPost(post)) {
      alert('Vous ne pouvez pas noter votre propre post.');
      return;
    }
    
    if (rating < 1 || rating > 5) {
      alert('La note doit être entre 1 et 5.');
      return;
    }
    
    // Vérifier si l'utilisateur a déjà noté ce post
    const existingRating = this.getUserRating(postId);
    const action = existingRating > 0 ? 'modifiée' : 'enregistrée';
    
    // Bloquer les clics multiples
    this.isRatingInProgress[postId] = true;
    
    this.postRatingService.ratePost(postId, rating).subscribe({
      next: () => {
        this.isRatingInProgress[postId] = false;
        this.userRatingsByPost[postId] = rating;
        const post = this.posts.find(p => p.id === postId);
        if (post) {
          this.postRatingService.getPostRatingSummary(postId).subscribe({
            next: (summary) => {
              post.averageRating = summary.averageRating || 0;
              post.ratingCount = summary.totalRatings || 0;
              post.ratingDistribution = summary.ratingDistribution || { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
            }
          });
        }

        const stats = this.getPostRatingInfo(post || {} as Post);
        alert(`Note ${action}! Moyenne: ${stats.average}/5 (${stats.count} votes)`);
      },
      error: (err) => {
        this.isRatingInProgress[postId] = false;
        console.error('Erreur lors de la notation:', err);
        alert('Erreur lors de l\'enregistrement de votre note.');
      }
});
  }

  isOwnPost(post: Post): boolean {
    if (!post.id) return false;
    if (this.currentUserNumericId === null) return false;
    const authorId = (post as any).author?.id || (post as any).user?.id;
    return Number(authorId) === Number(this.currentUserNumericId);
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
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
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
