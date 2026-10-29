import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Devolusionesprestamos } from './devolusionesprestamos';

describe('Devolusionesprestamos', () => {
  let component: Devolusionesprestamos;
  let fixture: ComponentFixture<Devolusionesprestamos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Devolusionesprestamos],
    }).compileComponents();

    fixture = TestBed.createComponent(Devolusionesprestamos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
