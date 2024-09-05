import {AfterViewInit, Component, ElementRef, Input, OnChanges, OnInit, ViewChild} from '@angular/core';
import {NgxDropzoneChangeEvent} from "ngx-dropzone";
import {DemandeActeur} from "../../../../models/demande-acteur.model";
import {MatDialog} from "@angular/material/dialog";
import {
  RechercheActeurDemandeActeurDialogComponent
} from "../../../../shared/dialog/recherche-acteur-demande-acteur-dialog/recherche-acteur-demande-acteur-dialog.component";
import {Cellule} from "../../../../models/cellule.model";
import {CelluleService} from "../../../../shared/services/cellule.service";
import {Acteur} from "../../../../models/acteur.model";
import {BehaviorSubject, debounceTime, distinctUntilChanged, fromEvent, map} from "rxjs";
import {ActeurService} from "../../../../shared/services/acteur.service";

@Component({
  selector: 'app-demande-acteur-agent',
  templateUrl: './demande-acteur-agent.component.html',
  styleUrls: ['./demande-acteur-agent.component.scss']
})
export class DemandeActeurAgentComponent implements AfterViewInit, OnChanges {

  @Input() demande: DemandeActeur
  files: File[] = []
  matricule: string;

  cellule: Cellule = new Cellule()
  celluleTerrain: Cellule = new Cellule()

  @ViewChild('search', {static: false}) search: ElementRef
  options = new BehaviorSubject<Acteur[]>([])
  options$ = this.options.asObservable()

  @Input() isView: boolean = false;

  @Input() celluleActeur: Cellule
  @Input() celluleTerrainActeur: Cellule


  constructor(private dialog: MatDialog, private celluleService: CelluleService, private acteurService: ActeurService) {
  }

  ngOnChanges() {
    if (this.isView){
      this.matricule = this.demande.matricule
      this.cellule = this.celluleActeur
      this.celluleTerrain = this.celluleTerrainActeur
    }
  }

  ngAfterViewInit() {
    if (!this.isView){
      fromEvent(this.search.nativeElement, 'keyup')
        .pipe(map((event: any) => (event.target as HTMLInputElement).value),
          debounceTime(500),
          distinctUntilChanged())
        .subscribe(input => {
          this.demande.clear()
          if (input) this.searchActeurByInput(input)
          else this.options.next([])
        })
    }
  }

  onSelect(event: NgxDropzoneChangeEvent) {
    this.files.push(...event.addedFiles);
    this.demande.piecesJointes = this.files
  }

  onRemove(event: File) {
    this.files.splice(this.files.indexOf(event), 1);
    this.demande.piecesJointes = this.files
  }

  updateLogin() {
    if (this.demande.nom && this.demande.prenom) {
      let login = this.demande.prenom.slice(0, 1) + this.demande.nom

      this.demande.login = login.slice(0, 20).toLowerCase()
    }
  }

  openDialog() {
    if (!this.isView) this.dialog.open(RechercheActeurDemandeActeurDialogComponent, {
      height: '70%',
      width: '50%'
    }).afterClosed().subscribe(res => {
      if (res) {
        this.updateActeur(res)
      }
    })
  }

  updateCelluleOfficielle($event: Cellule) {
    this.cellule = $event
    this.demande.cellule = $event.code

  }

  updateCelluleTerrain($event: Cellule) {
    this.celluleTerrain = $event
    this.demande.celluleTerrain = $event.code
  }

  updateActeur(res: any) {
    this.demande.clear()
    this.demande.nom = res.nom
    this.demande.prenom = res.prenom
    this.demande.nomMarital = res.nomMarital
    this.demande.nomUsuel = res.nomUsuel
    this.demande.prenomUsuel = res.prenomUsuel
    this.matricule = res.matricule ? res.matricule : res.idta + res.idtn.padStart(4, '0')
    this.demande.idta = res.idta
    this.demande.idtn = res.idtn
    this.demande.celluleDet = res.celluleDet
    this.cellule = null
    this.celluleService.findCelluleByCode(res.cellule).subscribe(cell => {
      this.updateCelluleOfficielle(cell)
    })
    this.updateLogin()
  }

  private searchActeurByInput(input: string) {
    this.acteurService.getActeursRhByInput(input).subscribe(res => {
      this.options.next(res)
    })
  }
}
