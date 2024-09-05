import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OffreService } from 'src/app/shared/services/offre.service';

@Component({
  selector: 'app-acteurs-habilites',
  templateUrl: './acteurs-habilites.component.html',
  styleUrls: ['./acteurs-habilites.component.scss']
})
export class ActeursHabilitesComponent implements OnInit{

  columns = {};
  data : any[] = [];
  idOffre: number;
  constructor(private offreService: OffreService,
              private route: ActivatedRoute) {}

  ngOnInit(): void {

    this.route.params.subscribe(params => {
      this.columns = {'nom': 'Nom', 'prenom': 'Prénom', 'profilsString': 'Profil'};
      this.idOffre = params['idOffre'];
      this.offreService.getActeursHabilites(this.idOffre).subscribe(response => {
        this.data = response;
      });
    });
  }

  exportCsv() {
    this.offreService.exportCsv(this.idOffre).subscribe((data: Blob) => {
      const blobUrl = URL.createObjectURL(data);
      const link = document.createElement('a');
      document.body.appendChild(link);
      link.href = blobUrl;
      link.download = 'file.csv';
      link.click();

      document.body.removeChild(link);
      URL.revokeObjectURL(blobUrl);
    })
  }

  onCheckboxChange(event: any) {
    if (event.checked) {
      this.columns = {'nom': 'Nom', 'prenom': 'Prénom', 'profilsString': 'Profil', 'finHisto': 'Droit supprimé le '};
      this.offreService.getHistoriqueActeursHabilites(this.idOffre).subscribe(response => {
        this.data = response;
      });
    } else {
      this.ngOnInit();

    }
  }

}
