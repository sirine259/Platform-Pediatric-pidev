import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

import { ForgotPasswordComponent } from './forgot-password.component';

describe('ForgotPasswordComponent', () => {
  let component: ForgotPasswordComponent;
  let fixture: ComponentFixture<ForgotPasswordComponent>;
  let mockActivatedRoute: ActivatedRoute;
  let mockHttp: any;

  beforeEach(async () => {
    const activatedRouteMock = {
      snapshot: {
        queryParamMap: {
          get: jasmine.createSpy('get').and.returnValue('')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [ForgotPasswordComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ForgotPasswordComponent);
    component = fixture.componentInstance;
    mockActivatedRoute = TestBed.inject(ActivatedRoute);
    mockHttp = TestBed.inject(HttpClient);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.forgotForm).toBeDefined();
    expect(component.isLoading).toBeFalsy();
    expect(component.successMessage).toBe('');
    expect(component.errorMessage).toBe('');
    expect(component.showResetForm).toBeFalsy();
    expect(component.token).toBe('');
  });

  it('should have correct form fields', () => {
    expect(component.email).toBeDefined();
    expect(component.newPassword).toBeDefined();
    expect(component.confirmPassword).toBeDefined();
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
    const newPasswordControl = component.newPassword;
    const confirmPasswordControl = component.confirmPassword;
    
    newPasswordControl?.setValue('123');
    expect(newPasswordControl?.invalid).toBeTruthy();
    expect(newPasswordControl?.errors?.['minlength']).toBeTruthy();

    newPasswordControl?.setValue('123456');
    expect(newPasswordControl?.valid).toBeTruthy();

    confirmPasswordControl?.setValue('different');
    expect(component.forgotForm.invalid).toBeTruthy();
  });

  it('should detect token in route params', () => {
    const mockToken = 'test-reset-token-123';
    spyOn(mockActivatedRoute.snapshot.queryParamMap, 'get').and.returnValue(mockToken);
    
    component.ngOnInit();
    
    expect(component.token).toBe(mockToken);
    expect(component.showResetForm).toBeTruthy();
  });

  it('should call forgotPassword API when in forgot mode', () => {
    spyOn(mockHttp, 'post').and.returnValue({
      subscribe: (callback: any) => callback.next({ message: 'Email sent successfully' })
    });

    component.showResetForm = false;
    component.forgotForm.patchValue({ email: 'test@example.com' });
    
    component.onSubmit();

    expect(mockHttp.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/auth/password/forgot',
      { email: 'test@example.com' }
    );
  });

  it('should call resetPassword API when in reset mode', () => {
    spyOn(mockHttp, 'post').and.returnValue({
      subscribe: (callback: any) => callback.next({ message: 'Password reset successfully' })
    });

    component.showResetForm = true;
    component.token = 'test-token';
    component.forgotForm.patchValue({ 
      newPassword: '123456',
      confirmPassword: '123456'
    });
    
    component.onSubmit();

    expect(mockHttp.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/auth/password/reset',
      { 
        token: 'test-token', 
        newPassword: '123456'
      }
    );
  });

  it('should handle password mismatch', () => {
    component.showResetForm = true;
    component.forgotForm.patchValue({ 
      newPassword: '123456',
      confirmPassword: '654321'
    });
    
    component.onSubmit();

    expect(component.errorMessage).toBe('Les mots de passe ne correspondent pas');
    expect(component.isLoading).toBeFalsy();
  });

  it('should not submit if form is invalid', () => {
    spyOn(mockHttp, 'post');
    
    component.onSubmit();

    expect(mockHttp.post).not.toHaveBeenCalled();
  });

  it('should have goBack method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.goBack();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
  });

  it('should handle API errors gracefully', () => {
    spyOn(mockHttp, 'post').and.callFake(() => {
      throw new Error('API Error');
    });

    component.showResetForm = false;
    component.forgotForm.patchValue({ email: 'test@example.com' });
    
    component.onSubmit();

    expect(component.errorMessage).toBe('Erreur lors de l\'envoi de l\'email de réinitialisation');
    expect(component.isLoading).toBeFalsy();
  });

  it('should redirect to login after successful password reset', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    spyOn(mockHttp, 'post').and.returnValue({
      subscribe: (callbacks: any) => {
        callbacks.next({ message: 'Password reset successfully' });
        return { unsubscribe: () => {} };
      }
    });

    component.showResetForm = true;
    component.token = 'test-token';
    component.forgotForm.patchValue({ 
      newPassword: '123456',
      confirmPassword: '123456'
    });
    
    component.onSubmit();

    expect(component.successMessage).toBeTruthy();
    
    setTimeout(() => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
    }, 2000);
  });

  it('should set loading state during submission', () => {
    spyOn(mockHttp, 'post').and.returnValue({
      subscribe: () => ({ unsubscribe: () => {} })
    });

    component.showResetForm = false;
    component.forgotForm.patchValue({ email: 'test@example.com' });
    
    component.onSubmit();

    expect(component.isLoading).toBeTruthy();
  });

  it('should clear error message on new submission', () => {
    component.errorMessage = 'Previous error';
    
    spyOn(mockHttp, 'post').and.returnValue({
      subscribe: () => ({ unsubscribe: () => {} })
    });

    component.showResetForm = false;
    component.forgotForm.patchValue({ email: 'test@example.com' });
    
    component.onSubmit();

    expect(component.errorMessage).toBe('');
  });
});
