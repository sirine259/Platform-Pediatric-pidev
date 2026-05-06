import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ForumDetailComponent } from './forum-detail.component';

describe('ForumDetailComponent', () => {
  let component: ForumDetailComponent;
  let fixture: ComponentFixture<ForumDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        HttpClientTestingModule,
        RouterTestingModule,
        NoopAnimationsModule,
        ForumDetailComponent
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ForumDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});