import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { KidneyTransplant, PostTransplantFollowUp } from '../models/kidney-transplant.model';

@Injectable({
  providedIn: 'root'
})
export class KidneyTransplantService {
  private apiUrl = environment.apiUrl;
  
  // Mock data pour tester pendant que le backend n'est pas disponible
  private mockTransplants: KidneyTransplant[] = [
    {
      id: 1,
      transplantDate: "2024-01-15T10:30:00Z",
      status: "COMPLETED",
      medicalRecord: {
        id: 1,
        patient: {
          firstName: "Martin",
          lastName: "Dubois",
          medicalRecordNumber: "MR-2024-001"
        }
      },
      doctor: {
        id: 1,
        user: {
          firstName: "Dr.",
          lastName: "Sophie Martin"
        },
        specialization: "Néphrologie pédiatrique",
        licenseNumber: "MED-12345"
      },
      postTransplantFollowUps: [
        {
          id: 1,
          followUpDate: "2024-02-15T14:00:00Z",
          immunosuppressiveTreatment: "Tacrolimus, Mycophénolate",
          observations: "Bon suivi, fonction rénale stable",
          clinicalNotes: "Patient stable, pas de complications",
          complications: "Aucune",
          creatinineLevel: 1.1,
          gfr: 75.5,
          bloodPressure: "120/80",
          medicationAdjustments: "Aucun ajustement nécessaire",
          labResults: "Normaux",
          isFollowUpComplete: true,
          nextFollowUpDate: "2024-03-15T10:00:00Z",
          recommendations: "Continuer le traitement actuel",
          followUpType: "ROUTINE",
          patientAttended: true,
          patientFeedback: "Patient se sent bien",
          kidneyTransplant: { id: 1, transplantDate: "2024-01-15T10:30:00Z" },
          doctor: {
            id: 1,
            user: { firstName: "Dr.", lastName: "Sophie Martin" },
            specialization: "Néphrologie pédiatrique"
          },
          createdAt: "2024-01-16T09:00:00Z",
          updatedAt: "2024-02-15T15:30:00Z"
        }
      ],
      createdAt: "2024-01-10T09:00:00Z",
      updatedAt: "2024-01-15T16:30:00Z"
    },
    {
      id: 2,
      transplantDate: "2024-02-20T14:00:00Z",
      status: "PLANNED",
      medicalRecord: {
        id: 2,
        patient: {
          firstName: "Léa",
          lastName: "Bernard",
          medicalRecordNumber: "MR-2024-002"
        }
      },
      doctor: {
        id: 2,
        user: {
          firstName: "Dr.",
          lastName: "Pierre Durand"
        },
        specialization: "Chirurgie pédiatrique",
        licenseNumber: "MED-67890"
      },
      postTransplantFollowUps: [],
      createdAt: "2024-01-25T11:00:00Z",
      updatedAt: "2024-01-25T11:00:00Z"
    }
  ];
  
  private nextId = 3;

  constructor(private http: HttpClient) { }

  // KidneyTransplant CRUD - Version Mock
  getAllTransplants(): Observable<KidneyTransplant[]> {
    return new Observable(observer => {
      setTimeout(() => {
        observer.next([...this.mockTransplants]);
        observer.complete();
      }, 500);
    });
  }

  getTransplantById(id: number): Observable<KidneyTransplant> {
    return new Observable(observer => {
      setTimeout(() => {
        const transplant = this.mockTransplants.find(t => t.id === id);
        if (transplant) {
          observer.next(transplant);
        } else {
          observer.error({ status: 404, message: 'Transplantation non trouvée' });
        }
        observer.complete();
      }, 300);
    });
  }

  createTransplant(transplant: any): Observable<KidneyTransplant> {
    return new Observable(observer => {
      setTimeout(() => {
        const newTransplant: KidneyTransplant = {
          id: this.nextId++,
          transplantDate: transplant.transplantDate,
          status: transplant.status || 'PLANNED',
          medicalRecord: transplant.medicalRecord,
          doctor: transplant.doctor,
          postTransplantFollowUps: transplant.postTransplantFollowUps || [],
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        
        this.mockTransplants.unshift(newTransplant);
        observer.next(newTransplant);
        observer.complete();
      }, 800);
    });
  }

  updateTransplant(id: number, transplant: any): Observable<KidneyTransplant> {
    return new Observable(observer => {
      setTimeout(() => {
        const index = this.mockTransplants.findIndex(t => t.id === id);
        if (index !== -1) {
          this.mockTransplants[index] = {
            ...this.mockTransplants[index],
            ...transplant,
            updatedAt: new Date().toISOString()
          };
          observer.next(this.mockTransplants[index]);
        } else {
          observer.error({ status: 404, message: 'Transplantation non trouvée' });
        }
        observer.complete();
      }, 600);
    });
  }

  deleteTransplant(id: number): Observable<void> {
    return new Observable(observer => {
      setTimeout(() => {
        const index = this.mockTransplants.findIndex(t => t.id === id);
        if (index !== -1) {
          this.mockTransplants.splice(index, 1);
          observer.next();
        } else {
          observer.error({ status: 404, message: 'Transplantation non trouvée' });
        }
        observer.complete();
      }, 400);
    });
  }

  getTransplantsByDoctor(doctorId: number): Observable<KidneyTransplant[]> {
    return this.http.get<KidneyTransplant[]>(`${this.apiUrl}/api/kidney-transplant/doctor/${doctorId}`);
  }

  getTransplantsByMedicalRecord(medicalRecordId: number): Observable<KidneyTransplant[]> {
    return this.http.get<KidneyTransplant[]>(`${this.apiUrl}/api/kidney-transplant/medical-record/${medicalRecordId}`);
  }

  // PostTransplantFollowUp CRUD
  getAllFollowUps(): Observable<PostTransplantFollowUp[]> {
    return this.http.get<PostTransplantFollowUp[]>(`${this.apiUrl}/api/post-transplant-follow-up`);
  }

  getFollowUpById(id: number): Observable<PostTransplantFollowUp> {
    return this.http.get<PostTransplantFollowUp>(`${this.apiUrl}/api/post-transplant-follow-up/${id}`);
  }

  createFollowUp(followUp: any): Observable<PostTransplantFollowUp> {
    return this.http.post<PostTransplantFollowUp>(`${this.apiUrl}/api/post-transplant-follow-up`, followUp);
  }

  updateFollowUp(id: number, followUp: any): Observable<PostTransplantFollowUp> {
    return this.http.put<PostTransplantFollowUp>(`${this.apiUrl}/api/post-transplant-follow-up/${id}`, followUp);
  }

  deleteFollowUp(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/post-transplant-follow-up/${id}`);
  }

  getFollowUpsByTransplant(transplantId: number): Observable<PostTransplantFollowUp[]> {
    return this.http.get<PostTransplantFollowUp[]>(`${this.apiUrl}/api/post-transplant-follow-up/transplant/${transplantId}`);
  }
}
