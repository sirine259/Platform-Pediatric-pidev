import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { LoginRequest, LoginResponse, User } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class MockAuthService {
  private users: User[] = [
    {
      id: 1,
      username: 'admin',
      email: 'admin@pidev.com',
      firstName: 'Admin',
      lastName: 'User',
      role: 'ADMIN',
      password: 'password'
    },
    {
      id: 2,
      username: 'doctor',
      email: 'doctor@pidev.com',
      firstName: 'Dr.',
      lastName: 'Martin',
      role: 'DOCTOR',
      password: 'password'
    },
    {
      id: 3,
      username: 'patient',
      email: 'patient@pidev.com',
      firstName: 'Jean',
      lastName: 'Dupont',
      role: 'PATIENT',
      password: 'password'
    }
  ];

  private currentUser: User | null = null;

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return of(credentials).pipe(
      delay(1000),
      map(creds => {
        const user = this.users.find(u => 
          u.username === creds.username && u.password === creds.password
        );
        
        if (!user) {
          throw new Error('Identifiants invalides');
        }

        this.currentUser = user;
        const response: LoginResponse = {
          token: 'mock-token-' + Date.now(),
          role: user.role,
          username: user.username,
          id: user.id,
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName
        };

        localStorage.setItem('token', response.token);
        localStorage.setItem('currentUser', JSON.stringify(user));
        
        return response;
      })
    );
  }

  register(userData: any): Observable<any> {
    return of(userData).pipe(
      delay(1000),
      map(data => {
        // Vérifier si l'utilisateur existe déjà
        if (this.users.find(u => u.username === data.username || u.email === data.email)) {
          throw new Error('Utilisateur déjà existant');
        }

        // Créer le nouvel utilisateur
        const newUser: User = {
          id: this.users.length + 1,
          username: data.username,
          email: data.email,
          firstName: data.firstName,
          lastName: data.lastName,
          role: data.role || 'PATIENT',
          password: data.password
        };

        this.users.push(newUser);
        
        return {
          message: 'Utilisateur créé avec succès',
          user: newUser
        };
      })
    );
  }

  forgotPassword(email: string): Observable<any> {
    return of({}).pipe(
      delay(1000),
      map(() => {
        const user = this.users.find(u => u.email === email);
        if (!user) {
          throw new Error('Email non trouvé');
        }
        return { message: 'Email de réinitialisation envoyé' };
      })
    );
  }

  validateResetToken(token: string): Observable<any> {
    return of({ valid: true }).pipe(delay(500));
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return of({ message: 'Mot de passe réinitialisé' }).pipe(delay(1000));
  }

  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    return of({ message: 'Mot de passe changé' }).pipe(delay(1000));
  }

  verifyToken(token: string): Observable<boolean> {
    return of(token === localStorage.getItem('token')).pipe(delay(500));
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.currentUser = null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUser(): User | null {
    if (!this.currentUser) {
      const userStr = localStorage.getItem('currentUser');
      if (userStr) {
        this.currentUser = JSON.parse(userStr);
      }
    }
    return this.currentUser;
  }

  setCurrentUser(user: User): void {
    this.currentUser = user;
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return token !== null && token !== undefined;
  }

  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'ADMIN';
  }

  isDoctor(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'DOCTOR' || user?.role === 'SURGEON';
  }

  isPatient(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'PATIENT';
  }
}
