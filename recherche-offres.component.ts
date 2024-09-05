import {Component, Inject, OnInit, Optional, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {OffreService} from 'src/app/shared/services/offre.service';
import {Router} from '@angular/router';
import {SearchActeurDialogComponent} from 'src/app/shared/dialog/search-acteur-dialog/search-acteur-dialog.component';
import {UserActions} from 'src/app/shared/components/tableau-generique/tableau-generique.component';
import {tap} from "rxjs";

@Component({
  selector: 'app-recherche-offres',
  templateUrl: './recherche-offres.component.html',
  styleUrls: ['./recherche-offres.component.scss']
})
export class RechercheOffresComponent implements OnInit{

  myForm!: FormGroup;
  dataList: any[];

  columns = {'checkbox': '', 'libelle': 'Nom', 'description': 'Description', 'label': 'DGA'};

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  usedOutside = false;
  private selectedOffresIds: any[] = [];
  page: any;


  constructor(private formBuilder: FormBuilder, private offreService: OffreService,
              private dialog: MatDialog,
              private router: Router,
              @Optional() private dialogRef: MatDialogRef<RechercheOffresComponent>,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: any) {
    if (data && data.usedOutside) {
      this.usedOutside = data.usedOutside;
    }
  }

  ngOnInit(): void {
    this.initForm();
  }


  initForm() {
    this.myForm = this.formBuilder.group({
      libelle: [''],
      codeMega: [''],
      label: [''],
      nomResponsable: [''],
      responsable: [],
      transverse: [false],
      metier: [false]
    })
  }onKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.search();
    }
  }


  search() {
    const formValue = this.myForm.value;
    // intialiser les valeurs à envoyer en fonction de l'état des cases cochées
    const metierValue = formValue.metier ? true : null;
    const transverseValue = formValue.transverse ? true : null;
    // la créaton d'un objet avec les valeurs du formulaire
    const filters = {
      libelle: formValue.libelle || null,
      codeMega: formValue.codeMega || null,
      label: formValue.label || null,
      nomResponsable: formValue.nomResponsable || null,
      responsable: formValue.responsable || null,
      metier: metierValue,
      transverse: transverseValue
    };
    this.offreService.getOffresByFilters(filters).subscribe((response: any) => {
      this.dataList = response;
    });
  }

  resetFilter() {
    this.initForm();
    this.search();
  }


  isButtonDisabled(): boolean {
    if (this.selectedOffresIds.length != 0) {
      return false;
    } else {
      return true;
    }
  }

  deleteOffres() {
    this.selectedOffresIds = this.selectedOffresIds.map(offre => offre);
    this.offreService.deleteOffres(this.selectedOffresIds).subscribe(res => {
      this.search();
      this.selectedOffresIds = [];
    });
  }

  chooseOffres() {
    this.dialogRef.close(this.selectedOffresIds);
  }

  openDialog() {
    this.dialog.open(SearchActeurDialogComponent,
        {height: '70%', width: '50%'})
        .afterClosed().subscribe(acteur => {
      if (acteur) {
        this.myForm.patchValue({
          nomResponsable: acteur.prenom + ' ' + acteur.nom,
          responsable: acteur.idActeur
        });
      }
    })
  }

  displayOffre(offre: any) {
    if (!this.usedOutside) {
      this.router.navigate(['/offre/afficher-offre/', offre.id]);
    }
  }

  updateCheckedElements(selectedRows: any[]) {
    this.selectedOffresIds = selectedRows;

  }

  selectOffre(event: any) {
    const action = event?.action;
    const offre = event?.obj;
    if (!this.usedOutside && action == UserActions.Select) {
      this.router.navigate(['/offre/afficher-offre/', offre.id]);
    }
  }

  pageEvent($event: PageEvent) {
    this.page = $event;

    this.offreService.getOffresByFilters(this.myForm.value, this.page).subscribe(res => {
      this.dataList = res;

    });
  }

  getExportOffre(){
    this.offreService.getExportOffreService()
      .pipe(
        tap((data: Blob) =>{
          const blobUrl = URL.createObjectURL(data)
          const link = document.createElement('a')
          document.body.appendChild(link)
          link.href = blobUrl
          link.download = "Export-offre-"+Date.now()+".csv"
          link.click()

          document.body.removeChild(link)
          URL.revokeObjectURL(blobUrl)
        })
      )
      .subscribe()
  }

}

