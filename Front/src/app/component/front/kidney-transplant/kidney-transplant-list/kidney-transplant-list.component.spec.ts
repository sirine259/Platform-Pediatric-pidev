import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { KidneyTransplantListComponent } from './kidney-transplant-list.component';

describe('KidneyTransplantListComponent', () => {
  let component: KidneyTransplantListComponent;
  let fixture: ComponentFixture<KidneyTransplantListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [KidneyTransplantListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KidneyTransplantListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.transplants).toEqual([]);
    expect(component.filteredTransplants).toEqual([]);
    expect(component.isLoading).toBeFalsy();
    expect(component.currentFilter).toBe('');
  });

  it('should have correct methods defined', () => {
    expect(component.loadTransplants).toBeDefined();
    expect(component.filterTransplants).toBeDefined();
    expect(component.selectTransplant).toBeDefined();
    expect(component.viewTransplantDetails).toBeDefined();
    expect(component.goBack).toBeDefined();
  });

  it('should filter transplants correctly', () => {
    component.transplants = [
      { id: 1, patientName: 'Patient A', status: 'COMPLETED' },
      { id: 2, patientName: 'Patient B', status: 'PENDING' }
    ];
    
    component.selectTransplant('Patient A');
    expect(component.filteredTransplants.length).toBe(1);
    expect(component.filteredTransplants[0].patientName).toBe('Patient A');
  });

  it('should load transplants correctly', () => {
    const mockTransplants = [
      { id: 1, patientName: 'Test Patient', status: 'COMPLETED' }
    ];

    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'get').and.callThrough({
      next: (callback: any) => callback(mockTransplants)
    });

    component.loadTransplants();

    expect(mockHttp.get).toHaveBeenCalledWith(
      'http://localhost:8080/api/kidney-transplants'
    );
  });

  it('should have goBack method', () => {
    expect(component.goBack).toBeDefined();
  });
});
