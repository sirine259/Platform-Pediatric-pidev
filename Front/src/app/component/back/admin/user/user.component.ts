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
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-user',
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
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  users: any[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'role', 'status', 'actions'];
  isLoading = false;
  searchTerm = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    // Simulation de données
    setTimeout(() => {
      this.users = [
        { id: 1, name: 'Admin User', email: 'admin@pediatric.com', role: 'administrator', status: 'active' },
        { id: 2, name: 'Dr. Martin', email: 'martin@pediatric.com', role: 'doctor', status: 'active' },
        { id: 3, name: 'Infirmière Sophie', email: 'sophie@pediatric.com', role: 'nurse', status: 'active' },
        { id: 4, name: 'Parent Dubois', email: 'dubois@pediatric.com', role: 'parent', status: 'inactive' }
      ];
      this.isLoading = false;
    }, 1000);
  }

  applyFilter(): void {
    // Logique de filtrage
  }

  addUser(): void {
    this.router.navigate(['/admin/users/add']);
  }

  editUser(id: number): void {
    this.router.navigate([`/admin/users/edit/${id}`]);
  }

  deleteUser(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
      this.users = this.users.filter(user => user.id !== id);
    }
  }

  viewDetails(id: number): void {
    this.router.navigate([`/admin/users/${id}`]);
  }

  editPermissions(id: number): void {
    this.router.navigate([`/admin/users/permissions/${id}`]);
  }

  toggleStatus(user: any): void {
    user.status = user.status === 'active' ? 'inactive' : 'active';
  }

  getRoleClass(role: string): string {
    switch (role) {
      case 'administrator': return 'role-admin';
      case 'doctor': return 'role-doctor';
      case 'nurse': return 'role-nurse';
      case 'parent': return 'role-parent';
      default: return 'role-default';
    }
  }

  getRoleLabel(role: string): string {
    switch (role) {
      case 'administrator': return 'Administrateur';
      case 'doctor': return 'Médecin';
      case 'nurse': return 'Infirmier';
      case 'parent': return 'Parent';
      default: return 'Utilisateur';
    }
  }

  getStatusClass(status: string): string {
    return status === 'active' ? 'status-active' : 'status-inactive';
  }

  get activeUsersCount(): number {
    return this.users.filter(u => u.status === 'active').length;
  }

  get administratorsCount(): number {
    return this.users.filter(u => u.role === 'administrator').length;
  }

  get doctorsCount(): number {
    return this.users.filter(u => u.role === 'doctor').length;
  }

  get totalConnections(): number {
    return this.activeUsersCount * 10;
  }

  get totalPermissions(): number {
    return this.users.length * 3;
  }
}
