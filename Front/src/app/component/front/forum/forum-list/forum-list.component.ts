import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule } from '@angular/material/paginator';
import { PageEvent } from '@angular/material/paginator';
import { ForumService, Post } from '../../../../services/forum.service';

export interface Forum {
  id: string;
  title: string;
  description: string;
  category: string;
  author: string;
  createdAt: Date;
  postsCount: number;
  isPrivate: boolean;
  allowAnonymous: boolean;
  requireModeration: boolean;
}

export interface SearchCriteria {
  keyword: string;
  columns: string[];
  sortOrder: 'newest' | 'oldest' | 'az' | 'za';
  category: string;
  dateFrom: string | null;
  dateTo: string | null;
}

@Component({
  selector: 'app-forum-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatChipsModule,
    MatPaginatorModule
  ],
  templateUrl: './forum-list.component.html',
  styleUrls: ['./forum-list.component.css']
})
export class ForumListComponent implements OnInit {
  forums: Forum[] = [];
  isLoading = false;
  searchTerm = '';
  selectedCategory = '';
  sortOrder: 'newest' | 'oldest' | 'az' | 'za' = 'newest';
  dateFrom: string | null = null;
  dateTo: string | null = null;
  searchColumns: string[] = ['title', 'description', 'author'];
  showAdvancedSearch = false;
  
  pageIndex = 0;
  pageSize = 5;
  pageSizeOptions = [5, 10, 25, 50];
  totalElements = 0;
  totalPages = 0;
  useServerPagination = false;
  Math = Math;

  categories = [
    { value: '', label: 'Toutes les catégories' },
    { value: 'transplantation-renale', label: 'Transplantation Rénale' },
    { value: 'dialyse-peritoneale', label: 'Dialyse Péritonéale' },
    { value: 'hemodialyse', label: 'Hémodialyse' },
    { value: 'nutrition-pediatrque', label: 'Nutrition Pédiatrique' },
    { value: 'experiences-parents', label: 'Expériences Parents' },
    { value: 'soutien-psychologique', label: 'Soutien Psychologique' },
    { value: 'actualites-medicales', label: 'Actualités Médicales' }
  ];

  sortOptions = [
    { value: 'newest', label: 'Plus récents', icon: 'arrow_downward' },
    { value: 'oldest', label: 'Plus anciens', icon: 'arrow_upward' },
    { value: 'az', label: 'A - Z', icon: 'sort_by_alpha' },
    { value: 'za', label: 'Z - A', icon: 'sort_by_alpha' }
  ];

  columnOptions = [
    { value: 'title', label: 'Titre' },
    { value: 'description', label: 'Description' },
    { value: 'author', label: 'Auteur' },
    { value: 'category', label: 'Catégorie' }
  ];

constructor(
    private router: Router,
    private forumService: ForumService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadForums();
  }

  loadForums(): void {
    this.isLoading = true;
    this.pageIndex = 0;
    
    if (this.useServerPagination) {
      this.loadForumsFromServer();
    } else {
      this.forumService.getAllPosts().subscribe({
        next: (posts) => {
          this.forums = posts.map((post: any) => ({
            id: post.id?.toString() || '',
            title: post.title || post.subject || 'Sans titre',
            description: post.content || '',
            category: post.category || 'transplantation-renale',
            author: post.authorName || post.author?.userName || 'Anonyme',
            createdAt: post.datePost ? new Date(post.datePost) : new Date(),
            postsCount: post.comments?.length || 0,
            isPrivate: post.isPrivate || false,
            allowAnonymous: post.allowAnonymous || true,
            requireModeration: post.requireModeration || false
          }));
          this.totalElements = this.forums.length;
          this.totalPages = Math.ceil(this.totalElements / this.pageSize);
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error loading posts:', err);
          this.forums = this.getMockForums();
          this.totalElements = this.forums.length;
          this.totalPages = Math.ceil(this.totalElements / this.pageSize);
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
    }
}

  loadForumsFromServer(): void {
    this.forumService.getPostsWithPagination(
      this.pageIndex,
      this.pageSize
    ).subscribe({
      next: (response: any) => {
        this.forums = response.content || [];
        this.totalElements = response.totalElements || 0;
        this.totalPages = response.totalPages || 0;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error loading forums:', err);
        this.forums = this.getMockForums();
        this.isLoading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  getFilteredForumsLength(): number {
    return this.getFilteredForums().length;
  }

  getTotalPages(): number {
    return Math.ceil(this.getFilteredForumsLength() / this.pageSize);
  }

  goToPage(page: number): void {
    this.pageIndex = page;
    this.cdr.detectChanges();
    if (this.useServerPagination) {
      this.loadForumsFromServer();
    }
  }

  nextPage(): void {
    if (this.pageIndex < this.getTotalPages() - 1) {
      this.goToPage(this.pageIndex + 1);
    }
  }

  previousPage(): void {
    if (this.pageIndex > 0) {
      this.goToPage(this.pageIndex - 1);
    }
  }

  firstPage(): void {
    this.goToPage(0);
  }

  lastPage(): void {
    this.goToPage(this.getTotalPages() - 1);
  }

  private getMockForums(): Forum[] {
    // Données mock depuis localStorage ou données par défaut
    const savedForums = JSON.parse(localStorage.getItem('pediatric_forum_posts') || '[]');
    
    if (savedForums.length > 0) {
      return savedForums.map((post: any) => ({
        id: post.id?.toString() || '',
        title: post.title || 'Sans titre',
        description: post.description || '',
        category: post.category || 'transplantation-renale',
        author: post.authorName || 'Anonyme',
        createdAt: post.datePost ? new Date(post.datePost) : new Date(),
        postsCount: post.comments?.length || 0,
        isPrivate: post.isPrivate || false,
        allowAnonymous: post.allowAnonymous || true,
        requireModeration: post.requireModeration || false
      }));
    }
    
    // Données mock par défaut
    return [
      {
        id: '1',
        title: 'Transplantation rénale chez les enfants',
        description: 'Forum dédié aux parents et patients sur la transplantation rénale pédiatrique',
        category: 'transplantation-renale',
        author: 'Dr. Martin',
        createdAt: new Date('2024-01-15'),
        postsCount: 45,
        isPrivate: false,
        allowAnonymous: true,
        requireModeration: true
      },
      {
        id: '2',
        title: 'Expériences de dialyse à domicile',
        description: 'Partagez vos expériences avec la dialyse péritonéale à domicile',
        category: 'dialyse-peritoneale',
        author: 'Sophie L.',
        createdAt: new Date('2024-02-20'),
        postsCount: 23,
        isPrivate: false,
        allowAnonymous: true,
        requireModeration: false
      },
      {
        id: '3',
        title: 'Nutrition et insuffisance rénale',
        description: 'Conseils nutritionnels pour les enfants atteints d\'insuffisance rénale',
        category: 'nutrition-pediatrque',
        author: 'Dr. Dubois',
        createdAt: new Date('2024-03-10'),
        postsCount: 67,
        isPrivate: false,
        allowAnonymous: false,
        requireModeration: true
      }
    ];
  }

  createForum(): void {
    this.router.navigate(['/forum/create']);
  }

  createPost(): void {
    this.router.navigate(['/forum/post-create']);
  }

  viewForum(id: string): void {
    this.router.navigate([`/forum/forum/${id}`]);
  }

  getCategoryLabel(category: string): string {
    const cat = this.categories.find(c => c.value === category);
    return cat ? cat.label : 'Non spécifiée';
  }

  getFilteredForums(): Forum[] {
    let filtered = this.forums;
    
    if (this.searchTerm && this.searchColumns.length > 0) {
      const keyword = this.searchTerm.toLowerCase();
      filtered = filtered.filter(forum => {
        return this.searchColumns.some(column => {
          switch (column) {
            case 'title':
              return forum.title.toLowerCase().includes(keyword);
            case 'description':
              return forum.description.toLowerCase().includes(keyword);
            case 'author':
              return forum.author.toLowerCase().includes(keyword);
            case 'category':
              return forum.category.toLowerCase().includes(keyword);
            default:
              return false;
          }
        });
      });
    }
    
    if (this.selectedCategory) {
      filtered = filtered.filter(forum => forum.category === this.selectedCategory);
    }
    
    if (this.dateFrom) {
      const fromDate = new Date(this.dateFrom);
      filtered = filtered.filter(forum => new Date(forum.createdAt) >= fromDate);
    }
    
    if (this.dateTo) {
      const toDate = new Date(this.dateTo);
      filtered = filtered.filter(forum => new Date(forum.createdAt) <= toDate);
    }
    
    filtered = this.sortForums(filtered);
    
    return filtered;
  }

  sortForums(forums: Forum[]): Forum[] {
    return [...forums].sort((a, b) => {
      switch (this.sortOrder) {
        case 'newest':
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        case 'oldest':
          return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
        case 'az':
          return a.title.localeCompare(b.title);
        case 'za':
          return b.title.localeCompare(a.title);
        default:
          return 0;
      }
    });
  }

  toggleAdvancedSearch(): void {
    this.showAdvancedSearch = !this.showAdvancedSearch;
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedCategory = '';
    this.sortOrder = 'newest';
    this.dateFrom = null;
    this.dateTo = null;
    this.searchColumns = ['title', 'description', 'author'];
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

  getPageNumbers(): number[] {
    const filteredForums = this.getFilteredForums();
    const totalFilteredPages = Math.ceil(filteredForums.length / this.pageSize);
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(0, this.pageIndex - Math.floor(maxVisible / 2));
    let end = Math.min(totalFilteredPages, start + maxVisible);
    
    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }
    
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }
}

