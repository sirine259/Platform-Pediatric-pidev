import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { KidneyTransplantCreateComponent } from './kidney-transplant-create.component';

describe('KidneyTransplantCreateComponent', () => {
  let component: KidneyTransplantCreateComponent;
  let fixture: ComponentFixture<KidneyTransplantCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        KidneyTransplantCreateComponent,
        ReactiveFormsModule,
        RouterTestingModule,
        NoopAnimationsModule,
        MatSnackBarModule
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KidneyTransplantCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.transplantForm).toBeDefined();
    expect(component.isLoading).toBeFalsy();
    expect(component.isSubmitting).toBeFalsy();
  });

  it('should have all required form fields', () => {
    const form = component.transplantForm;
    expect(form.contains('patientName')).toBeTruthy();
    expect(form.contains('patientAge')).toBeTruthy();
    expect(form.contains('diagnosis')).toBeTruthy();
    expect(form.contains('transplantDate')).toBeTruthy();
    expect(form.contains('donorType')).toBeTruthy();
    expect(form.contains('hospital')).toBeTruthy();
    expect(form.contains('surgeon')).toBeTruthy();
    expect(form.contains('notes')).toBeTruthy();
  });

  it('should validate patientName field', () => {
    const patientNameControl = component.patientName;
    
    // Test required validation
    patientNameControl?.setValue('');
    expect(patientNameControl?.invalid).toBeTruthy();
    expect(patientNameControl?.errors?.['required']).toBeTruthy();

    // Test minlength validation
    patientNameControl?.setValue('a');
    expect(patientNameControl?.invalid).toBeTruthy();
    expect(patientNameControl?.errors?.['minlength']).toBeTruthy();

    // Test maxlength validation
    patientNameControl?.setValue('a'.repeat(51));
    expect(patientNameControl?.invalid).toBeTruthy();
    expect(patientNameControl?.errors?.['maxlength']).toBeTruthy();

    // Test valid input
    patientNameControl?.setValue('Jean Dupont');
    expect(patientNameControl?.valid).toBeTruthy();
  });

  it('should validate patientAge field', () => {
    const patientAgeControl = component.patientAge;
    
    // Test required validation
    patientAgeControl?.setValue('');
    expect(patientAgeControl?.invalid).toBeTruthy();
    expect(patientAgeControl?.errors?.['required']).toBeTruthy();

    // Test min validation
    patientAgeControl?.setValue('-1');
    expect(patientAgeControl?.invalid).toBeTruthy();
    expect(patientAgeControl?.errors?.['min']).toBeTruthy();

    // Test max validation
    patientAgeControl?.setValue('19');
    expect(patientAgeControl?.invalid).toBeTruthy();
    expect(patientAgeControl?.errors?.['max']).toBeTruthy();

    // Test pattern validation
    patientAgeControl?.setValue('abc');
    expect(patientAgeControl?.invalid).toBeTruthy();
    expect(patientAgeControl?.errors?.['pattern']).toBeTruthy();

    // Test valid input
    patientAgeControl?.setValue('5');
    expect(patientAgeControl?.valid).toBeTruthy();
  });

  it('should validate diagnosis field', () => {
    const diagnosisControl = component.diagnosis;
    
    diagnosisControl?.setValue('');
    expect(diagnosisControl?.invalid).toBeTruthy();
    expect(diagnosisControl?.errors?.['required']).toBeTruthy();

    diagnosisControl?.setValue('insuffisance-renale-chronique');
    expect(diagnosisControl?.valid).toBeTruthy();
  });

  it('should validate transplantDate field', () => {
    const transplantDateControl = component.transplantDate;
    
    transplantDateControl?.setValue('');
    expect(transplantDateControl?.invalid).toBeTruthy();
    expect(transplantDateControl?.errors?.['required']).toBeTruthy();

    transplantDateControl?.setValue(new Date());
    expect(transplantDateControl?.valid).toBeTruthy();
  });

  it('should validate surgeon field', () => {
    const surgeonControl = component.surgeon;
    
    // Test required validation
    surgeonControl?.setValue('');
    expect(surgeonControl?.invalid).toBeTruthy();
    expect(surgeonControl?.errors?.['required']).toBeTruthy();

    // Test minlength validation
    surgeonControl?.setValue('a');
    expect(surgeonControl?.invalid).toBeTruthy();
    expect(surgeonControl?.errors?.['minlength']).toBeTruthy();

    // Test valid input
    surgeonControl?.setValue('Dr. Martin');
    expect(surgeonControl?.valid).toBeTruthy();
  });

  it('should validate notes field maxlength', () => {
    const notesControl = component.notes;
    
    // Test maxlength validation
    notesControl?.setValue('a'.repeat(501));
    expect(notesControl?.invalid).toBeTruthy();
    expect(notesControl?.errors?.['maxlength']).toBeTruthy();

    // Test valid input
    notesControl?.setValue('Notes normales');
    expect(notesControl?.valid).toBeTruthy();
  });

  it('should initialize form with default values on ngOnInit', () => {
    component.ngOnInit();
    const form = component.transplantForm;
    
    expect(form.get('transplantDate')?.value).toBeInstanceOf(Date);
    expect(form.get('bloodType')?.value).toBe('O+');
    expect(form.get('immunosuppressiveTherapy')?.value).toBe('Standard');
  });

  it('should have diagnosis options', () => {
    expect(component.diagnosisOptions).toBeDefined();
    expect(component.diagnosisOptions.length).toBeGreaterThan(0);
    expect(component.diagnosisOptions[0]).toHaveProperty('value');
    expect(component.diagnosisOptions[0]).toHaveProperty('label');
  });

  it('should have donor type options', () => {
    expect(component.donorTypeOptions).toBeDefined();
    expect(component.donorTypeOptions.length).toBeGreaterThan(0);
    expect(component.donorTypeOptions[0]).toHaveProperty('value');
    expect(component.donorTypeOptions[0]).toHaveProperty('label');
  });

  it('should have hospital options', () => {
    expect(component.hospitalOptions).toBeDefined();
    expect(component.hospitalOptions.length).toBeGreaterThan(0);
    expect(component.hospitalOptions[0]).toHaveProperty('value');
    expect(component.hospitalOptions[0]).toHaveProperty('label');
  });

  it('should validate age correctly', () => {
    expect(component.validateAge()).toBeFalsy(); // No value set
    
    component.transplantForm.patchValue({ patientAge: '5' });
    expect(component.validateAge()).toBeTruthy();
    
    component.transplantForm.patchValue({ patientAge: '25' });
    expect(component.validateAge()).toBeFalsy();
  });

  it('should calculate transplant age correctly', () => {
    const testDate = new Date();
    testDate.setDate(testDate.getDate() - 10); // 10 days ago
    
    const age = component.calculateTransplantAge(testDate);
    expect(age).toBe(10);
  });

  it('should not submit invalid form', () => {
    spyOn(component, 'showErrorMessage');
    
    component.onSubmit();
    
    expect(component.showErrorMessage).toHaveBeenCalledWith('Veuillez corriger les erreurs dans le formulaire');
    expect(component.isSubmitting).toBeFalsy();
  });

  it('should reset form correctly', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    
    // Set some values
    component.transplantForm.patchValue({
      patientName: 'Test Patient',
      patientAge: '5'
    });
    
    component.resetForm();
    
    expect(component.transplantForm.get('patientName')?.value).toBe('');
    expect(component.transplantForm.get('patientAge')?.value).toBe('');
    expect(component.transplantForm.get('bloodType')?.value).toBe('O+');
    expect(component.transplantForm.get('immunosuppressiveTherapy')?.value).toBe('Standard');
  });

  it('should cancel with confirmation when form is dirty', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    
    // Make form dirty
    component.transplantForm.patchValue({ patientName: 'Test' });
    
    component.cancel();
    
    expect(window.confirm).toHaveBeenCalledWith('Êtes-vous sûr de vouloir annuler? Les données non sauvegardées seront perdues.');
    expect(router.navigate).toHaveBeenCalledWith(['/kidney-transplant/list']);
  });

  it('should cancel without confirmation when form is clean', () => {
    spyOn(window, 'confirm');
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    
    component.cancel();
    
    expect(window.confirm).not.toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/kidney-transplant/list']);
  });
});
