import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {DemandeActeur} from "../../../models/demande-acteur.model";
import {MatRadioChange} from "@angular/material/radio";
import {DemandeService} from "../../../shared/services/demande.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ActivatedRoute} from "@angular/router";
import {BehaviorSubject} from "rxjs";
import {ConstantsGlobals} from 'src/app/shared/utils/constantes-globals';
import {StructureService} from "../../../shared/services/structure.service";
import {Location} from "@angular/common";

type Mode = 'creation' | 'modification' | 'suppression';

@Component({
    selector: 'app-demande-acteur',
    templateUrl: './demande-acteur.component.html',
    styleUrls: ['./demande-acteur.component.scss']
})
export class DemandeActeurComponent implements OnInit, OnChanges {

    demande: DemandeActeur = new DemandeActeur()
    protected readonly ConstantsGlobals = ConstantsGlobals;
    mode: Mode = 'creation'
    @Input() isView: boolean = false;
    @Input() demandeActeur: DemandeActeur;

  private celluleActeurSubject = new BehaviorSubject<any>(null);
  celluleActeur$ = this.celluleActeurSubject.asObservable();

  private celluleTerrainActeurSubject = new BehaviorSubject<any>(null);
  celluleTerrainActeur$ = this.celluleTerrainActeurSubject.asObservable();


  constructor(private demandeService: DemandeService, private snackbar: MatSnackBar, private route: ActivatedRoute,
              private structureService: StructureService, private _location: Location) {
  }

    ngOnInit() {
        this.mode = <Mode>this.route.snapshot.paramMap.get('mode')
    }

    ngOnChanges(changes: SimpleChanges) {
      if (changes['demandeActeur'] && changes['demandeActeur'].currentValue && this.isView) {
        this.demande = this.getDemandeActeur(this.demandeActeur)
        if (this.demande && this.demande.cellule.trim()){
          this.structureService.getCelluleOrHistoriqueCelluleByCode(this.demande.cellule).subscribe(cell => {
            this.celluleActeurSubject.next(cell);
          })
        }

        if (this.demande && this.demande.celluleTerrain){
          this.structureService.getCelluleOrHistoriqueCelluleByCode(this.demande.celluleTerrain).subscribe(cell => {
            this.celluleTerrainActeurSubject.next(cell);
          })
        }
      }
    }

  saveDemande() {
        this.demande.typeDemande = this.mode == 'creation' ? ConstantsGlobals.TOP_CREATION : this.mode == 'modification' ? ConstantsGlobals.TOP_MODIFICATION : ConstantsGlobals.TOP_SUPPRESSION

        this.demandeService.saveDemandeActeur(this.demande).subscribe(() => {
          this.snackbar.open("La demande a bien été sauvegardée.", "OK", {duration: 3 * 1000});
          this._location.back()
        })
    }

    changeTypoActeur($event: MatRadioChange) {
        this.demande = new DemandeActeur()
        this.demande.typeActeur = $event.value
    }

  public getDemandeActeur(demandeAc: DemandeActeur): DemandeActeur{
    return new DemandeActeur(demandeAc.id, demandeAc.typeActeur, demandeAc.typeDemande, demandeAc.acteurBenef,
      demandeAc.statut, demandeAc.coll, demandeAc.ssColl, demandeAc.idta, demandeAc.idtn, demandeAc.login, demandeAc.nom, demandeAc.nomMarital, demandeAc.prenom, demandeAc.email,
      demandeAc.cellule, demandeAc.fonction, demandeAc.nomUsuel, demandeAc.prenomUsuel, demandeAc.collTerrain, demandeAc.ssCollTerrain, demandeAc.celluleTerrain,
      demandeAc.description, demandeAc.tsSortiePrev, demandeAc.celluleDet, demandeAc.telExterne, demandeAc.etapeDemande, demandeAc.commentaire, demandeAc.piecesJointes, demandeAc.matricule
    )
  }
}
