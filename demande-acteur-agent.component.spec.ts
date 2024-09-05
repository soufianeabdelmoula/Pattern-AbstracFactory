import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DemandeActeurAgentComponent } from './demande-acteur-agent.component';

describe('DemandeActeurAgentComponent', () => {
  let component: DemandeActeurAgentComponent;
  let fixture: ComponentFixture<DemandeActeurAgentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DemandeActeurAgentComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DemandeActeurAgentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
