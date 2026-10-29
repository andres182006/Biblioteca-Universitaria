import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Videobeams } from './videobeams';

describe('Videobeams', () => {
  let component: Videobeams;
  let fixture: ComponentFixture<Videobeams>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Videobeams],
    }).compileComponents();

    fixture = TestBed.createComponent(Videobeams);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
