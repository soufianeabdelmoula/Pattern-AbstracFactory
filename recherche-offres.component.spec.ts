import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RechercheOffresComponent } from './recherche-offres.component';

describe('RechercheOffresComponent', () => {
  let component: RechercheOffresComponent;
  let fixture: ComponentFixture<RechercheOffresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RechercheOffresComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RechercheOffresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
