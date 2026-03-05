import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { NurseComponent } from './nurse.component';

describe('NurseComponent', () => {
  let component: NurseComponent;
  let fixture: ComponentFixture<NurseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        RouterTestingModule
      ],
      declarations: [NurseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NurseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty arrays', () => {
    expect(component.patients).toEqual([]);
    expect(component.schedules).toEqual([]);
    expect(component.isLoading).toBeFalsy();
    expect(component.currentUser).toBeDefined();
  });

  it('should load current user on init', () => {
    fixture.detectChanges();
    expect(component.currentUser.name).toBe('Infirmière Dubois');
    expect(component.currentUser.role).toBe('NURSE');
  });

  it('should load patients on init', () => {
    fixture.detectChanges();
    expect(component.isLoading).toBeTruthy();
  });

  it('should have patients after loading', (done) => {
    fixture.detectChanges();
    setTimeout(() => {
      expect(component.patients.length).toBeGreaterThan(0);
      expect(component.isLoading).toBeFalsy();
      done();
    }, 1500);
  });

  it('should have schedules after loading', () => {
    fixture.detectChanges();
    expect(component.schedules.length).toBeGreaterThan(0);
  });

  it('should have viewPatient method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewPatient({ id: 1 });

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/nurse/patient-details', 1]);
  });

  it('should have updateVitals method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.updateVitals({ id: 1 });

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/nurse/vitals', 1]);
  });

  it('should have viewSchedule method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewSchedule();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/nurse/schedule']);
  });

  it('should have viewMedications method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewMedications();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/nurse/medications']);
  });

  it('should have correct template structure', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    
    expect(compiled.querySelector('.nurse-container')).toBeTruthy();
    expect(compiled.querySelector('.nurse-header')).toBeTruthy();
    expect(compiled.querySelector('.content-section')).toBeTruthy();
  });

  it('should display loading spinner when loading', () => {
    component.isLoading = true;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const loadingSpinner = compiled.querySelector('.loading-spinner');
    
    expect(loadingSpinner).toBeTruthy();
    expect(loadingSpinner?.textContent).toContain('Chargement des patients');
  });

  it('should display patient cards when patients exist', () => {
    component.isLoading = false;
    component.patients = [
      { id: 1, name: 'Martin Dubois', age: 12, room: 'A101', condition: 'Stable' }
    ];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const patientItem = compiled.querySelector('.patient-item');
    
    expect(patientItem).toBeTruthy();
    expect(patientItem?.textContent).toContain('Martin Dubois');
    expect(patientItem?.textContent).toContain('A101');
  });

  it('should display schedule items when schedules exist', () => {
    component.isLoading = false;
    component.schedules = [
      { id: 1, date: '2024-03-10', shift: 'Matin', patients: 5 }
    ];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const scheduleItem = compiled.querySelector('.schedule-item');
    
    expect(scheduleItem).toBeTruthy();
    expect(scheduleItem?.textContent).toContain('Matin');
  });
});
