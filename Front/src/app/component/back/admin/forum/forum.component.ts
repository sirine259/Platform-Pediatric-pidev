import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-forum',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './forum.component.html',
  styleUrls: ['./forum.component.css']
})
export class ForumComponent implements OnInit {
  posts: any[] = [];
  categories: any[] = [];
  displayedColumns: string[] = ['id', 'title', 'author', 'category', 'date', 'status', 'actions'];
  isLoading = false;
  searchTerm = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadPosts();
    this.loadCategories();
  }

  loadPosts(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.posts = [
        { 
          id: 1, 
          title: 'Questions sur la dialyse pédiatrique', 
          author: 'Parent Dubois',
          category: 'Traitement',
          date: '2024-01-15',
          status: 'published' 
        },
        { 
          id: 2, 
          title: 'Expérience post-transplantation', 
          author: 'Dr. Martin',
          category: 'Témoignages',
          date: '2024-01-14',
          status: 'published' 
        },
        { 
          id: 3, 
          title: 'Conseils alimentaires pour enfants', 
          author: 'Infirmière Sophie',
          category: 'Nutrition',
          date: '2024-01-13',
          status: 'pending' 
        }
      ];
      this.isLoading = false;
    }, 1000);
  }

  loadCategories(): void {
    this.categories = [
      { id: 1, name: 'Traitement', count: 15 },
      { id: 2, name: 'Témoignages', count: 8 },
      { id: 3, name: 'Nutrition', count: 12 },
      { id: 4, name: 'Questions générales', count: 23 }
    ];
  }

  applyFilter(): void {
    // Logique de filtrage
  }

  addPost(): void {
    this.router.navigate(['/admin/forum/add']);
  }

  editPost(id: number): void {
    this.router.navigate([`/admin/forum/edit/${id}`]);
  }

  deletePost(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce post ?')) {
      this.posts = this.posts.filter(post => post.id !== id);
    }
  }

  toggleStatus(post: any): void {
    post.status = post.status === 'published' ? 'pending' : 'published';
  }

  viewDetails(id: number): void {
    this.router.navigate([`/admin/forum/details/${id}`]);
  }

  viewComments(id: number): void {
    this.router.navigate([`/admin/forum/comments/${id}`]);
  }

  getCategoryClass(category: string): string {
    switch (category) {
      case 'Traitement': return 'cat-treatment';
      case 'Témoignages': return 'cat-testimonial';
      case 'Nutrition': return 'cat-nutrition';
      case 'Questions générales': return 'cat-general';
      default: return 'cat-default';
    }
  }

  getStatusClass(status: string): string {
    return status === 'published' ? 'status-published' : 'status-pending';
  }

  get publishedPostsCount(): number {
    return this.posts.filter(post => post.status === 'published').length;
  }

  get pendingPostsCount(): number {
    return this.posts.filter(post => post.status === 'pending').length;
  }
}
