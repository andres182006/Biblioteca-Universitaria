import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Videobeamsprofesores } from './videobeamsprofesores';

describe('Videobeamsprofesores', () => {
  let component: Videobeamsprofesores;
  let fixture: ComponentFixture<Videobeamsprofesores>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Videobeamsprofesores],
    }).compileComponents();

    fixture = TestBed.createComponent(Videobeamsprofesores);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
