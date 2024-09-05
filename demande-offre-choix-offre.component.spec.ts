import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DemandeOffreChoixOffreComponent } from './demande-offre-choix-offre.component';

describe('DemandeOffreChoixOffreComponent', () => {
  let component: DemandeOffreChoixOffreComponent;
  let fixture: ComponentFixture<DemandeOffreChoixOffreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DemandeOffreChoixOffreComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DemandeOffreChoixOffreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
