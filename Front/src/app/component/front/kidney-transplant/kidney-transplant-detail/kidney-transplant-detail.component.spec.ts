import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { KidneyTransplantDetailComponent } from './kidney-transplant-detail.component';

describe('KidneyTransplantDetailComponent', () => {
  let component: KidneyTransplantDetailComponent;
  let fixture: ComponentFixture<KidneyTransplantDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [KidneyTransplantDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KidneyTransplantDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.transplant).toBeNull();
    expect(component.medicalRecords).toEqual([]);
    expect(component.followUps).toEqual([]);
    expect(component.isLoading).toBeFalsy();
    expect(component.transplantId).toBe(0);
  });

  it('should have correct methods defined', () => {
    expect(component.loadTransplantDetails).toBeDefined();
    expect(component.loadMedicalRecords).toBeDefined();
    expect(component.loadFollowUps).toBeDefined();
    expect(component.createFollowUp).toBeDefined();
    expect(component.updateFollowUp).toBeDefined();
    expect(component.deleteFollowUp).toBeDefined();
    expect(component.goBack).toBeDefined();
  });

  it('should load transplant details correctly', () => {
    const mockTransplant = {
      id: 1,
      patientName: 'Test Patient',
      donorName: 'Test Donor',
      transplantDate: '2024-01-15',
      status: 'COMPLETED',
      medicalRecords: [],
      followUps: []
    };

    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'get').and.callThrough({
      next: (callback: any) => callback(mockTransplant)
    });

    component.transplantId = 1;
    component.loadTransplantDetails();

    expect(mockHttp.get).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants/1'
    );
  });

  it('should load medical records correctly', () => {
    const mockRecords = [
      { id: 1, date: '2024-01-10', type: 'CONSULTATION', doctor: 'Dr. Smith', notes: 'Initial consultation' },
      { id: 2, date: '2024-01-15', type: 'ANALYSE', doctor: 'Dr. Johnson', notes: 'Blood analysis' }
    ];

    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'get').and.callThrough({
      next: (callback: any) => callback(mockRecords)
    });

    component.transplantId = 1;
    component.loadMedicalRecords();

    expect(mockHttp.get).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants/1/medical-records'
    );
  });

  it('should load follow-ups correctly', () => {
    const mockFollowUps = [
      { id: 1, date: '2024-01-20', type: 'POST_TRANSPLANT', doctor: 'Dr. Smith', notes: 'Post-transplant follow-up' },
      { id: 2, date: '2024-01-25', type: 'POST_TRANSPLANT', doctor: 'Dr. Johnson', notes: 'Recovery check' }
    ];

    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'get').and.callThrough({
      next: (callback: any) => callback(mockFollowUps)
    });

    component.transplantId = 1;
    component.loadFollowUps();

    expect(mockHttp.get).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants/1/follow-ups'
    );
  });

  it('should create follow-up correctly', () => {
    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'post').and.callThrough({
      next: (callback: any) => callback({ success: true })
    });

    component.createFollowUp();

    expect(mockHttp.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants/1/follow-ups',
      jasmine.objectContaining({
        date: jasmine.any(Date),
        type: 'POST_TRANSPLANT',
        doctor: 'Dr. Smith',
        notes: jasmine.stringContaining('Post-transplant follow-up')
      })
    );
  });

  it('should have goBack method', () => {
    expect(component.goBack).toBeDefined();
  });
});
