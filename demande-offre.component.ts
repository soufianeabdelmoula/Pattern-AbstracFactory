import {Component, OnInit, ViewChild} from '@angular/core';
import {OffreService} from "../../../shared/services/offre.service";
import {Question} from "../../../models/question.model";
import {Profil} from "../../../models/profil.model";
import {DemandeDroit} from "../../../models/demande-droit.model";
import {FormBuilder, Validators} from "@angular/forms";
import {DoubleSelectEvent} from "../../../shared/components/double-select/double-select.component";
import {Reponse} from "../../../models/reponse.model";
import {MatStepper} from "@angular/material/stepper";
import {DemandeService} from "../../../shared/services/demande.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {ConstantsGlobals} from "../../../shared/utils/constantes-globals";

type Mode = 'creation' | 'modification' | 'suppression' | 'tache';


@Component({
  selector: 'app-demande-offre',
  templateUrl: './demande-offre.component.html',
  styleUrls: ['./demande-offre.component.scss']
})
export class DemandeOffreComponent implements OnInit {

  mode: Mode = "creation"

  @ViewChild('stepper', {static: false}) stepper: MatStepper;

  demande: DemandeDroit = new DemandeDroit();
  questions: Question[]
  profils: Profil[]

  choixOffreFormControl = this._formBuilder.group({
    offreControl: ['', Validators.required],
    acteurControl: ['', Validators.required],
    commentaireControl: ['', Validators.required],
  })

  constructor(private offreService: OffreService,
              private _formBuilder: FormBuilder,
              private demandeService: DemandeService,
              private snackBar: MatSnackBar,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private _location: Location) {
  }

  ngOnInit() {
    this.mode = <Mode>this.activatedRoute.snapshot.paramMap.get('mode')
    this.demande.topDem = this.mode == 'creation' ? ConstantsGlobals.TOP_CREATION : this.mode == 'modification' ? ConstantsGlobals.TOP_MODIFICATION : ConstantsGlobals.TOP_SUPPRESSION

  }

  selectOffre($event: any) {
    this.demande.offre = $event
    this.offreService.getQuestions(this.demande.offre.id).subscribe(res => {
      this.questions = res.sort((q1, q2) => q1.ordre - q2.ordre)
      //On filtre les questions en fonction du mode dans lequel on est
      if (this.mode == 'creation') {
        this.questions = this.questions.filter(q => q.topQuestCrea != ConstantsGlobals.TOP_QUESTION_ABSENTE)
      } else if (this.mode == 'modification') {
        this.questions = this.questions.filter(q => q.topQuestMod != ConstantsGlobals.TOP_QUESTION_ABSENTE)
      } else {
        this.questions = this.questions.filter(q => q.topQuestSupp != ConstantsGlobals.TOP_QUESTION_ABSENTE)
      }
    })

    this.offreService.getProfils(this.demande.offre.id).subscribe(res => {this.profils = res;
    })

    this.demande.profils = []
    this.demande.reponses = []

  }

  selectActeur($event: any) {
    this.demande.acteurBenef = $event
  }

  setCommentaire($event: any) {
    this.demande.commentaire = $event
  }

  updateProfils($event: DoubleSelectEvent) {
    let profils: Profil[] = []
    $event.selectedItems.forEach(item => {
      profils.push(new Profil(item.idItem, item.selected, item.lblItem))
    })
    this.demande.profils = profils
  }

  updateReponses($event: Reponse[]) {
    this.demande.reponses = $event
  }

  updatePj($event: File[]) {
    this.demande.piecesJointes = $event
  }

  isInvalid(): boolean {
    if (this.stepper) {
      return (this.stepper._getFocusIndex() == 0 && !this.choixOffreFormControl.valid) ||
          (this.stepper._getFocusIndex() == 1 && this.demande.reponses.some(r => !r.reponse && r.obligatoire))
    }
    return true
  }

  saveDemande() {
    this.demande.topDem = this.mode == 'creation' ? ConstantsGlobals.TOP_CREATION : this.mode == 'modification' ? ConstantsGlobals.TOP_MODIFICATION : ConstantsGlobals.TOP_SUPPRESSION
    this.demandeService.saveDemandeOffre(this.demande).subscribe(() => {this.snackBar.open("La demande d'offre a été sauvegardée.", "OK", {duration: 5 * 1000})
      this._location.back()
    })
  }
}
