import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { DoctorComponent } from './doctor.component';

describe('DoctorComponent', () => {
  let component: DoctorComponent;
  let fixture: ComponentFixture<DoctorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        RouterTestingModule
      ],
      declarations: [DoctorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DoctorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty doctors array', () => {
    expect(component.doctors).toEqual([]);
    expect(component.isLoading).toBeFalsy();
    expect(component.currentUser).toBeDefined();
  });

  it('should load current user on init', () => {
    fixture.detectChanges();
    expect(component.currentUser.name).toBe('Dr. Martin');
    expect(component.currentUser.role).toBe('DOCTOR');
  });

  it('should load doctors on init', () => {
    fixture.detectChanges();
    expect(component.isLoading).toBeTruthy();
  });

  it('should have doctors after loading', (done) => {
    fixture.detectChanges();
    setTimeout(() => {
      expect(component.doctors.length).toBeGreaterThan(0);
      expect(component.isLoading).toBeFalsy();
      done();
    }, 1500);
  });

  it('should have viewDoctorProfile method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewDoctorProfile({ id: 1 });

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/doctor/profile', 1]);
  });

  it('should have contactDoctor method', () => {
    spyOn(console, 'log');

    component.contactDoctor({ name: 'Dr. Martin' });

    expect(console.log).toHaveBeenCalledWith('Contacter le médecin:', 'Dr. Martin');
  });

  it('should have viewAppointments method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewAppointments();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/doctor/appointments']);
  });

  it('should have viewPatients method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewPatients();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/doctor/patients']);
  });

  it('should have viewSchedule method', () => {
    const mockRouter = TestBed.inject(Router);
    spyOn(mockRouter, 'navigate');

    component.viewSchedule();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/front/doctor/schedule']);
  });

  it('should have correct template structure', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    
    expect(compiled.querySelector('.doctor-container')).toBeTruthy();
    expect(compiled.querySelector('.doctor-header')).toBeTruthy();
    expect(compiled.querySelector('.content-section')).toBeTruthy();
  });

  it('should display loading spinner when loading', () => {
    component.isLoading = true;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const loadingSpinner = compiled.querySelector('.loading-spinner');
    
    expect(loadingSpinner).toBeTruthy();
    expect(loadingSpinner?.textContent).toContain('Chargement des médecins');
  });

  it('should display doctor cards when doctors exist', () => {
    component.isLoading = false;
    component.doctors = [
      { id: 1, name: 'Dr. Martin', specialty: 'Néphrologie', email: 'martin@hospital.com', available: true }
    ];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const doctorCard = compiled.querySelector('.doctor-card');
    
    expect(doctorCard).toBeTruthy();
    expect(doctorCard?.textContent).toContain('Dr. Martin');
    expect(doctorCard?.textContent).toContain('Néphrologie');
  });

  it('should display availability status correctly', () => {
    component.isLoading = false;
    component.doctors = [
      { id: 1, name: 'Dr. Martin', specialty: 'Néphrologie', email: 'martin@hospital.com', available: true },
      { id: 2, name: 'Dr. Sophie', specialty: 'Pédiatrie', email: 'sophie@hospital.com', available: false }
    ];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const availabilityElements = compiled.querySelectorAll('.availability');
    
    expect(availabilityElements[0].textContent).toContain('Disponible');
    expect(availabilityElements[1].textContent).toContain('Indisponible');
  });
});
