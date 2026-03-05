import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { MockAuthService } from './mock-auth.service';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  role: string;
  username: string;
  id: number;
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
  private useMock = true;

  constructor(private http: HttpClient, private mockAuth: MockAuthService) { }

  private checkBackendAvailability(): boolean {
    // Pour le développement, utiliser le mock
    return !this.useMock;
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    if (this.checkBackendAvailability()) {
      return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login`, credentials);
    }
    return this.mockAuth.login(credentials);
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
  }

  getToken(): string | null {
    return this.mockAuth.getToken();
  }

  getCurrentUser(): User | null {
    return this.mockAuth.getCurrentUser();
  }

  setCurrentUser(user: User): void {
    this.mockAuth.setCurrentUser(user);
  }

  isAuthenticated(): boolean {
    return this.mockAuth.isAuthenticated();
  }

  isAdmin(): boolean {
    return this.mockAuth.isAdmin();
  }

  isDoctor(): boolean {
    return this.mockAuth.isDoctor();
  }

  isPatient(): boolean {
    return this.mockAuth.isPatient();
  }
}
