import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {CreerOffreComponent} from "./creer-offre/creer-offre.component";
import {RechercheOffresComponent} from "./recherche-offres/recherche-offres.component";

const routes: Routes = [{
  path: 'creer-offre', component: CreerOffreComponent
}, {
  path: 'afficher-offre/:idOffre', component: CreerOffreComponent
},
  {
    path: 'rechercher-offre', component: RechercheOffresComponent
  },
]

@NgModule({imports: [RouterModule.forChild(routes)], exports: [RouterModule]})
export class OffreRoutingModule {
}
