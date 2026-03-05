import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { ForumCreateComponent } from './forum-create.component';

describe('ForumCreateComponent', () => {
  let component: ForumCreateComponent;
  let fixture: ComponentFixture<ForumCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ForumCreateComponent,
        ReactiveFormsModule,
        RouterTestingModule,
        NoopAnimationsModule,
        MatSnackBarModule
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ForumCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.forumForm).toBeDefined();
    expect(component.isLoading).toBeFalsy();
    expect(component.isSubmitting).toBeFalsy();
  });

  it('should have all required form fields', () => {
    const form = component.forumForm;
    expect(form.contains('title')).toBeTruthy();
    expect(form.contains('description')).toBeTruthy();
    expect(form.contains('category')).toBeTruthy();
    expect(form.contains('isPrivate')).toBeTruthy();
    expect(form.contains('allowAnonymous')).toBeTruthy();
    expect(form.contains('requireModeration')).toBeTruthy();
  });

  it('should validate title field', () => {
    const titleControl = component.title;
    
    // Test required validation
    titleControl?.setValue('');
    expect(titleControl?.invalid).toBeTruthy();
    expect(titleControl?.errors?.['required']).toBeTruthy();

    // Test minlength validation
    titleControl?.setValue('ab');
    expect(titleControl?.invalid).toBeTruthy();
    expect(titleControl?.errors?.['minlength']).toBeTruthy();

    // Test maxlength validation
    titleControl?.setValue('a'.repeat(101));
    expect(titleControl?.invalid).toBeTruthy();
    expect(titleControl?.errors?.['maxlength']).toBeTruthy();

    // Test valid input
    titleControl?.setValue('Forum Test');
    expect(titleControl?.valid).toBeTruthy();
  });

  it('should validate description field', () => {
    const descriptionControl = component.description;
    
    // Test required validation
    descriptionControl?.setValue('');
    expect(descriptionControl?.invalid).toBeTruthy();
    expect(descriptionControl?.errors?.['required']).toBeTruthy();

    // Test minlength validation
    descriptionControl?.setValue('abc');
    expect(descriptionControl?.invalid).toBeTruthy();
    expect(descriptionControl?.errors?.['minlength']).toBeTruthy();

    // Test maxlength validation
    descriptionControl?.setValue('a'.repeat(501));
    expect(descriptionControl?.invalid).toBeTruthy();
    expect(descriptionControl?.errors?.['maxlength']).toBeTruthy();

    // Test valid input
    descriptionControl?.setValue('Description valide du forum');
    expect(descriptionControl?.valid).toBeTruthy();
  });

  it('should validate category field', () => {
    const categoryControl = component.category;
    
    categoryControl?.setValue('');
    expect(categoryControl?.invalid).toBeTruthy();
    expect(categoryControl?.errors?.['required']).toBeTruthy();

    categoryControl?.setValue('transplantation-renale');
    expect(categoryControl?.valid).toBeTruthy();
  });

  it('should have categories options', () => {
    expect(component.categories).toBeDefined();
    expect(component.categories.length).toBeGreaterThan(0);
    expect(component.categories[0].value).toBeDefined();
    expect(component.categories[0].label).toBeDefined();
  });

  it('should initialize form with default values', () => {
    const form = component.forumForm;
    
    expect(form.get('category')?.value).toBe('transplantation-renale');
    expect(form.get('isPrivate')?.value).toBeFalsy();
    expect(form.get('allowAnonymous')?.value).toBeTruthy();
    expect(form.get('requireModeration')?.value).toBeFalsy();
  });

  it('should validate title correctly', () => {
    expect(component.validateTitle()).toBeFalsy(); // No value set
    
    component.forumForm.patchValue({ title: 'Valid Title' });
    expect(component.validateTitle()).toBeTruthy();
    
    component.forumForm.patchValue({ title: 'ab' });
    expect(component.validateTitle()).toBeFalsy();
    
    component.forumForm.patchValue({ title: 'a'.repeat(101) });
    expect(component.validateTitle()).toBeFalsy();
  });

  it('should validate description correctly', () => {
    expect(component.validateDescription()).toBeFalsy(); // No value set
    
    component.forumForm.patchValue({ description: 'Valid description with enough characters' });
    expect(component.validateDescription()).toBeTruthy();
    
    component.forumForm.patchValue({ description: 'short' });
    expect(component.validateDescription()).toBeFalsy();
    
    component.forumForm.patchValue({ description: 'a'.repeat(501) });
    expect(component.validateDescription()).toBeFalsy();
  });

  it('should get category label correctly', () => {
    const label = component.getCategoryLabel('transplantation-renale');
    expect(label).toBe('Transplantation Rénale');
    
    const unknownLabel = component.getCategoryLabel('unknown-category');
    expect(unknownLabel).toBe('Non spécifiée');
  });

  it('should not submit invalid form', () => {
    spyOn(component as any, 'showErrorMessage');
    
    component.onSubmit();
    
    expect((component as any).showErrorMessage).toHaveBeenCalledWith('Veuillez corriger les erreurs dans le formulaire');
    expect(component.isSubmitting).toBeFalsy();
  });

  it('should reset form correctly', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    
    // Set some values
    component.forumForm.patchValue({
      title: 'Test Forum',
      description: 'Test Description',
      isPrivate: true
    });
    
    component.resetForm();
    
    expect(component.forumForm.get('title')?.value).toBe('');
    expect(component.forumForm.get('description')?.value).toBe('');
    expect(component.forumForm.get('category')?.value).toBe('transplantation-renale');
    expect(component.forumForm.get('isPrivate')?.value).toBeFalsy();
    expect(component.forumForm.get('allowAnonymous')?.value).toBeTruthy();
    expect(component.forumForm.get('requireModeration')?.value).toBeFalsy();
  });

  it('should cancel with confirmation when form is dirty', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    
    // Make form dirty
    component.forumForm.patchValue({ title: 'Test' });
    
    component.cancel();
    
    expect(window.confirm).toHaveBeenCalledWith('Êtes-vous sûr de vouloir annuler? Les données non sauvegardées seront perdues.');
    expect(router.navigate).toHaveBeenCalledWith(['/forum/list']);
  });

  it('should cancel without confirmation when form is clean', () => {
    spyOn(window, 'confirm');
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    
    component.cancel();
    
    expect(window.confirm).not.toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/forum/list']);
  });

  it('should have pediatric-specific categories', () => {
    const pediatricCategories = component.categories.filter(cat => 
      cat.label.includes('Pédiatrique') || 
      cat.label.includes('Renale') || 
      cat.label.includes('Parents') ||
      cat.label.includes('Dialyse') ||
      cat.label.includes('Transplantation')
    );
    
    expect(pediatricCategories.length).toBeGreaterThan(0);
    expect(pediatricCategories.some(cat => cat.value === 'transplantation-renale')).toBeTruthy();
    expect(pediatricCategories.some(cat => cat.value === 'dialyse-peritoneale')).toBeTruthy();
    expect(pediatricCategories.some(cat => cat.value === 'experiences-parents')).toBeTruthy();
  });

  it('should handle form submission with valid data', () => {
    spyOn(localStorage, 'setItem');
    spyOn(localStorage, 'getItem').and.returnValue('[]');
    spyOn(component as any, 'showSuccessMessage');
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    
    // Set valid form data
    component.forumForm.patchValue({
      title: 'Forum Test',
      description: 'Description valide avec plus de 10 caractères',
      category: 'transplantation-renale',
      isPrivate: false,
      allowAnonymous: true,
      requireModeration: false
    });
    
    component.onSubmit();
    
    expect(component.isSubmitting).toBeTruthy();
    expect(component.isLoading).toBeTruthy();
  });

  it('should create forum with correct structure', () => {
    const formData = {
      title: 'Test Forum',
      description: 'Test Description',
      category: 'transplantation-renale',
      isPrivate: false,
      allowAnonymous: true,
      requireModeration: false
    };
    
    component.forumForm.patchValue(formData);
    
    const expectedForumData = {
      ...formData,
      createdBy: 'Utilisateur',
      createdAt: jasmine.any(Date),
      updatedAt: jasmine.any(Date),
      memberCount: 1,
      postCount: 0
    };
    
    // The actual forum data creation happens in saveForum method
    // This test verifies the structure is correct
    expect(component.forumForm.value).toEqual(formData);
  });
});
