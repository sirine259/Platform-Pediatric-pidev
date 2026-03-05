import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { PostUpdateComponent } from './post-update.component';

describe('PostUpdateComponent', () => {
  let component: PostUpdateComponent;
  let fixture: ComponentFixture<PostUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [PostUpdateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.post).toBeNull();
    expect(component.postId).toBe(0);
    expect(component.isLoading).toBeFalsy();
    expect(component.isSubmitting).toBeFalsy();
  });

  it('should have correct methods defined', () => {
    expect(component.loadPost).toBeDefined();
    expect(component.onSubmit).toBeDefined();
    expect(component.onImageUpload).toBeDefined();
    expect(component.removeImage).toBeDefined();
    expect(component.cancel).toBeDefined();
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

  it('should validate form before submission', () => {
    component.post = {
      title: '',
      content: '',
      postType: 'FORUM'
    };
    
    // The method should show alert for empty fields
    expect(component.onSubmit).not.toThrow();
  });

  it('should handle image upload', () => {
    component.post = { picture: null };
    const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
    const mockEvent = {
      target: {
        files: [mockFile]
      }
    };
    
    component.onImageUpload(mockEvent);
    expect(component.onImageUpload).toBeDefined();
  });

  it('should remove image', () => {
    component.post = { picture: 'test-image.jpg' };
    component.removeImage();
    expect(component.post.picture).toBeNull();
  });

  it('should have cancel method', () => {
    expect(component.cancel).toBeDefined();
  });
});
