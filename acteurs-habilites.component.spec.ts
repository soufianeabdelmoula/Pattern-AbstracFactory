import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActeursHabilitesComponent } from './acteurs-habilites.component';

describe('ActeursHabilitesComponent', () => {
  let component: ActeursHabilitesComponent;
  let fixture: ComponentFixture<ActeursHabilitesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActeursHabilitesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActeursHabilitesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
