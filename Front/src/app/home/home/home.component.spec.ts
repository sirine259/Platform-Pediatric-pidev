import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { HomeComponent } from './home.component';
import { AuthService } from '../../services/auth.service';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'logout']);

    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        RouterTestingModule
      ],
      declarations: [HomeComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with authentication check', () => {
    expect(component.isAuthenticated).toBeDefined();
    expect(mockAuthService.isAuthenticated).toHaveBeenCalled();
  });

  it('should call logout method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.logout();

    expect(mockAuthService.logout).toHaveBeenCalled();
    expect(component.isAuthenticated).toBeFalsy();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
  });

  it('should have correct template structure', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    
    expect(compiled.querySelector('.home-container')).toBeTruthy();
    expect(compiled.querySelector('.hero-section')).toBeTruthy();
    expect(compiled.querySelector('.features-section')).toBeTruthy();
  });

  it('should display user status bar when authenticated', () => {
    component.isAuthenticated = true;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const userStatusBar = compiled.querySelector('.user-status-bar');
    
    expect(userStatusBar).toBeTruthy();
    expect(userStatusBar?.textContent).toContain('Bienvenue sur la plateforme médicale pédiatrique');
  });

  it('should hide user status bar when not authenticated', () => {
    component.isAuthenticated = false;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const userStatusBar = compiled.querySelector('.user-status-bar');
    
    expect(userStatusBar).toBeFalsy();
  });

  it('should have correct navigation links', () => {
    component.isAuthenticated = true;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const forumLink = compiled.querySelector('button[routerLink="/forum"]');
    const transplantLink = compiled.querySelector('button[routerLink="/kidney-transplant"]');
    
    expect(forumLink).toBeTruthy();
    expect(transplantLink).toBeTruthy();
  });

  it('should redirect to login when not authenticated', () => {
    component.isAuthenticated = false;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const authButtons = compiled.querySelectorAll('button[routerLink="/auth/login"]');
    
    expect(authButtons.length).toBeGreaterThan(0);
  });

  it('should have proper styling classes', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    
    expect(compiled.querySelector('.hero-title')).toBeTruthy();
    expect(compiled.querySelector('.cta-button')).toBeTruthy();
    expect(compiled.querySelector('.features-grid')).toBeTruthy();
  });
});
