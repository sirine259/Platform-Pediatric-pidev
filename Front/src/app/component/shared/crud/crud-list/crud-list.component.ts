import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface CrudColumn {
  key: string;
  label: string;
  type: 'text' | 'number' | 'date' | 'boolean' | 'badge';
  sortable?: boolean;
  format?: (value: any) => string;
  badgeClass?: (value: any) => string;
  badgeText?: (value: any) => string;
}

export interface CrudConfig {
  title: string;
  createRoute?: string;
  viewRoute?: string;
  editRoute?: string;
  deleteEnabled?: boolean;
  paginationEnabled?: boolean;
  searchEnabled?: boolean;
}

@Component({
  selector: 'app-crud-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, MatButtonModule, MatIconModule, MatTableModule, MatPaginatorModule, MatFormFieldModule, MatInputModule],
  templateUrl: './crud-list.component.html',
  styleUrls: ['./crud-list.component.css']
})
export class CrudListComponent implements OnInit {
  Math = Math;
  @Input() data$: Observable<any[]> = new Observable();
  @Input() config: CrudConfig = {
    title: '',
    deleteEnabled: true,
    paginationEnabled: true,
    searchEnabled: true
  };
  @Input() columns: CrudColumn[] = [];
  @Input() permissions: Record<string, boolean> = {};
  @Input() showAdvancedFeatures: boolean = true;
  @Input() readOnlyFields: string[] = [];

  @Output() delete = new EventEmitter<any>();
  @Output() edit = new EventEmitter<any>();
  @Output() view = new EventEmitter<any>();

  items: any[] = [];
  loading = false;
  error: string = '';
  searchTerm = '';
  currentPage = 1;
  itemsPerPage = 10;
  totalItems = 0;

  ngOnInit(): void {
    this.data$.subscribe({
      next: (data) => {
        this.items = data || [];
        this.totalItems = this.items.length;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading data:', err);
        this.loading = false;
      }
    });
  }

  getNestedValue(obj: any, path: string): any {
    return path.split('.').reduce((current, key) => current && current[key], obj);
  }

  formatValue(value: any, column: CrudColumn): string {
    if (column.format) {
      return column.format(value);
    }
    
    switch (column.type) {
      case 'date':
        return value ? new Date(value).toLocaleDateString('fr-FR') : '';
      case 'boolean':
        return value ? 'Oui' : 'Non';
      default:
        return String(value || '');
    }
  }

  getBadgeClass(value: any, column: CrudColumn): string {
    if (column.badgeClass) {
      return column.badgeClass(value);
    }
    
    if (column.type === 'badge') {
      return 'badge bg-primary';
    }
    return '';
  }

  getBadgeText(value: any, column: CrudColumn): string {
    if (column.badgeText) {
      return column.badgeText(value);
    }
    return String(value);
  }

  isReadOnly(field: string): boolean {
    return this.readOnlyFields.includes(field);
  }

  hasPermission(action: string): boolean {
    return this.permissions[action] !== false;
  }

  getPaginatedItems(): any[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.items.slice(start, end);
  }

  getTotalPages(): number {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }

  onPageChange(page: number): void {
    this.currentPage = page;
  }

  onSearch(): void {
    this.currentPage = 1;
    // Implement search logic
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.onSearch();
  }

  onDelete(item: any): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet élément ?')) {
      this.delete.emit(item);
    }
  }

  onEdit(item: any): void {
    this.edit.emit(item);
  }

  onView(item: any): void {
    this.view.emit(item);
  }

  getPaginationPages(): number[] {
    const pages: number[] = [];
    const totalPages = this.getTotalPages();
    const startPage = Math.max(1, this.currentPage - 2);
    const endPage = Math.min(totalPages, this.currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }
}
