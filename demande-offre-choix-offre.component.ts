import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {NgxDropzoneChangeEvent} from "ngx-dropzone";
import {MatDialog} from "@angular/material/dialog";
import {SearchOffreDialogComponent} from "../../../../shared/dialog/search-offre-dialog/search-offre-dialog.component";
import {
  SearchActeurDialogComponent
} from "../../../../shared/dialog/search-acteur-dialog/search-acteur-dialog.component";
import Utils from "../../../../shared/utils/utils";
import {ActivatedRoute} from "@angular/router";
import {ActeurService} from "../../../../shared/services/acteur.service";
import {DroitService} from "../../../../shared/services/droit.service";

@Component({
  selector: 'app-demande-offre-choix-offre',
  templateUrl: './demande-offre-choix-offre.component.html',
  styleUrls: ['./demande-offre-choix-offre.component.scss']
})
export class DemandeOffreChoixOffreComponent implements OnChanges {
  @Input() formGroup: FormGroup;

  files: File[] = [];

  @Output() selectOffre = new EventEmitter;
  @Output() selectActeur = new EventEmitter;
  @Output() updatePj = new EventEmitter;
  @Output() setCommentaire = new EventEmitter;

  //Provisionné par le paramètre d'URL idActeur, uniquement dans la demande d'habilitation depuis la fiche acteur
  idActeur: number

  //Provisionné par le paramètre d'URL idDroit, uniquement dans le cas d'une modif ou d'une suppression
  idDroit: number


  constructor(private dialog: MatDialog, private route: ActivatedRoute, private acteurService: ActeurService, private droitService: DroitService) {
  }

  ngOnChanges() {
    this.idActeur = Number(this.route.snapshot.paramMap.get('idActeur'))
    if (this.idActeur) {
      this.acteurService.getActeur(this.idActeur).subscribe(acteur => {
        this.formGroup.controls['acteurControl'].patchValue(acteur ? Utils.getNom(acteur).toUpperCase() + ' ' + Utils.getPrenom(acteur) : '')
        this.selectActeur.emit(acteur)
      })
    }

    this.idDroit = Number(this.route.snapshot.paramMap.get('idDroit'))
    if (this.idDroit) {
      this.droitService.getDroitById(this.idDroit).subscribe(res => {
        this.formGroup.controls['acteurControl'].patchValue(res.acteur ? Utils.getNom(res.acteur).toUpperCase() + ' ' + Utils.getPrenom(res.acteur) : '')
        this.selectActeur.emit(res.acteur)
        this.formGroup.controls['offreControl'].patchValue(res.offre ? res.offre.libelle : '')
        this.selectOffre.emit(res.offre)
      })
    }
  }

  onSelect(event: NgxDropzoneChangeEvent) {
    this.files.push(...event.addedFiles);
    this.updatePj.emit(this.files)
  }

  onRemove(event: File) {
    this.files.splice(this.files.indexOf(event), 1);
    this.updatePj.emit(this.files)
  }

  openSearchOffreDialog() {
    this.dialog.open(SearchOffreDialogComponent, {height: '70%', width: '50%'}).afterClosed().subscribe(offre => {
      if (offre) {
        this.formGroup.controls['offreControl'].patchValue(offre ? offre.libelle : '')
        this.selectOffre.emit(offre)
      }
    })
  }

  openSearchActeurDialog() {
    this.dialog.open(SearchActeurDialogComponent, {height: '70%', width: '50%'}).afterClosed().subscribe(acteur => {
      if (acteur) {
        this.formGroup.controls['acteurControl'].patchValue(acteur ? Utils.getNom(acteur).toUpperCase() + ' ' + Utils.getPrenom(acteur) : '')
        this.selectActeur.emit(acteur)
      }
    })
  }

  comment($event: Event) {
    this.setCommentaire.emit(($event.target as HTMLInputElement).value)
  }
}
