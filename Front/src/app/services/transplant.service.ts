import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

import { 
  Transplant, 
  KidneyTransplant, 
  PostTransplantFollowUp,
  Donor,
  Recipient,
  TransplantStatus,
  TransplantType,
  SurgeryApproach,
  KidneySource
} from '../models/transplant.model';

@Injectable({
  providedIn: 'root'
})
export class TransplantService {
  private apiUrl = environment.apiUrl + '/api';

  constructor(private http: HttpClient) {}

  // === TRANSPLANT OPERATIONS ===
  
  getAllTransplants(): Observable<Transplant[]> {
    return this.http.get<Transplant[]>(`${this.apiUrl}/transplants`);
  }

  getTransplantById(id: number): Observable<Transplant> {
    return this.http.get<Transplant>(`${this.apiUrl}/transplants/${id}`);
  }

  createTransplant(transplant: Transplant): Observable<Transplant> {
    return this.http.post<Transplant>(`${this.apiUrl}/transplants`, transplant);
  }

  updateTransplant(id: number, transplant: Transplant): Observable<Transplant> {
    return this.http.put<Transplant>(`${this.apiUrl}/transplants/${id}`, transplant);
  }

  deleteTransplant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/transplants/${id}`);
  }

  getTransplantsByStatus(status: TransplantStatus): Observable<Transplant[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<Transplant[]>(`${this.apiUrl}/transplants/by-status`, { params });
  }

  getTransplantsByRecipient(recipientId: number): Observable<Transplant[]> {
    return this.http.get<Transplant[]>(`${this.apiUrl}/transplants/by-recipient/${recipientId}`);
  }

  // === KIDNEY TRANSPLANT OPERATIONS ===
  
  getAllKidneyTransplants(): Observable<KidneyTransplant[]> {
    return this.http.get<KidneyTransplant[]>(`${this.apiUrl}/kidney-transplants`);
  }

  getKidneyTransplantById(id: number): Observable<KidneyTransplant> {
    return this.http.get<KidneyTransplant>(`${this.apiUrl}/kidney-transplants/${id}`);
  }

  createKidneyTransplant(kidneyTransplant: KidneyTransplant): Observable<KidneyTransplant> {
    return this.http.post<KidneyTransplant>(`${this.apiUrl}/kidney-transplants`, kidneyTransplant);
  }

  updateKidneyTransplant(id: number, kidneyTransplant: KidneyTransplant): Observable<KidneyTransplant> {
    return this.http.put<KidneyTransplant>(`${this.apiUrl}/kidney-transplants/${id}`, kidneyTransplant);
  }

  deleteKidneyTransplant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/kidney-transplants/${id}`);
  }

  getKidneyTransplantsByType(type: TransplantType): Observable<KidneyTransplant[]> {
    const params = new HttpParams().set('type', type);
    return this.http.get<KidneyTransplant[]>(`${this.apiUrl}/kidney-transplants/by-type`, { params });
  }

  getKidneyTransplantsBySurgeon(surgeonId: number): Observable<KidneyTransplant[]> {
    return this.http.get<KidneyTransplant[]>(`${this.apiUrl}/kidney-transplants/by-surgeon/${surgeonId}`);
  }

  getSuccessfulTransplants(): Observable<KidneyTransplant[]> {
    return this.http.get<KidneyTransplant[]>(`${this.apiUrl}/kidney-transplants/successful`);
  }

  getFailedTransplants(): Observable<KidneyTransplant[]> {
    return this.http.get<KidneyTransplant[]>(`${this.apiUrl}/kidney-transplants/failed`);
  }

  // === POST TRANSPLANT FOLLOW UP OPERATIONS ===
  
  getAllFollowUps(): Observable<PostTransplantFollowUp[]> {
    return this.http.get<PostTransplantFollowUp[]>(`${this.apiUrl}/post-transplant-follow-ups`);
  }

  getFollowUpById(id: number): Observable<PostTransplantFollowUp> {
    return this.http.get<PostTransplantFollowUp>(`${this.apiUrl}/post-transplant-follow-ups/${id}`);
  }

  createFollowUp(followUp: PostTransplantFollowUp): Observable<PostTransplantFollowUp> {
    return this.http.post<PostTransplantFollowUp>(`${this.apiUrl}/post-transplant-follow-ups`, followUp);
  }

  updateFollowUp(id: number, followUp: PostTransplantFollowUp): Observable<PostTransplantFollowUp> {
    return this.http.put<PostTransplantFollowUp>(`${this.apiUrl}/post-transplant-follow-ups/${id}`, followUp);
  }

  deleteFollowUp(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/post-transplant-follow-ups/${id}`);
  }

  getFollowUpsByTransplant(transplantId: number): Observable<PostTransplantFollowUp[]> {
    return this.http.get<PostTransplantFollowUp[]>(`${this.apiUrl}/post-transplant-follow-ups/by-transplant/${transplantId}`);
  }

  getFollowUpsByPatient(patientId: number): Observable<PostTransplantFollowUp[]> {
    return this.http.get<PostTransplantFollowUp[]>(`${this.apiUrl}/post-transplant-follow-ups/by-patient/${patientId}`);
  }

  getOverdueFollowUps(): Observable<PostTransplantFollowUp[]> {
    return this.http.get<PostTransplantFollowUp[]>(`${this.apiUrl}/post-transplant-follow-ups/overdue`);
  }

  getUpcomingFollowUps(): Observable<PostTransplantFollowUp[]> {
    return this.http.get<PostTransplantFollowUp[]>(`${this.apiUrl}/post-transplant-follow-ups/upcoming`);
  }

  // === DONOR OPERATIONS ===
  
  getAllDonors(): Observable<Donor[]> {
    return this.http.get<Donor[]>(`${this.apiUrl}/donors`);
  }

  getDonorById(id: number): Observable<Donor> {
    return this.http.get<Donor>(`${this.apiUrl}/donors/${id}`);
  }

  createDonor(donor: Donor): Observable<Donor> {
    return this.http.post<Donor>(`${this.apiUrl}/donors`, donor);
  }

  updateDonor(id: number, donor: Donor): Observable<Donor> {
    return this.http.put<Donor>(`${this.apiUrl}/donors/${id}`, donor);
  }

  // === RECIPIENT OPERATIONS ===
  
  getAllRecipients(): Observable<Recipient[]> {
    return this.http.get<Recipient[]>(`${this.apiUrl}/recipients`);
  }

  getRecipientById(id: number): Observable<Recipient> {
    return this.http.get<Recipient>(`${this.apiUrl}/recipients/${id}`);
  }

  createRecipient(recipient: Recipient): Observable<Recipient> {
    return this.http.post<Recipient>(`${this.apiUrl}/recipients`, recipient);
  }

  updateRecipient(id: number, recipient: Recipient): Observable<Recipient> {
    return this.http.put<Recipient>(`${this.apiUrl}/recipients/${id}`, recipient);
  }

  // === STATISTICS ===
  
  getTransplantStatistics(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/transplants/statistics`);
  }

  getKidneyTransplantStatistics(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/kidney-transplants/statistics`);
  }

  getFollowUpStatistics(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/post-transplant-follow-ups/statistics`);
  }
}
