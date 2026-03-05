import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { LoginComponent } from './login.component';
import { AuthService } from '../../../services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent, // Import standalone component
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        HttpClientTestingModule,
        RouterTestingModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.loginForm).toBeDefined();
    expect(component.isLoading).toBeFalsy();
    expect(component.errorMessage).toBe('');
    expect(component.successMessage).toBe('');
  });

  it('should have correct form fields', () => {
    expect(component.username).toBeDefined();
    expect(component.password).toBeDefined();
  });

  it('should validate username field', () => {
    const usernameControl = component.username;
    
    usernameControl?.setValue('');
    expect(usernameControl?.invalid).toBeTruthy();
    expect(usernameControl?.errors?.['required']).toBeTruthy();

    usernameControl?.setValue('testuser');
    expect(usernameControl?.valid).toBeTruthy();
  });

  it('should validate password field', () => {
    const passwordControl = component.password;
    
    passwordControl?.setValue('');
    expect(passwordControl?.invalid).toBeTruthy();
    expect(passwordControl?.errors?.['required']).toBeTruthy();

    passwordControl?.setValue('123');
    expect(passwordControl?.invalid).toBeTruthy();
    expect(passwordControl?.errors?.['minlength']).toBeTruthy();

    passwordControl?.setValue('123456');
    expect(passwordControl?.valid).toBeTruthy();
  });

  it('should call authService.login on form submit', () => {
    const mockResponse = {
      token: 'test-token',
      username: 'testuser',
      role: 'PATIENT'
    };

    mockAuthService.login.and.returnValue({
      subscribe: (callback: any) => callback.next(mockResponse)
    });

    component.loginForm.patchValue({
      username: 'testuser',
      password: '123456'
    });

    component.onSubmit();

    expect(mockAuthService.login).toHaveBeenCalledWith({
      username: 'testuser',
      password: '123456'
    });
  });

  it('should handle successful login', () => {
    const mockResponse = {
      token: 'test-token',
      username: 'testuser',
      role: 'PATIENT'
    };

    mockAuthService.login.and.returnValue({
      subscribe: (callbacks: any) => {
        callbacks.next(mockResponse);
        return { unsubscribe: () => {} };
      }
    });

    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');
    spyOn(localStorage, 'setItem');

    component.loginForm.patchValue({
      username: 'testuser',
      password: '123456'
    });

    component.onSubmit();

    expect(component.isLoading).toBeFalsy();
    expect(component.successMessage).toBe('Connexion réussie !');
    expect(localStorage.setItem).toHaveBeenCalledWith('token', 'test-token');
    expect(localStorage.setItem).toHaveBeenCalledWith('currentUser', JSON.stringify(mockResponse));

    setTimeout(() => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/home']);
    }, 1000);
  });

  it('should handle login error', () => {
    mockAuthService.login.and.returnValue({
      subscribe: (callbacks: any) => {
        callbacks.error({ status: 401 });
        return { unsubscribe: () => {} };
      }
    });

    component.loginForm.patchValue({
      username: 'testuser',
      password: 'wrongpassword'
    });

    component.onSubmit();

    expect(component.isLoading).toBeFalsy();
    expect(component.errorMessage).toBe('Identifiants incorrects');
    expect(component.successMessage).toBe('');
  });

  it('should not submit if form is invalid', () => {
    spyOn(mockAuthService, 'login');
    
    component.onSubmit();

    expect(mockAuthService.login).not.toHaveBeenCalled();
  });

  it('should navigate to signup', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.goToSignup();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/signup']);
  });

  it('should navigate to forgot password', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.goToForgotPassword();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/forgot-password']);
  });

  it('should set loading state during submission', () => {
    mockAuthService.login.and.returnValue({
      subscribe: () => ({ unsubscribe: () => {} })
    });

    component.loginForm.patchValue({
      username: 'testuser',
      password: '123456'
    });

    component.onSubmit();

    expect(component.isLoading).toBeTruthy();
  });

  it('should clear error message on new submission', () => {
    component.errorMessage = 'Previous error';
    
    mockAuthService.login.and.returnValue({
      subscribe: () => ({ unsubscribe: () => {} })
    });

    component.loginForm.patchValue({
      username: 'testuser',
      password: '123456'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('');
  });
});
