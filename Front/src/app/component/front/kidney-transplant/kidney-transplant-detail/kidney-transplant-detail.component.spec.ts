import { ComponentFixture, TestBed } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { CommonModule } from "@angular/common";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { KidneyTransplantDetailComponent } from "./kidney-transplant-detail.component";

describe("KidneyTransplantDetailComponent", () => {
  let component: KidneyTransplantDetailComponent;
  let fixture: ComponentFixture<KidneyTransplantDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonModule, HttpClientTestingModule, RouterTestingModule, NoopAnimationsModule, KidneyTransplantDetailComponent]
    }).compileComponents();
    fixture = TestBed.createComponent(KidneyTransplantDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => { expect(component).toBeTruthy(); });
});