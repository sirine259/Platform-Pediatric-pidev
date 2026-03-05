import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransplantComponent } from './transplant.component';

describe('TransplantComponent', () => {
  let component: TransplantComponent;
  let fixture: ComponentFixture<TransplantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransplantComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransplantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
