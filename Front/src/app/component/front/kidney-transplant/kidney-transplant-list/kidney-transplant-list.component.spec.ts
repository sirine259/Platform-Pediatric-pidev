import { ComponentFixture, TestBed } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { KidneyTransplantListComponent } from "./kidney-transplant-list.component";

describe("KidneyTransplantListComponent", () => {
  let component: KidneyTransplantListComponent;
  let fixture: ComponentFixture<KidneyTransplantListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule, HttpClientTestingModule, RouterTestingModule, NoopAnimationsModule, KidneyTransplantListComponent]
    }).compileComponents();
    fixture = TestBed.createComponent(KidneyTransplantListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => { expect(component).toBeTruthy(); });
});