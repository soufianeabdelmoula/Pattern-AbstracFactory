package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.ad.ActeurAD;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/acteur")
@Log4j2
@EnableSpringDataWebSupport
public class ActeurController {


    private final ActeurADService acteurADService;

    private final ActeurService acteurService;

    private final ActeurRhService acteurRhService;
    private final GroupeADService groupeADService;
    private final HistoriqueActeurService historiqueActeurService;
    private final ExpotAdService expotAdService;

    public ActeurController(ActeurADService acteurADService, ActeurService acteurService, GroupeADService groupeADService,
                            ActeurRhService acteurRhService, HistoriqueActeurService historiqueActeurService, ExpotAdService expotAdService) {
        this.acteurADService = acteurADService;
        this.acteurService = acteurService;
        this.groupeADService = groupeADService;
        this.acteurRhService = acteurRhService;
        this.historiqueActeurService = historiqueActeurService;
        this.expotAdService = expotAdService;
    }

    /**
     * Retourne l'ensemble des Acteurs de la Vue ACTEURVUE
     * @return list<ActeurVueDto>
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllActeurs(@RequestParam Optional<String> input, Pageable page) {
        log.info("get all acteurs ");
        return new ResponseEntity<>(this.acteurService.getAllActeurVue(input.orElse(null), page), HttpStatus.OK);
    }
    @GetMapping("")
    public List<ActeurVueDto> getActeurs() {
        log.info("get all acteurs ");
        return this.acteurService.getAllActeurVue();
    }

    /**
     * Cherche et retourne un DTO d'un acteur depuis l'AD à partir de son login
     * @param login Login de l'acteur
     * @return Un Dto ActeurAD
     */
    @GetMapping("/ad/login")
    public ResponseEntity<ActeurADDto> getActeurADByLogin(@RequestParam String login, @RequestParam(required = false) String type) {
        return new ResponseEntity<>(this.acteurADService.findByIdentifiant(login, type), HttpStatus.OK);
    }

    @GetMapping("/ad")
    public List<ActeurAD> getAllActeursAD(){
        log.info("get acteurs ");
        return this.acteurADService.getAllActeurAD();
    }

    @PostMapping("/ad/recherche")
    public RechercheResponse searchByFilters(@RequestBody RechercheRequest rechercheRequest) {
        log.info("Recherche des acteurs par plusieurs filtres");
        return expotAdService.getActeursADByFilters(rechercheRequest);
    }

    @PostMapping("/ad/export-csv")
    public ResponseEntity<byte[]> exportCsv(@RequestBody ExportFIleDto exportFIleDto) {

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export.csv");

        // Return CSV as a ResponseEntity
        return ResponseEntity.ok().headers(headers).body(expotAdService.exportCsvFile(exportFIleDto));
    }

    @GetMapping("/{idActeur}")
    public ResponseEntity<ActeurVueDto> getActeur(@PathVariable Long idActeur) {
        return new ResponseEntity<>(this.acteurService.getActeurVueById(idActeur), HttpStatus.OK);
    }


    @GetMapping("/droits")
    public ResponseEntity<List<DroitDto>> getDroits(@RequestParam Long idActeur) {
        return new ResponseEntity<>(this.acteurService.getDroits(idActeur), HttpStatus.OK);
    }

    @GetMapping("/groupes/{login}")
    public ResponseEntity<List<GroupeADDto>> getGroupes(@PathVariable String login) throws ServiceException {
        return new ResponseEntity<>(this.groupeADService.getGroupesActeur(login), HttpStatus.OK);
    }

    @PostMapping("/ad/password")
    public ResponseEntity<Void> updatePassword(@RequestBody LoginDto loginDto) {
        this.acteurADService.updatePassword(loginDto.getUserName(), loginDto.getPassword(), loginDto.getType());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/ad/desactiver/{login}")
    public ResponseEntity<Void> desactiver(@PathVariable String login, @RequestBody(required = false) String type) throws ServiceException {
        this.acteurADService.desactiver(login, type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/ad/activer/{login}")
    public ResponseEntity<Void> activer(@PathVariable String login, @RequestBody(required = false) String type) throws ServiceException {
        this.acteurADService.activer(login, type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rh/filtre")
    public ResponseEntity<?> getActeursRhByFiltre(@RequestParam(required = false) String nom, @RequestParam(required = false) String prenom, @RequestParam(required = false) String login, @RequestParam(required = false) String codeCellule, Pageable page) {
        return new ResponseEntity<>(this.acteurRhService.getActeursRhByFiltre(nom, prenom, login, codeCellule, page), HttpStatus.OK);
    }

    @GetMapping("/rh")
    public ResponseEntity<List<ActeurVueDto>> getActeursRhByInput(@RequestParam String input) {
        return new ResponseEntity<>(this.acteurRhService.getActeurRhByInput(input), HttpStatus.OK);
    }

    @PostMapping("/filtre-acteur")
    public ResponseEntity<Page<ActeurVueDto>> getAllActeursByFilters(@RequestBody FiltreHistoriqueDemandesDto filter, Pageable pageable) {
        return new ResponseEntity<>(this.acteurService.getAllActeursByFilters(filter, pageable), HttpStatus.OK);
    }

    @GetMapping("/acteurs-techniques")
    public ResponseEntity<?> getActeursTechniquesByFilter(
            @RequestParam String typoCompte,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String codeCellule,
            @RequestParam(required = false) Long idType,
            @RequestParam(required = false) Long idOffre,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String codeMega,
            @RequestParam(required = false) Long idDemandeur,
            Pageable page) {

        return new ResponseEntity<>(this.acteurService.getActeursTechniquesByFilter(typoCompte, nom, codeCellule, idType, idOffre, login, codeMega, idDemandeur, page), HttpStatus.OK);
    }

    @GetMapping("/demandeur")
    public ResponseEntity<ActeurVueDto> getDemandeur(@RequestParam Long idActeur) throws ServiceException {
        return new ResponseEntity<>(this.acteurService.getDemandeur(idActeur), HttpStatus.OK);
    }

    @PostMapping("/compte-technique")
    public ResponseEntity<Void> saveCompteTechnique(@RequestBody CreationCompteDto compte) throws ServiceException {
        this.acteurService.saveCompteTechnique(compte);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/{idActeur}")
    public ResponseEntity<Void> deleteActeur(@PathVariable Long idActeur) throws ServiceException {
        this.acteurService.deleteActeur(idActeur);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @GetMapping("/historique-acteur")
    public ResponseEntity<FicheHistoriqueActeurDto> getDetailHistoriqueActeur(@RequestParam Long idActeur) {
        return new ResponseEntity<>(this.historiqueActeurService.getDetailHistoriqueActeur(idActeur), HttpStatus.OK);
    }

    @GetMapping("/acteur/export-csv")
    public ResponseEntity<byte[]> exportCsvActeurs() {

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export-acteurs.csv");

        // Return CSV as a ResponseEntity
        return ResponseEntity.ok().headers(headers).body(acteurService.exportCsvFileActeurs());
    }

    @GetMapping("/acteur-techniques/export-csv")
    public ResponseEntity<byte[]> getExportActeursTechniques(@RequestParam String typeCompte) {

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export-acteurs.csv");

        // Return CSV as a ResponseEntity
        return ResponseEntity.ok().headers(headers).body(acteurService.getExportActeursTechniques(typeCompte));
    }

}