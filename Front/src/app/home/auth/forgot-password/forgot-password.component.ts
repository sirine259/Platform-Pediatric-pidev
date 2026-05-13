import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatProgressSpinnerModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
  forgotForm: FormGroup;
  isLoading = false;
  successMessage = '';
  errorMessage = '';
  showResetForm = false;
  token = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Vérifier si on vient avec un token de réinitialisation
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (this.token) {
      this.showResetForm = true;
    }
  }

  get email() {
    return this.forgotForm.get('email');
  }

  get newPassword() {
    return this.forgotForm.get('newPassword');
  }

  get confirmPassword() {
    return this.forgotForm.get('confirmPassword');
  }

  onSubmit(): void {
    if (this.showResetForm) {
      if (this.forgotForm.invalid || this.newPassword?.value !== this.confirmPassword?.value) {
        return;
      }
    } else {
      if (this.email?.invalid || !this.email?.value) {
        return;
      }
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.showResetForm) {
      this.resetPassword();
    } else {
      this.forgotPassword();
    }
  }

  private forgotPassword(): void {
    const email = this.email?.value;

    const apiUrl = environment.apiUrl + '/api/auth/password/forgot';
    this.http.post(apiUrl, { email }).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.successMessage = response.message || 'Un email de réinitialisation a été envoyé à votre adresse email';
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Erreur lors de l\'envoi de l\'email de réinitialisation';
        console.error('Forgot password error:', error);
      }
    });
  }

  private resetPassword(): void {
    const newPassword = this.newPassword?.value;
    const confirmPassword = this.confirmPassword?.value;

    if (newPassword !== confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas';
      this.isLoading = false;
      return;
    }

    const apiUrl = environment.apiUrl + '/api/auth/password/reset';
    this.http.post(apiUrl, { 
      token: this.token, 
      newPassword 
    }).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.successMessage = response.message || 'Mot de passe réinitialisé avec succès';
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 2000);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Token invalide ou expiré';
        console.error('Reset password error:', error);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/auth/login']);
  }
}
