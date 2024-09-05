import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DemandeActeurComponent } from './demande-acteur.component';

describe('DemandeActeurComponent', () => {
  let component: DemandeActeurComponent;
  let fixture: ComponentFixture<DemandeActeurComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DemandeActeurComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DemandeActeurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
