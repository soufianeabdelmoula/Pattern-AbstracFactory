import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {OffreRoutingModule} from "./offre-routing.module";
import {SharedModule} from "../../shared/shared.module";
import {ActeursHabilitesComponent} from "./creer-offre/acteurs-habilites/acteurs-habilites.component";
import {ActeursModalComponent} from "./creer-offre/acteurs-modal/acteurs-modal.component";
import {GroupeApplicatifAdComponent} from "./creer-offre/groupe-applicatif-ad/groupe-applicatif-ad.component";
import {QuestionOffreComponent} from "./creer-offre/question-offre/question-offre.component";
import {CreerOffreComponent} from "./creer-offre/creer-offre.component";
import {RechercheOffresComponent} from "./recherche-offres/recherche-offres.component";
import {DemandeModule} from "../demande/demande.module";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {MatCardModule} from "@angular/material/card";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatButtonModule} from "@angular/material/button";
import {MatSelectModule} from "@angular/material/select";
import {
  GroupeApplicatifModalComponent
} from "./creer-offre/groupe-applicatif-ad/groupe-applicatif-modal/groupe-applicatif-modal.component";
import {MatTabsModule} from "@angular/material/tabs";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatDividerModule} from "@angular/material/divider";


@NgModule({
  declarations: [ActeursHabilitesComponent, ActeursModalComponent, GroupeApplicatifAdComponent, QuestionOffreComponent, CreerOffreComponent, RechercheOffresComponent, GroupeApplicatifModalComponent],
  imports: [
    CommonModule,
    OffreRoutingModule,
    SharedModule,
    DemandeModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatCardModule,
    ReactiveFormsModule,
    MatTooltipModule,
    MatButtonModule,
    MatSelectModule,
    FormsModule,
    MatTabsModule,
    MatCheckboxModule,
    MatDividerModule
  ]
})
export class OffreModule {
}
