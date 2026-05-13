import { ComponentFixture, TestBed } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { CommonModule } from "@angular/common";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { FollowUpDetailComponent } from "./follow-up-detail.component";

describe("FollowUpDetailComponent", () => {
  let component: FollowUpDetailComponent;
  let fixture: ComponentFixture<FollowUpDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonModule, HttpClientTestingModule, RouterTestingModule, NoopAnimationsModule, FollowUpDetailComponent]
    }).compileComponents();
    fixture = TestBed.createComponent(FollowUpDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => { expect(component).toBeTruthy(); });
});