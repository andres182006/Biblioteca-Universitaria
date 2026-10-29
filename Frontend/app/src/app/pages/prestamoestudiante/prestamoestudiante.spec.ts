import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Prestamoestudiante } from './prestamoestudiante';

describe('Prestamoestudiante', () => {
  let component: Prestamoestudiante;
  let fixture: ComponentFixture<Prestamoestudiante>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Prestamoestudiante],
    }).compileComponents();

    fixture = TestBed.createComponent(Prestamoestudiante);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
