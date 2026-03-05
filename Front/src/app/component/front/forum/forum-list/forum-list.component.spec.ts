import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

import { ForumListComponent } from './forum-list.component';

describe('ForumListComponent', () => {
  let component: ForumListComponent;
  let fixture: ComponentFixture<ForumListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [ForumListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ForumListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.forums).toEqual([]);
    expect(component.filteredForums).toEqual([]);
    expect(component.isLoading).toBeFalsy();
    expect(component.currentFilter).toBe('');
  });

  it('should have correct methods defined', () => {
    expect(component.loadForums).toBeDefined();
    expect(component.filterForums).toBeDefined();
    expect(component.selectForum).toBeDefined();
    expect(component.viewForumDetails).toBeDefined();
    expect(component.goBack).toBeDefined();
  });

  it('should filter forums correctly', () => {
    component.forums = [
      { id: 'transplant', name: 'Transplant Rénaux' },
      { id: 'dialysis', name: 'Dialyse Pédiatrique' }
    ];
    
    component.selectForum('transplant');
    expect(component.filteredForums.length).toBe(1);
    expect(component.filteredForums[0].id).toBe('transplant');
  });

  it('should have goBack method', () => {
    expect(component.goBack).toBeDefined();
  });
});
