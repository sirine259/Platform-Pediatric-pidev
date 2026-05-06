import { Component, Input, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GoogleMapsService } from '../../../services/google-maps.service';
import { SafeUrlPipe } from '../../../shared/safe-url.pipe';

declare var google: any;

export interface HospitalLocation {
  name: string;
  address: string;
  lat: number;
  lng: number;
  phone?: string;
}

@Component({
  selector: 'app-google-maps',
  standalone: true,
  imports: [CommonModule, SafeUrlPipe],
  templateUrl: './google-maps.component.html',
  styleUrls: ['./google-maps.component.css']
})
export class GoogleMapsComponent implements AfterViewInit, OnInit {
  @ViewChild('mapElement') mapElement!: ElementRef;
  @Input() locations: HospitalLocation[] = [];
  @Input() height: string = '400px';
  @Input() zoom: number = 8;
  @Input() centerLat: number = 46.603354;
  @Input() centerLng: number = 2.5;

  map: any;
  markers: any[] = [];
  infoWindow: any;
  mapKey: string | null = null;
  mapKeySource: string = 'aucune';
  isLoading: boolean = false;
  mapErrorMessage: string = '';
  showOsmFallback: boolean = false;
  osmUrl: string = '';

  constructor(private googleMapsService: GoogleMapsService) {}

  private defaultHospitals: HospitalLocation[] = [
    { name: 'Hôpital Necker-Enfants Malades', address: '149 Rue de Sèvres, 75015 Paris', lat: 48.8499, lng: 2.3167, phone: '01 44 49 40 00' },
    { name: 'CHU de Rouen', address: '1 Rue de Germont, 76000 Rouen', lat: 49.4432, lng: 1.0989, phone: '02 32 88 89 90' },
    { name: 'Hospices Civils de Lyon', address: '28 Avenue Jean Jaurès, 69007 Lyon', lat: 45.7485, lng: 4.8467, phone: '04 72 11 60 60' },
    { name: 'CHU de Toulouse', address: 'Place du Dr Baylac, 31059 Toulouse', lat: 43.5586, lng: 1.4543, phone: '05 61 77 22 33' },
    { name: 'CHU de Nantes', address: '5 Rue de la Mourière, 44093 Nantes', lat: 47.2173, lng: -1.5534, phone: '02 40 08 33 33' }
  ];

  private getKeyFromUrl(): string | null {
    const params = new URLSearchParams(window.location.search);
    const gmKey = params.get('gmKey');
    return gmKey?.trim() || null;
  }

  ngOnInit(): void {
    const urlKey = this.getKeyFromUrl();
    if (urlKey) {
      this.mapKey = urlKey;
      this.mapKeySource = 'URL (gmKey)';
      this.mapErrorMessage = 'Utilisation de la clé via l\'URL (gmKey).';
      this.loadGoogleMaps(this.mapKey);
      if (!this.locations || this.locations.length === 0) {
        this.locations = this.defaultHospitals;
      }
      return;
    }

    const storedKey = localStorage.getItem('googleMapsApiKey');
    if (storedKey) {
      this.mapKey = storedKey;
      this.mapKeySource = 'localStorage';
      this.mapErrorMessage = 'Utilisation de la clé stockée localement.';
      this.loadGoogleMaps(this.mapKey);
      if (!this.locations || this.locations.length === 0) {
        this.locations = this.defaultHospitals;
      }
      return;
    }

    this.googleMapsService.getApiKey().subscribe((key) => {
      if (key) {
        this.mapKey = key;
        this.mapKeySource = 'backend';
        this.mapErrorMessage = 'Clé backend récupérée.';
        this.loadGoogleMaps(key);
      } else {
        this.mapKey = null;
        this.mapKeySource = 'aucune';
        this.mapErrorMessage = 'Clé Google Maps non configurée sur le backend. Mettre la clé dans application.properties ou via /api/config/google-maps-key.';
        this.loadMapWithoutKey();
      }

      if (!this.locations || this.locations.length === 0) {
        this.locations = this.defaultHospitals;
      }
    });
  }

  ngAfterViewInit(): void {
    // Gestion des erreurs d'authentification Maps (clé invalide / restrictions)
    (window as any).gm_authFailure = () => {
      this.onMapError('Clé Google Maps invalide ou restrictions de domaine activées.');
    };
  }

  private onMapError(message: string): void {
    if (message.includes('ApiNotActivatedMapError')) {
      message += ' - activez Maps JavaScript API dans Google Cloud Console et vérifiez la clé.';
      this.activateOsmFallback();
    } else if (message.includes('RefererNotAllowedMapError')) {
      message += ' - ajoutez http://localhost:4200/* comme restriction d\'URL dans les clés API.';
      this.activateOsmFallback();
    } else if (message.includes('InvalidKey')) {
      message += ' - vérifiez que la clé est correcte et liée au bon projet GCP.';
      this.activateOsmFallback();
    }

    setTimeout(() => {
      this.mapErrorMessage = message;
      this.isLoading = false;
      this.map = null;
      this.markers = [];
    });
  }

  private activateOsmFallback(): void {
    this.showOsmFallback = true;
    this.osmUrl = `https://www.openstreetmap.org/export/embed.html?bbox=${this.centerLng - 0.2}%2C${this.centerLat - 0.2}%2C${this.centerLng + 0.2}%2C${this.centerLat + 0.2}&layer=mapnik&marker=${this.centerLat}%2C${this.centerLng}`;
  }

  getMapsUrl(name: string): string {
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(name)}`;
  }

  private loadGoogleMaps(apiKey: string): void {
    this.mapErrorMessage = '';
    this.isLoading = true;

    if (typeof google !== 'undefined' && google.maps) {
      this.initMap();
      this.isLoading = false;
      return;
    }

    const CALLBACK_NAME = '__googleMapsLoadedCallback';
    (window as any)[CALLBACK_NAME] = () => {
      try {
        this.initMap();
      } catch (error) {
        this.onMapError('Erreur d\'initialisation de la carte après chargement du script.');
      } finally {
        this.isLoading = false;
      }
    };

    const script = document.createElement('script');
    script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&v=weekly&callback=${CALLBACK_NAME}`;
    script.async = true;
    script.defer = true;
    script.setAttribute('loading', 'lazy');
    script.onerror = () => {
      this.onMapError('Impossible de charger Google Maps (erreur réseau ou clé invalide).');
      this.isLoading = false;
    };

    document.head.appendChild(script);
  }

  private loadMapWithoutKey(): void {
    this.map = null;
    this.mapErrorMessage = 'Clé API non disponible : affichez la liste des hôpitaux et vérifiez la config backend.';
  }

  focusHospital(hospital: HospitalLocation): void {
    if (!this.map) return;

    const position = { lat: hospital.lat, lng: hospital.lng };
    this.map.setCenter(position);
    this.map.setZoom(14);

    if (!this.infoWindow) {
      this.infoWindow = new google.maps.InfoWindow();
    }

    const contentString = `
      <div style="padding: 10px; min-width: 200px;">
        <h3 style="margin: 0 0 8px 0; color: #667eea; font-size: 14px;">${hospital.name}</h3>
        <p style="margin: 5px 0; font-size: 12px; color: #666;">📍 ${hospital.address}</p>
        ${hospital.phone ? `<p style="margin: 5px 0; font-size: 12px; color: #666;">📞 ${hospital.phone}</p>` : ''}
      </div>
    `;

    this.infoWindow.setContent(contentString);
    this.infoWindow.setPosition(position);
    this.infoWindow.open(this.map);
  }

  private renderHospitalsList(): string {
    return `
      <div style="padding: 20px; height: 100%; overflow-y: auto; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);">
        <div style="text-align: center; margin-bottom: 20px;">
          <div style="font-size: 40px; margin-bottom: 10px;">🗺️</div>
          <h3 style="color: #333; margin: 0 0 5px 0;">Carte des Hôpitaux</h3>
          <p style="color: #666; font-size: 12px; margin: 0;">Hôpitaux de transplantation rénale pédiatrique</p>
        </div>
        <div style="display: grid; gap: 12px;">
          ${this.defaultHospitals.map(h => `
            <a href="https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(h.name + ' ' + h.address)}" 
               target="_blank"
               style="text-decoration: none;">
              <div style="background: white; padding: 15px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); cursor: pointer; transition: all 0.3s; border-left: 4px solid #667eea;"
                   onmouseover="this.style.transform='translateX(5px)'; this.style.boxShadow='0 4px 20px rgba(102,126,234,0.3)';"
                   onmouseout="this.style.transform='translateX(0)'; this.style.boxShadow='0 2px 10px rgba(0,0,0,0.1)';">
                <div style="display: flex; align-items: center; gap: 10px;">
                  <span style="font-size: 24px;">📍</span>
                  <div>
                    <div style="font-weight: bold; color: #667eea; font-size: 14px;">${h.name}</div>
                    <div style="color: #666; font-size: 12px;">${h.address}</div>
                    ${h.phone ? `<div style="color: #888; font-size: 11px; margin-top: 4px;">📞 ${h.phone}</div>` : ''}
                    <div style="color: #667eea; font-size: 11px; margin-top: 6px;">Cliquez pour ouvrir dans Google Maps →</div>
                  </div>
                </div>
              </div>
            </a>
          `).join('')}
        </div>
      </div>
    `;
  }

  private initMap(): void {
    if (!this.mapElement?.nativeElement) return;

    this.locations = this.defaultHospitals;

    this.map = new google.maps.Map(this.mapElement.nativeElement, {
      center: { lat: this.centerLat, lng: this.centerLng },
      zoom: this.zoom
    });

    this.infoWindow = new google.maps.InfoWindow();
    this.addMarkers();
    this.fitBounds();
  }

  private addMarkers(): void {
    (this.locations || this.defaultHospitals).forEach(hospital => {
      const marker = new google.maps.Marker({
        position: { lat: hospital.lat, lng: hospital.lng },
        map: this.map,
        title: hospital.name,
        animation: google.maps.Animation.DROP
      });

      const contentString = `
        <div style="padding: 10px; min-width: 200px;">
          <h3 style="margin: 0 0 8px 0; color: #667eea; font-size: 14px;">${hospital.name}</h3>
          <p style="margin: 5px 0; font-size: 12px; color: #666;">📍 ${hospital.address}</p>
          ${hospital.phone ? `<p style="margin: 5px 0; font-size: 12px; color: #666;">📞 ${hospital.phone}</p>` : ''}
          <a href="https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(hospital.name)}" 
             target="_blank" 
             style="color: #667eea; font-size: 12px; text-decoration: none; display: inline-block; margin-top: 8px;">
            Ouvrir dans Google Maps →
          </a>
        </div>
      `;

      marker.addListener('click', () => {
        this.infoWindow.setContent(contentString);
        this.infoWindow.open(this.map, marker);
      });

      this.markers.push(marker);
    });
  }

  private fitBounds(): void {
    if (this.markers.length === 0) return;

    const bounds = new google.maps.LatLngBounds();
    this.markers.forEach(marker => bounds.extend(marker.getPosition()));
    this.map.fitBounds(bounds);

    if (this.markers.length === 1) {
      this.map.setZoom(14);
    }
  }

  saveApiKey(apiKey: string): void {
    if (!apiKey || !apiKey.trim()) {
      this.mapErrorMessage = 'La clé est vide, veuillez fournir une clé Google Maps valide.';
      return;
    }

    this.googleMapsService.setApiKey(apiKey.trim()).subscribe((result) => {
      if (result.success) {
        this.mapKey = apiKey.trim();
        this.mapKeySource = 'backend';
        this.mapErrorMessage = 'Clé enregistrée dans le backend et chargement de la carte en cours.';
        this.loadGoogleMaps(this.mapKey);
      } else {
        this.mapErrorMessage = 'Erreur lors de l\'enregistrement de la clé backend.';
      }
    });
  }

  clearApiKey(): void {
    localStorage.removeItem('googleMapsApiKey');
    this.mapKey = null;
    this.mapKeySource = 'aucune';
    this.mapErrorMessage = 'Clé locale supprimée.';
    this.map = null;
    this.markers = [];
  }

  reloadMap(): void {
    if (this.mapKey) {
      this.mapErrorMessage = 'Rechargement de la carte avec la clé actuelle.';
      this.loadGoogleMaps(this.mapKey);
    } else {
      this.mapErrorMessage = 'Aucune clé disponible pour recharger la carte.';
    }
  }
}
