import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-parent',
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
  templateUrl: './parent.component.html',
  styleUrls: ['./parent.component.css']
})
export class ParentComponent implements OnInit {
  parents: any[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'phone', 'childrenCount', 'status', 'actions'];
  isLoading = false;
  searchTerm = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadParents();
  }

  loadParents(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.parents = [
        { 
          id: 1, 
          name: 'Jean Dubois', 
          email: 'dubois@pediatric.com', 
          phone: '06 12 34 56 78',
          childrenCount: 2,
          status: 'active' 
        },
        { 
          id: 2, 
          name: 'Marie Martin', 
          email: 'martin@pediatric.com', 
          phone: '06 98 76 54 32',
          childrenCount: 1,
          status: 'active' 
        },
        { 
          id: 3, 
          name: 'Pierre Bernard', 
          email: 'bernard@pediatric.com', 
          phone: '06 45 67 89 01',
          childrenCount: 3,
          status: 'inactive' 
        }
      ];
      this.isLoading = false;
    }, 1000);
  }

  applyFilter(): void {
    // Logique de filtrage
  }

  addParent(): void {
    this.router.navigate(['/admin/parents/add']);
  }

  editParent(id: number): void {
    this.router.navigate([`/admin/parents/edit/${id}`]);
  }

  deleteParent(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce parent ?')) {
      this.parents = this.parents.filter(parent => parent.id !== id);
    }
  }

  toggleStatus(parent: any): void {
    parent.status = parent.status === 'active' ? 'inactive' : 'active';
  }

  viewDetails(id: number): void {
    this.router.navigate([`/admin/parents/details/${id}`]);
  }

  viewChildren(id: number): void {
    this.router.navigate([`/admin/parents/${id}/children`]);
  }

  getStatusClass(status: string): string {
    return status === 'active' ? 'status-active' : 'status-inactive';
  }

  // Getter properties for template calculations
  get activeParentsCount(): number {
    return this.parents.filter(p => p.status === 'active').length;
  }

  get totalChildrenCount(): number {
    return this.parents.reduce((sum, p) => sum + p.childrenCount, 0);
  }

  get familiesCount(): number {
    return this.parents.filter(p => p.childrenCount > 1).length;
  }

  get monthlyMessagesCount(): number {
    return this.activeParentsCount * 3;
  }
}
