import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class GoogleMapsService {
  private apiUrl = 'http://localhost:8091/api/config';

  constructor(private http: HttpClient) {}

  getApiKey(): Observable<string | null> {
    return this.http.get<{ key: string; enabled: boolean }>(`${this.apiUrl}/google-maps-key`).pipe(
      map(response => response.enabled ? response.key : null),
      catchError(() => of(null))
    );
  }

  setApiKey(apiKey: string): Observable<any> {
    return this.http.post<{ key: string; enabled: boolean }>(`${this.apiUrl}/google-maps-key`, { key: apiKey }).pipe(
      map(response => {
        localStorage.setItem('googleMapsApiKey', apiKey);
        return { success: response.enabled };
      }),
      catchError((error) => {
        return of({ success: false, error });
      })
    );
  }
}
