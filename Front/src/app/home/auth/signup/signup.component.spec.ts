import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { SignupComponent } from './signup.component';
import { AuthService } from '../../../services/auth.service';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [SignupComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.signupForm).toBeDefined();
    expect(component.isLoading).toBeFalsy();
    expect(component.errorMessage).toBe('');
    expect(component.successMessage).toBe('');
  });

  it('should have correct form fields', () => {
    expect(component.username).toBeDefined();
    expect(component.email).toBeDefined();
    expect(component.password).toBeDefined();
    expect(component.confirmPassword).toBeDefined();
    expect(component.firstName).toBeDefined();
    expect(component.lastName).toBeDefined();
    expect(component.role).toBeDefined();
  });

  it('should validate username field', () => {
    const usernameControl = component.username;
    
    usernameControl?.setValue('');
    expect(usernameControl?.invalid).toBeTruthy();
    expect(usernameControl?.errors?.['required']).toBeTruthy();

    usernameControl?.setValue('testuser');
    expect(usernameControl?.valid).toBeTruthy();
  });

  it('should validate email field', () => {
    const emailControl = component.email;
    
    emailControl?.setValue('');
    expect(emailControl?.invalid).toBeTruthy();
    expect(emailControl?.errors?.['required']).toBeTruthy();

    emailControl?.setValue('invalid-email');
    expect(emailControl?.invalid).toBeTruthy();
    expect(emailControl?.errors?.['email']).toBeTruthy();

    emailControl?.setValue('valid@email.com');
    expect(emailControl?.valid).toBeTruthy();
  });

  it('should validate password fields', () => {
    const passwordControl = component.password;
    const confirmPasswordControl = component.confirmPassword;
    
    passwordControl?.setValue('123');
    expect(passwordControl?.invalid).toBeTruthy();
    expect(passwordControl?.errors?.['minlength']).toBeTruthy();

    passwordControl?.setValue('123456');
    expect(passwordControl?.valid).toBeTruthy();

    confirmPasswordControl?.setValue('different');
    expect(component.signupForm.invalid).toBeTruthy();
  });

  it('should validate required fields', () => {
    const firstNameControl = component.firstName;
    const lastNameControl = component.lastName;
    
    firstNameControl?.setValue('');
    expect(firstNameControl?.invalid).toBeTruthy();
    expect(firstNameControl?.errors?.['required']).toBeTruthy();

    lastNameControl?.setValue('');
    expect(lastNameControl?.invalid).toBeTruthy();
    expect(lastNameControl?.errors?.['required']).toBeTruthy();
  });

  it('should have default role as PATIENT', () => {
    const roleControl = component.role;
    expect(roleControl?.value).toBe('PATIENT');
  });

  it('should call authService.register on form submit', () => {
    mockAuthService.register.and.returnValue({
      subscribe: (callback: any) => callback.next({ message: 'User created' })
    });

    component.signupForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: '123456',
      confirmPassword: '123456',
      firstName: 'Test',
      lastName: 'User',
      role: 'PATIENT'
    });

    component.onSubmit();

    expect(mockAuthService.register).toHaveBeenCalledWith({
      username: 'testuser',
      email: 'test@example.com',
      password: '123456',
      confirmPassword: '123456',
      firstName: 'Test',
      lastName: 'User',
      role: 'PATIENT'
    });
  });

  it('should handle successful registration', () => {
    mockAuthService.register.and.returnValue({
      subscribe: (callbacks: any) => {
        callbacks.next({ message: 'User created successfully' });
        return { unsubscribe: () => {} };
      }
    });

    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.signupForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: '123456',
      confirmPassword: '123456',
      firstName: 'Test',
      lastName: 'User',
      role: 'PATIENT'
    });

    component.onSubmit();

    expect(component.isLoading).toBeFalsy();
    expect(component.successMessage).toBe('Compte créé avec succès!');

    setTimeout(() => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
    }, 2000);
  });

  it('should handle registration error', () => {
    mockAuthService.register.and.returnValue({
      subscribe: (callbacks: any) => {
        callbacks.error({ status: 400 });
        return { unsubscribe: () => {} };
      }
    });

    component.signupForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: '123456',
      confirmPassword: '123456',
      firstName: 'Test',
      lastName: 'User',
      role: 'PATIENT'
    });

    component.onSubmit();

    expect(component.isLoading).toBeFalsy();
    expect(component.errorMessage).toBe('Erreur lors de la création du compte');
    expect(component.successMessage).toBe('');
  });

  it('should not submit if form is invalid', () => {
    spyOn(mockAuthService, 'register');
    
    component.onSubmit();

    expect(mockAuthService.register).not.toHaveBeenCalled();
  });

  it('should navigate to login', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.goToLogin();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
  });

  it('should set loading state during submission', () => {
    mockAuthService.register.and.returnValue({
      subscribe: () => ({ unsubscribe: () => {} })
    });

    component.signupForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: '123456',
      confirmPassword: '123456',
      firstName: 'Test',
      lastName: 'User',
      role: 'PATIENT'
    });

    component.onSubmit();

    expect(component.isLoading).toBeTruthy();
  });

  it('should clear error message on new submission', () => {
    component.errorMessage = 'Previous error';
    
    mockAuthService.register.and.returnValue({
      subscribe: () => ({ unsubscribe: () => {} })
    });

    component.signupForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: '123456',
      confirmPassword: '123456',
      firstName: 'Test',
      lastName: 'User',
      role: 'PATIENT'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('');
  });
});
