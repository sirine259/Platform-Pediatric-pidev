import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { FollowUpUpdateComponent } from './follow-up-update.component';

describe('FollowUpUpdateComponent', () => {
  let component: FollowUpUpdateComponent;
  let fixture: ComponentFixture<FollowUpUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [FollowUpUpdateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FollowUpUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.followUp).toBeDefined();
    expect(component.isLoading).toBeFalsy();
    expect(component.isSubmitting).toBeFalsy();
    expect(component.followUpId).toBe(0);
    expect(component.transplantId).toBe(0);
  });

  it('should have correct methods defined', () => {
    expect(component.loadFollowUpDetails).toBeDefined();
    expect(component.onSubmit).toBeDefined();
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

  it('should submit form correctly', () => {
    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'put').and.callThrough({
      next: (callback: any) => callback({ success: true })
    });

    component.transplantId = 1;
    component.followUpId = 1;
    component.followUp = {
      date: '2024-01-20',
      type: 'POST_TRANSPLANT',
      doctor: 'Dr. Smith',
      notes: 'Updated follow-up notes'
    };

    component.onSubmit();

    expect(mockHttp.put).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants/1/follow-ups/1',
      jasmine.objectContaining({
        date: '2024-01-20',
        type: 'POST_TRANSPLANT',
        doctor: 'Dr. Smith',
        notes: 'Updated follow-up notes'
      })
    );
  });

  it('should have goBack method', () => {
    expect(component.goBack).toBeDefined();
  });
});
