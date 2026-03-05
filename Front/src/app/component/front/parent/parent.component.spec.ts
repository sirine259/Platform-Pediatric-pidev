import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { ParentComponent } from './parent.component';

describe('ParentComponent', () => {
  let component: ParentComponent;
  let fixture: ComponentFixture<ParentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        RouterTestingModule
      ],
      declarations: [ParentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty arrays', () => {
    expect(component.children).toEqual([]);
    expect(component.notifications).toEqual([]);
    expect(component.isLoading).toBeFalsy();
    expect(component.currentUser).toBeDefined();
  });

  it('should load current user on init', () => {
    fixture.detectChanges();
    expect(component.currentUser.name).toBe('Parent Martin');
    expect(component.currentUser.role).toBe('PARENT');
  });

  it('should load children on init', () => {
    fixture.detectChanges();
    expect(component.isLoading).toBeTruthy();
  });

  it('should have children after loading', (done) => {
    fixture.detectChanges();
    setTimeout(() => {
      expect(component.children.length).toBeGreaterThan(0);
      expect(component.isLoading).toBeFalsy();
      done();
    }, 1500);
  });

  it('should have notifications after loading', () => {
    fixture.detectChanges();
    expect(component.notifications.length).toBeGreaterThan(0);
  });

  it('should have viewChild method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewChild({ id: 1 });

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/parent/child-details', 1]);
  });

  it('should have viewMedicalRecords method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewMedicalRecords({ id: 1 });

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/parent/medical-records', 1]);
  });

  it('should have viewAppointments method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewAppointments();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/parent/appointments']);
  });

  it('should have viewSchoolInfo method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewSchoolInfo();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/parent/school-info']);
  });

  it('should have markNotificationAsRead method', () => {
    const notification = { id: 1, read: false };
    component.notifications = [notification];
    
    component.markNotificationAsRead(notification);

    expect(notification.read).toBeTruthy();
  });

  it('should have correct template structure', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    
    expect(compiled.querySelector('.parent-container')).toBeTruthy();
    expect(compiled.querySelector('.parent-header')).toBeTruthy();
    expect(compiled.querySelector('.content-section')).toBeTruthy();
  });

  it('should display loading spinner when loading', () => {
    component.isLoading = true;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const loadingSpinner = compiled.querySelector('.loading-spinner');
    
    expect(loadingSpinner).toBeTruthy();
    expect(loadingSpinner?.textContent).toContain('Chargement des enfants');
  });

  it('should display child cards when children exist', () => {
    component.isLoading = false;
    component.children = [
      { id: 1, name: 'Pierre Martin', age: 12, class: '6ème', school: 'École Primaire' }
    ];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const childItem = compiled.querySelector('.child-item');
    
    expect(childItem).toBeTruthy();
    expect(childItem?.textContent).toContain('Pierre Martin');
  });
});
