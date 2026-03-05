import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { PostCreateComponent } from './post-create.component';

describe('PostCreateComponent', () => {
  let component: PostCreateComponent;
  let fixture: ComponentFixture<PostCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [PostCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.newPost).toBeDefined();
    expect(component.isSubmitting).toBeFalsy();
    expect(component.imagePreview).toBe('');
  });

  it('should have correct methods defined', () => {
    expect(component.onSubmit).toBeDefined();
    expect(component.onImageUpload).toBeDefined();
    expect(component.removeImage).toBeDefined();
    expect(component.goBack).toBeDefined();
  });

  it('should validate form correctly', () => {
    // Test with empty form
    component.newPost = {
      title: '',
      content: '',
      postType: '',
      forumId: ''
    };
    expect(component.isFormValid()).toBeFalsy();

    // Test with valid form
    component.newPost = {
      title: 'Test Title',
      content: 'Test content with enough characters',
      postType: 'FORUM',
      forumId: 'transplant'
    };
    expect(component.isFormValid()).toBeTruthy();
  });

  it('should handle image upload', () => {
    const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
    const mockEvent = {
      target: {
        files: [mockFile]
      }
    };
    
    component.onImageUpload(mockEvent);
    expect(component.imagePreview).toBeDefined();
  });

  it('should remove image', () => {
    component.imagePreview = 'test-preview';
    component.removeImage();
    expect(component.imagePreview).toBe('');
  });

  it('should handle image upload correctly', () => {
    const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
    const mockEvent = {
      target: {
        files: [mockFile]
      }
    };
    
    component.onImageUpload(mockEvent);
    expect(component.imagePreview).toBeDefined();
    expect(component.imagePreview).toContain('data:image');
  });

  it('should remove image correctly', () => {
    component.imagePreview = 'test-preview';
    component.removeImage();
    expect(component.imagePreview).toBe('');
    expect(component.newPost.picture).toBeNull();
  });

  it('should validate form correctly', () => {
    // Test with empty form
    component.newPost = {
      title: '',
      content: '',
      postType: '',
      forumId: '',
      isAnonymous: false
    };
    expect(component.isFormValid()).toBeFalsy();

    // Test with valid form
    component.newPost = {
      title: 'Test Title',
      content: 'Test content with enough characters',
      postType: 'FORUM',
      forumId: 'transplant',
      isAnonymous: false
    };
    expect(component.isFormValid()).toBeTruthy();
  });

  it('should submit form correctly', () => {
    const mockHttp = TestBed.inject(HttpClient);
    spyOn(mockHttp, 'post').and.callThrough({
      next: { subscribe: (callback: any) => callback({}) }
    });

    component.newPost = {
      title: 'Test Post',
      content: 'Test content',
      postType: 'FORUM',
      forumId: 'transplant',
      isAnonymous: false
    };

    component.onSubmit();

    expect(mockHttp.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/forum/posts',
      jasmine.objectContaining({
        title: 'Test Post',
        content: 'Test content',
        postType: 'FORUM',
        forumId: 'transplant',
        isAnonymous: false,
        createdAt: jasmine.any(Date),
        isDeleted: false,
        likeCount: 0,
        viewCount: 0,
        status: 'Pending'
      })
    );
  });

  it('should have goBack method', () => {
    expect(component.goBack).toBeDefined();
  });
});
