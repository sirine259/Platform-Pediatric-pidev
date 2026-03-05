import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { FollowUpCreateComponent } from './follow-up-create.component';

describe('FollowUpCreateComponent', () => {
  let component: FollowUpCreateComponent;
  let fixture: ComponentFixture<FollowUpCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [FollowUpCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FollowUpCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.transplantId).toBe(0);
    expect(component.followUp).toBeDefined();
    expect(component.isLoading).toBeFalsy();
    expect(component.isSubmitting).toBeFalsy();
  });

  it('should have correct methods defined', () => {
    expect(component.loadTransplantDetails).toBeDefined();
    expect(component.onSubmit).toBeDefined();
    expect(component.goBack).toBeDefined();
  });

  it('should load transplant details correctly', () => {
    const mockTransplant = {
      id: 1,
      patientName: 'Test Patient',
      donorName: 'Test Donor',
      transplantDate: '2024-01-15',
      status: 'COMPLETED'
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

  it('should submit form correctly', () => {
    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'post').and.callThrough({
      next: (callback: any) => callback({ success: true })
    });

    component.transplantId = 1;
    component.followUp = {
      date: '2024-01-20',
      type: 'POST_TRANSPLANT',
      doctor: 'Dr. Smith',
      notes: 'Post-transplant follow-up notes'
    };

    component.onSubmit();

    expect(mockHttp.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants/1/follow-ups',
      jasmine.objectContaining({
        transplantId: 1,
        date: '2024-01-20',
        type: 'POST_TRANSPLANT',
        doctor: 'Dr. Smith',
        notes: 'Post-transplant follow-up notes'
      })
    );
  });

  it('should have goBack method', () => {
    expect(component.goBack).toBeDefined();
  });
});
