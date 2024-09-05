import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {OffreService} from 'src/app/shared/services/offre.service';
import {RechercheOffresComponent} from '../recherche-offres/recherche-offres.component';
import {ActivatedRoute, Router} from '@angular/router';
import {SearchActeurDialogComponent} from 'src/app/shared/dialog/search-acteur-dialog/search-acteur-dialog.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {EMPTY, mergeMap, of} from 'rxjs';
import {ActeurService} from 'src/app/shared/services/acteur.service';

@Component({
  selector: 'app-creer-offre',
  templateUrl: './creer-offre.component.html',
  styleUrls: ['./creer-offre.component.scss']
})
export class CreerOffreComponent implements OnInit{
  form!: FormGroup;
  columns = {'checkbox': '', 'ordre': 'Ordre', 'libelle': 'Nom', 'description': 'Description'};
  private selectedOffresIds: number[] = [];
  data: any[];
  idOffre: number;
  isEditMode: boolean = false;
  SupModMap!: Map<string, number>;
  SupModMapKeys: any[];


  constructor(private offreService: OffreService, private formBuilder: FormBuilder,
              private dialog: MatDialog, private route: ActivatedRoute,
              private router: Router, private _snackBar: MatSnackBar, private acteurService: ActeurService) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.idOffre = params['idOffre'];

      this.isEditMode = this.router.url.includes('/afficher-offre');
      this.initForm();
      if (this.isEditMode) {
        this.getOfferAndSetForm();
      }
    });
    const SupModList = ["Collectivité", "Délégation générale", "Direction", "Pôle", "Service", "Division",
                      "Subdivision niv. 6", "Subdivision niv. 7", "Subdivision niv. 8", "Subdivision niv. 9", "Subdivision niv. 10",
                      "Unité de zonage niv. 11", "Unité de zonage niv. 12", "Unité de zonage niv. 13", "Unité de zonage niv. 14"];

    this.SupModMap = new Map(SupModList.map((item, index) => [item, index - 1]));
  }

  initForm() {
    this.form = this.formBuilder.group({
      id: [null],
      questions: this.formBuilder.array([]),
      adGroup: this.formBuilder.group({
        groupePrincipal: this.formBuilder.group({
          dn: [''],
          description: [''],
        }),
        objectClassList: [[]],
        groupesSecondaires: this.formBuilder.array([])
      }),
      libelle: [{value: '', disabled: this.isEditMode}, Validators.required],
      codeOffre: [{value: '', disabled: this.isEditMode}, Validators.required],
      nomResponsable: [{value: '', disabled: this.isEditMode}],
      responsable: [],
      description: [{value: '', disabled: this.isEditMode}, Validators.required],
      ad: [{value: false, disabled: this.isEditMode}],
      topSecondaire: [{value: false, disabled: this.isEditMode}],
      suppressionAuto: [{value: '', disabled: this.isEditMode}],
      nbJourSuppAuto: [{value: 0, disabled: this.isEditMode}],
      nivSupp: [{value: '', disabled: this.isEditMode}],
      nivMod: [{value: '', disabled: this.isEditMode}],
      topAccord: [{value: false, disabled: this.isEditMode}],
      label:  [{value: '', disabled: this.isEditMode}],
      transverse: [{value: false, disabled: this.isEditMode}],
      metier: [{value: false, disabled: this.isEditMode}],
    })
  }

  getOfferAndSetForm() {
    this.offreService.getOfferById(this.idOffre).pipe(
      mergeMap((offer) => {
        const responsableId = offer.responsable;
        this.fillForm(offer);

        if (responsableId != null) {
          return this.acteurService.getActeur(responsableId).pipe(
            mergeMap((acteur) => {

              this.form.patchValue({
                nomResponsable: acteur.prenom + ' ' + acteur.nom,
              });
              return of(acteur);
            })
          );
        }
        return EMPTY
      })
    ).subscribe(offer => {});
  }

  fillForm(offer: any) {
    Object.keys(this.form.controls).forEach(controlName => {
      if (offer.hasOwnProperty(controlName)) {
        if (controlName === 'questions' && offer[controlName].length !== 0) {
          const questionsGroup = this.formBuilder.group(offer[controlName]);
          this.form.setControl(controlName, questionsGroup);
        } else {
          this.form.get(controlName).patchValue(offer[controlName]);
        }
      }
    });
  }

  saveOffre() {
    this.offreService.saveOffre(this.form.value).subscribe(data => {
      this.openSnackBar();
      this.router.navigate(['/offre/afficher-offre/', data.id]);
    })
  }

  updateQuestions(questions: FormArray) {
    this.form.setControl("questions", questions);
  }

  updateAdGroup(adGroup: FormGroup) {

    const adGroupForm = this.form.get('adGroup') as FormGroup;
    console.log(adGroup)
    const groupesSecondairesArray = adGroupForm.get('groupesSecondaires') as FormArray;

    while (groupesSecondairesArray.length !== 0) {
      groupesSecondairesArray.removeAt(0);
    }

    adGroup.get('groupesSecondaires').value.forEach((groupe: any) => {
      groupesSecondairesArray.push(this.formBuilder.group({
        dn: [groupe.dn],
        description: [groupe.description],
      }));
    });

    adGroupForm.patchValue({
      groupePrincipal: adGroup.get('groupePrincipal').value,
    });
  }

  openDialog() {
    this.dialog.open(SearchActeurDialogComponent,
      {height: '70%', width: '50%'})
      .afterClosed().subscribe(acteur => {
      if (acteur) {
        this.form.patchValue({
          nomResponsable: acteur.prenom + ' ' + acteur.nom,
          responsable: acteur.idActeur
        });
      }
    })
  }

  updateCheckedElements(selectedRows: any[]) {
    this.selectedOffresIds = selectedRows.map(offre => offre.id);
  }

  chooseOffres() {
    const dialogRef = this.dialog.open(RechercheOffresComponent, {
      width: '100%',
      height: '90%',
      data: {
        usedOutside: true
      }
    });

    dialogRef.afterClosed().subscribe((offresSelected: any) => {
      this.data = offresSelected;

      this.data = this.data.map((obj, index) => {
        return { ...obj, ordre: index + 1, ...obj };
      });
    });
  }

  deleteSelectedOffres() {
    this.data = this.data?.filter(item => !this.selectedOffresIds.includes(item.id));
    this.data?.forEach((obj, index) => obj.ordre = index + 1);

    this.selectedOffresIds = [];
  }

  editOffre() {
    this.isEditMode = !this.isEditMode;
    this.enableFormControls();
  }

  endEditMode() {
    this.isEditMode = true;
    this.disableFormControls();
  }

  disableFormControls() {
    // Disable all form controls
    Object.keys(this.form.controls).forEach(controlName => {
      this.form.get(controlName)?.disable();
    });
  }

  enableFormControls() {
    // Enable all form controls
    Object.keys(this.form.controls).forEach(controlName => {
      this.form.get(controlName)?.enable();
    });
  }

  getList() {
    return Array.from(this.SupModMap.keys());
  }

  openSnackBar() {
    this._snackBar.open('Offre de service est sauvegardée', 'Fermer', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 4000
    });
  }
}
