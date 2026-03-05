import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

import { PatientComponent } from './patient.component';

describe('PatientComponent', () => {
  let component: PatientComponent;
  let fixture: ComponentFixture<PatientComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        PatientComponent,
        CommonModule,
        RouterTestingModule,
        MatIconModule,
        MatButtonModule
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty arrays', () => {
    expect(component.medicalRecords).toEqual([]);
    expect(component.appointments).toEqual([]);
    expect(component.isLoading).toBeFalsy();
    expect(component.currentUser).toBeDefined();
  });

  it('should load current user on init', () => {
    fixture.detectChanges();
    expect(component.currentUser.name).toBe('Martin Dubois');
    expect(component.currentUser.role).toBe('PATIENT');
  });

  it('should load medical records on init', () => {
    fixture.detectChanges();
    expect(component.isLoading).toBeTruthy();
  });

  it('should have medical records after loading', (done) => {
    fixture.detectChanges();
    setTimeout(() => {
      expect(component.medicalRecords.length).toBeGreaterThan(0);
      expect(component.isLoading).toBeFalsy();
      done();
    }, 1500);
  });

  it('should have appointments after loading', () => {
    fixture.detectChanges();
    expect(component.appointments.length).toBeGreaterThan(0);
  });

  it('should have viewMedicalRecord method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewMedicalRecord({ id: 1 });

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/patient/medical-record', 1]);
  });

  it('should have bookAppointment method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.bookAppointment();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/patient/book-appointment']);
  });

  it('should have viewProfile method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewProfile();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/patient/profile']);
  });

  it('should have viewTransplantInfo method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewTransplantInfo();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/patient/transplant']);
  });

  it('should have correct template structure', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    
    expect(compiled.querySelector('.patient-container')).toBeTruthy();
    expect(compiled.querySelector('.patient-header')).toBeTruthy();
    expect(compiled.querySelector('.content-section')).toBeTruthy();
  });

  it('should display loading spinner when loading', () => {
    component.isLoading = true;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const loadingSpinner = compiled.querySelector('.loading-spinner');
    
    expect(loadingSpinner).toBeTruthy();
    expect(loadingSpinner?.textContent).toContain('Chargement du dossier');
  });

  it('should display patient cards when medical records exist', () => {
    component.isLoading = false;
    component.medicalRecords = [
      { id: 1, date: '2024-01-15', type: 'Consultation', doctor: 'Dr. Martin', status: 'Completed' }
    ];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const recordItem = compiled.querySelector('.record-item');
    
    expect(recordItem).toBeTruthy();
    expect(recordItem?.textContent).toContain('Consultation');
  });
});
