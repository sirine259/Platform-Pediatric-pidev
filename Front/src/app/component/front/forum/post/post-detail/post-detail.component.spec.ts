import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { PostDetailComponent } from './post-detail.component';

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [PostDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.post).toBeNull();
    expect(component.comments).toEqual([]);
    expect(component.isLoading).toBeFalsy();
    expect(component.postId).toBe(0);
    expect(component.newComment).toBe('');
    expect(component.isSubmittingComment).toBeFalsy();
  });

  it('should have correct methods defined', () => {
    expect(component.loadPostDetails).toBeDefined();
    expect(component.loadComments).toBeDefined();
    expect(component.addComment).toBeDefined();
    expect(component.updatePost).toBeDefined();
    expect(component.deletePost).toBeDefined();
    expect(component.likePost).toBeDefined();
    expect(component.deleteComment).toBeDefined();
    expect(component.goBack).toBeDefined();
  });

  it('should return correct post type labels', () => {
    expect(component.getPostTypeLabel('FORUM')).toBe('Forum');
    expect(component.getPostTypeLabel('MEDICAL_UPDATE')).toBe('Mise à jour médicale');
    expect(component.getPostTypeLabel('FOLLOW_UP')).toBe('Suivi');
    expect(component.getPostTypeLabel('ANNOUNCEMENT')).toBe('Annonce');
    expect(component.getPostTypeLabel('UNKNOWN')).toBe('Autre');
  });

  it('should return correct post type colors', () => {
    expect(component.getPostTypeColor('FORUM')).toBe('primary');
    expect(component.getPostTypeColor('MEDICAL_UPDATE')).toBe('success');
    expect(component.getPostTypeColor('FOLLOW_UP')).toBe('warning');
    expect(component.getPostTypeColor('ANNOUNCEMENT')).toBe('info');
    expect(component.getPostTypeColor('UNKNOWN')).toBe('secondary');
  });

  it('should format time ago correctly', () => {
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    expect(component.getTimeAgo(yesterday.toISOString())).toBe('Hier');

    const lastWeek = new Date();
    lastWeek.setDate(lastWeek.getDate() - 5);
    expect(component.getTimeAgo(lastWeek.toISOString())).toContain('Il y a');
  });

  it('should validate comment before adding', () => {
    component.newComment = '';
    expect(component.addComment).not.toThrow();
    // The method should show alert for empty comment
  });

  it('should have goBack method', () => {
    expect(component.goBack).toBeDefined();
  });
});
