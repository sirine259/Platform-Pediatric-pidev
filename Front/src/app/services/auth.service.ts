import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { MockAuthService } from './mock-auth.service';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType?: string;
  role: string;
  username: string;
  id?: number;
  userId?: number;
  email: string;
  firstName: string;
  lastName: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  // Pour le mock uniquement (non utilisé côté backend)
  password?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private currentUser: User | null = null;
  private useMock = false;

  constructor(private http: HttpClient, private mockAuth: MockAuthService) { }

  private checkBackendAvailability(): boolean {
    // Pour le développement, utiliser le mock
    return !this.useMock;
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    if (this.checkBackendAvailability()) {
      return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login`, credentials).pipe(
        map((response) => this.persistAuthSession(response))
      );
    }
    return this.mockAuth.login(credentials).pipe(
      map((response) => this.persistAuthSession(response))
    );
  }

  register(userData: any): Observable<any> {
    if (this.checkBackendAvailability()) {
      return this.http.post(`${this.apiUrl}/api/auth/register`, userData);
    }
    return this.mockAuth.register(userData);
  }

  forgotPassword(email: string): Observable<any> {
    if (this.checkBackendAvailability()) {
      return this.http.post(`${this.apiUrl}/api/auth/password/forgot`, { email });
    }
    return this.mockAuth.forgotPassword(email);
  }

  validateResetToken(token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/auth/password/validate-token?token=${token}`);
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/auth/password/reset`, { token, newPassword });
  }

  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/auth/password/change`, { 
      currentPassword, 
      newPassword 
    });
  }

  verifyToken(token: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/api/auth/verify-token/${token}`);
  }

  logout(): void {
    this.mockAuth.logout();
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
    return token !== null && token !== undefined && token !== '';
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

  private persistAuthSession(response: LoginResponse): LoginResponse {
    const normalizedId = response.userId ?? response.id;
    const normalized: LoginResponse = {
      ...response,
      id: normalizedId
    };
    if (normalized.token) {
      localStorage.setItem('token', normalized.token);
    }
    if (normalizedId !== undefined && normalizedId !== null) {
      const user: User = {
        id: normalizedId,
        username: normalized.username,
        email: normalized.email,
        firstName: normalized.firstName,
        lastName: normalized.lastName,
        role: normalized.role
      };
      this.setCurrentUser(user);
    }
    return normalized;
  }
}
