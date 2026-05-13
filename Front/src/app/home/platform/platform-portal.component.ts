import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-platform-portal',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule, MatButtonModule],
  templateUrl: './platform-portal.component.html',
  styleUrls: ['./platform-portal.component.css']
})
export class PlatformPortalComponent {

  constructor(private router: Router) {}

  openForum(): void {
    this.router.navigate(['/forum']);
  }

  openKidneyTransplant(): void {
    this.router.navigate(['/kidney-transplant']);
  }
}

