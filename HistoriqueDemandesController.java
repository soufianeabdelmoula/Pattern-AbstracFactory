package fr.vdm.referentiel.refadmin.controller;


import fr.vdm.referentiel.refadmin.dto.EtapeDemActeurDto;
import fr.vdm.referentiel.refadmin.dto.FiltreHistoriqueDemandesDto;
import fr.vdm.referentiel.refadmin.dto.HistoriqueDemandeDroitDto;
import fr.vdm.referentiel.refadmin.dto.StatutDemandeDto;
import fr.vdm.referentiel.refadmin.service.HistoriqueDemandesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@Log4j2
public class HistoriqueDemandesController {

    private final HistoriqueDemandesService historiqueDemandesService;

    public HistoriqueDemandesController(HistoriqueDemandesService historiqueDemandesService) {
        this.historiqueDemandesService = historiqueDemandesService;
    }


    @PostMapping("filter-historique-demandes")
    public ResponseEntity<Page<HistoriqueDemandeDroitDto>> getAllDemandesHabilitationByFilter(
            @RequestBody FiltreHistoriqueDemandesDto filtreDto, Pageable pageable) {
        return new ResponseEntity<>(
                this.historiqueDemandesService.findAllDemandesHabilitationByFilter(filtreDto, pageable),
                HttpStatus.OK
        );
    }
    @PostMapping("filter-demandes-compte-acteur")
    public ResponseEntity<Page<HistoriqueDemandeDroitDto>> getAllDemandesCompteActeurByFilter(
            @RequestBody FiltreHistoriqueDemandesDto filtreDto, Pageable pageable) {
        return new ResponseEntity<>(
                this.historiqueDemandesService.findAllDemandesCompteActeurByFilter(filtreDto, pageable),
                HttpStatus.OK
        );
    }

    @GetMapping("suivi-demandes-compte-acteur")
    public ResponseEntity<List<EtapeDemActeurDto>> getEtapesFromIdDemandeActeur(@RequestParam Long idDemandeAct) {
        return new ResponseEntity<>(
                this.historiqueDemandesService.findAllEtapesFromIdDemandeActeur(idDemandeAct),
                HttpStatus.OK
        );
    }

    @GetMapping("statut-demande")
    public ResponseEntity<StatutDemandeDto> getStautDemandeByIdDemandeActeur(@RequestParam Long idDemandeAct) {
        return new ResponseEntity<>(
                this.historiqueDemandesService.findStautDemandeByIdDemandeActeur(idDemandeAct),
                HttpStatus.OK
        );
    }

    @GetMapping("/demandes-habilitation/export-csv")
    public ResponseEntity<byte[]> exportCsvHistoriqueDemandesHabilitation() {

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export-demandes-habilitation.csv");

        // Return CSV as a ResponseEntity
        return ResponseEntity.ok().headers(headers)
                .body(historiqueDemandesService.getExportCsvHistoriqueDemandesHabilitation());
    }

    @GetMapping("/demandes-compte-acteur/export-csv")
    public ResponseEntity<byte[]> exportCsvHistoriqueDemandesCompteActeur() {

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export-demandes-compte-acteur.csv");

        // Return CSV as a ResponseEntity
        return ResponseEntity.ok().headers(headers)
                .body(historiqueDemandesService.getExportCsvHistoriqueDemandesCompteActeur());
    }
}

