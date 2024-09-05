import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreerOffreComponent } from './creer-offre.component';

describe('CreerOffreComponent', () => {
  let component: CreerOffreComponent;
  let fixture: ComponentFixture<CreerOffreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreerOffreComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreerOffreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
