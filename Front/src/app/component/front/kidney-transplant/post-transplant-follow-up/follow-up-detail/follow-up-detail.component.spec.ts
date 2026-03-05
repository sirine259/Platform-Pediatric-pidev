import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { FollowUpDetailComponent } from './follow-up-detail.component';

describe('FollowUpDetailComponent', () => {
  let component: FollowUpDetailComponent;
  let fixture: ComponentFixture<FollowUpDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [FollowUpDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FollowUpDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.followUp).toBeNull();
    expect(component.transplant).toBeNull();
    expect(component.isLoading).toBeFalsy();
    expect(component.followUpId).toBe(0);
    expect(component.transplantId).toBe(0);
  });

  it('should have correct methods defined', () => {
    expect(component.loadFollowUpDetails).toBeDefined();
    expect(component.loadTransplantDetails).toBeDefined();
    expect(component.updateFollowUp).toBeDefined();
    expect(component.deleteFollowUp).toBeDefined();
    expect(component.goBack).toBeDefined();
  });

  it('should load follow-up details correctly', () => {
    const mockFollowUp = {
      id: 1,
      date: '2024-01-20',
      type: 'POST_TRANSPLANT',
      doctor: 'Dr. Smith',
      notes: 'Post-transplant follow-up notes'
    };

    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'get').and.callThrough({
      next: (callback: any) => callback(mockFollowUp)
    });

    component.followUpId = 1;
    component.transplantId = 1;
    component.loadFollowUpDetails();

    expect(mockHttp.get).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants/1/follow-ups/1'
    );
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

  it('should delete follow-up correctly', () => {
    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'delete').and.callThrough({
      next: (callback: any) => callback({ success: true })
    });

    component.followUpId = 1;
    component.transplantId = 1;
    component.deleteFollowUp();

    expect(mockHttp.delete).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants/1/follow-ups/1'
    );
  });

  it('should format date correctly', () => {
    const date = '2024-01-20T10:30:00Z';
    const formatted = component.formatDate(date);
    expect(formatted).toContain('20');
    expect(formatted).toContain('2024');
  });

  it('should have goBack method', () => {
    expect(component.goBack).toBeDefined();
  });
});
