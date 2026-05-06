import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar } from '@angular/material/snack-bar';

// Interfaces pour les données
export interface KidneyTransplant {
  id?: string;
  patientName: string;
  patientAge: number;
  diagnosis: string;
  transplantDate: Date;
  donorType: string;
  hospital: string;
  surgeon: string;
  notes: string;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface Patient {
  id: string;
  name: string;
  age: number;
  medicalRecord: string;
}

@Component({
  selector: 'app-kidney-transplant-create',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCardModule
  ],
  templateUrl: './kidney-transplant-create.component.html',
  styleUrls: ['./kidney-transplant-create.component.css']
})
export class KidneyTransplantCreateComponent implements OnInit {
  transplantForm: FormGroup;
  isLoading = false;
  isSubmitting = false;
  
  // Options pour les sélections
  diagnosisOptions = [
    { value: 'insuffisance-renale-chronique', label: 'Insuffisance Rénale Chronique' },
    { value: 'syndrome-nephrotique', label: 'Syndrome Néphrotique' },
    { value: 'maladie-polycystique', label: 'Maladie Polykystique' },
    { value: 'hypoplasie-renale', label: 'Hypoplasie Rénale' },
    { value: 'glomerulonephrite', label: 'Glomérulonéphrite' },
    { value: 'reflux-vesico-ureteral', label: 'Reflux Vésico-urétéral' },
    { value: 'autre', label: 'Autre' }
  ];

  donorTypeOptions = [
    { value: 'vivant-donneur-vivant', label: 'Donneur Vivant (parent/frère)' },
    { value: 'decede-donneur-decede', label: 'Donneur Décédé' },
    { value: 'vivant-donneur-non-apparente', label: 'Donneur Vivant (non apparenté)' }
  ];

  hospitalOptions = [
    { value: 'hopital-enfant-paris', label: 'Hôpital Necker-Enfants Malades (Paris)' },
    { value: 'hopital-rouen', label: 'CHU de Rouen' },
    { value: 'hopital-lyon', label: 'Hospices Civils de Lyon' },
    { value: 'hopital-marseille', label: 'AP-HM Marseille' },
    { value: 'hopital-lille', label: 'CHU de Lille' },
    { value: 'autre', label: 'Autre' }
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.transplantForm = this.fb.group({
      patientName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      patientAge: ['', [Validators.required, Validators.min(0), Validators.max(18), Validators.pattern('^[0-9]+$')]],
      diagnosis: ['', Validators.required],
      transplantDate: ['', Validators.required],
      donorType: ['', Validators.required],
      hospital: ['', Validators.required],
      surgeon: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      notes: ['', [Validators.maxLength(500)]],
      // Champs additionnels pour plus de détails
      bloodType: [''],
      hlaMatch: [''],
      immunosuppressiveTherapy: [''],
      followUpSchedule: ['']
    });
  }

  ngOnInit(): void {
    // Initialisation du formulaire avec la date du jour par défaut
    const today = new Date();
    this.transplantForm.patchValue({
      transplantDate: today,
      bloodType: 'O+',
      immunosuppressiveTherapy: 'Standard'
    });
  }

  // Getters pour faciliter l'accès aux contrôles
  get patientName() { return this.transplantForm.get('patientName'); }
  get patientAge() { return this.transplantForm.get('patientAge'); }
  get diagnosis() { return this.transplantForm.get('diagnosis'); }
  get transplantDate() { return this.transplantForm.get('transplantDate'); }
  get donorType() { return this.transplantForm.get('donorType'); }
  get hospital() { return this.transplantForm.get('hospital'); }
  get surgeon() { return this.transplantForm.get('surgeon'); }
  get notes() { return this.transplantForm.get('notes'); }

  onSubmit(): void {
    if (this.transplantForm.invalid) {
      this.transplantForm.markAllAsTouched();
      this.showErrorMessage('Veuillez corriger les erreurs dans le formulaire');
      return;
    }

    this.isSubmitting = true;
    this.isLoading = true;
    
    const transplantData: KidneyTransplant = {
      ...this.transplantForm.value,
      patientAge: Number(this.transplantForm.value.patientAge),
      transplantDate: new Date(this.transplantForm.value.transplantDate),
      createdAt: new Date(),
      updatedAt: new Date()
    };

    // Simulation de sauvegarde avec service
    this.saveTransplant(transplantData);
  }

  private saveTransplant(transplantData: KidneyTransplant): void {
    // Simulation d'appel API
    setTimeout(() => {
      try {
        // Simuler une sauvegarde réussie
        console.log('Kidney Transplant créée:', transplantData);
        
        // Stocker dans localStorage pour la démo
        const existingTransplants = JSON.parse(localStorage.getItem('kidneyTransplants') || '[]');
        transplantData.id = Date.now().toString();
        existingTransplants.push(transplantData);
        localStorage.setItem('kidneyTransplants', JSON.stringify(existingTransplants));
        
        this.isSubmitting = false;
        this.isLoading = false;
        
        this.showSuccessMessage('Kidney Transplantation créée avec succès!');
        
        // Redirection vers la liste après un court délai
        setTimeout(() => {
          this.router.navigate(['/kidney-transplant/list']);
        }, 1500);
        
      } catch (error) {
        console.error('Erreur lors de la sauvegarde:', error);
        this.isSubmitting = false;
        this.isLoading = false;
        this.showErrorMessage('Erreur lors de la sauvegarde. Veuillez réessayer.');
      }
    }, 2000);
  }

  cancel(): void {
    if (this.transplantForm.dirty) {
      if (confirm('Êtes-vous sûr de vouloir annuler? Les données non sauvegardées seront perdues.')) {
        this.router.navigate(['/kidney-transplant/list']);
      }
    } else {
      this.router.navigate(['/kidney-transplant/list']);
    }
  }

  resetForm(): void {
    if (confirm('Êtes-vous sûr de vouloir réinitialiser le formulaire?')) {
      this.transplantForm.reset();
      const today = new Date();
      this.transplantForm.patchValue({
        transplantDate: today,
        bloodType: 'O+',
        immunosuppressiveTherapy: 'Standard'
      });
    }
  }

  // Méthodes pour les messages
  private showSuccessMessage(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['success-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  private showErrorMessage(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 5000,
      panelClass: ['error-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  // Validation personnalisée
  validateAge(): boolean {
    const age = this.transplantForm.get('patientAge')?.value;
    return age >= 0 && age <= 18;
  }

  // Méthode pour calculer l'âge de la transplantation
  calculateTransplantAge(transplantDate: Date): number {
    const today = new Date();
    const diffTime = Math.abs(today.getTime() - transplantDate.getTime());
    return Math.floor(diffTime / (1000 * 60 * 60 * 24)); // jours
  }
}
