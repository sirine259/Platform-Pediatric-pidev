import { Component, OnInit } from '@angular/core';
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

@Component({
  selector: 'app-forum-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule
  ],
  templateUrl: './forum-list.component.html',
  styleUrls: ['./forum-list.component.css']
})
export class ForumListComponent implements OnInit {
  forums: Forum[] = [];
  isLoading = false;
  searchTerm = '';
  selectedCategory = '';

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

  constructor(
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadForums();
  }

  loadForums(): void {
    this.isLoading = true;
    
    // Simulation de chargement avec données mock
    setTimeout(() => {
      this.forums = this.getMockForums();
      this.isLoading = false;
    }, 1000);
  }

  private getMockForums(): Forum[] {
    // Données mock depuis localStorage ou données par défaut
    const savedForums = JSON.parse(localStorage.getItem('forums') || '[]');
    
    if (savedForums.length > 0) {
      return savedForums;
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
    
    if (this.searchTerm) {
      filtered = filtered.filter(forum => 
        forum.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        forum.description.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
    
    if (this.selectedCategory) {
      filtered = filtered.filter(forum => forum.category === this.selectedCategory);
    }
    
    return filtered;
  }
}
