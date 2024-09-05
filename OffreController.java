package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.ParametrageLdap;
import fr.vdm.referentiel.refadmin.service.BouquetService;
import fr.vdm.referentiel.refadmin.service.DemandeDroitService;
import fr.vdm.referentiel.refadmin.service.OffreService;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offres")
public class OffreController {

    private static final Logger K_LOGGER = LoggerFactory.getLogger(OffreController.class);

    private final OffreService offreService;
    private final DemandeDroitService demandeDroitService;

    private final BouquetService bouquetService;

    public OffreController(OffreService offreService, DemandeDroitService demandeDroitService, BouquetService bouquetService) {
        this.offreService = offreService;
        this.demandeDroitService = demandeDroitService;
        this.bouquetService = bouquetService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOffres(){
        K_LOGGER.info("Appel REST: {}/getAllOffre - OffreController", "/api/v1/offre");

        return new ResponseEntity<>(this.offreService.findAllOffres(), HttpStatus.OK);
    }

    @GetMapping("/{idOffre}/profils")
    public ResponseEntity<List<ProfilDto>> getProfilsByIdOffre(@PathVariable Long idOffre) {
        K_LOGGER.info("Appel REST: /api/v1/offre/profils/{} - OffreController", idOffre);

        return new ResponseEntity<>(this.offreService.findProfilsByIdOffre(idOffre), HttpStatus.OK);
    }

    @GetMapping("/{idOffre}")
    public OffreDto getOffre(@PathVariable Long idOffre) {
        return offreService.getOffre(idOffre);
    }

    @GetMapping("/{idOffre}/questions")
    public ResponseEntity<List<QuestionDto>> getQuestionsByIdOffre(@PathVariable Long idOffre) {
        K_LOGGER.info("Appel REST: /api/v1/offre/{}/questions - OffreController", idOffre);

        return new ResponseEntity<>(this.offreService.findQuestionsByIdOffre(idOffre), HttpStatus.OK);
    }

    @PostMapping
    public OffreDto createOffre(@RequestBody OffreRequestDto offreRequestDto) throws ServiceException {
        return offreService.saveOffre(offreRequestDto);
    }

    @PutMapping
    public OffreDto update(@RequestBody OffreRequestDto offreRequestDto) throws ServiceException {
        return offreService.saveOffre(offreRequestDto);
    }

    @DeleteMapping("/{offreId}")
    public void delete(@PathVariable Long offreId) {
        offreService.deleteById(offreId);
    }

    @PostMapping("/delete")
    public void deleteALL(@RequestBody List<Long> offresIdList) {
        offreService.deleteAllById(offresIdList);
    }

    @PostMapping("/by-filters")
    public Page<OffreDto> getOffresByFilters(@RequestBody OffreRequestDto offreRequestDto, Pageable page) {
        return offreService.getOffresByFilters(offreRequestDto, page);
    }

    @PostMapping("/groupes-applicatifs")
    public List<GroupeApplicatifDto> getGroupApplicatifListByFilters(@RequestBody GroupeApplicatifRequestDto  groupeApplicatifRequestDto) {
        return offreService.getGroupApplicatifListByFilters(groupeApplicatifRequestDto);
    }

    @GetMapping("/groupes-applicatifs")
    public List<GroupeApplicatifDto> getGroupeApplicatifList() {
        return offreService.getGroupApplicatifList();
    }

    @GetMapping("/{idOffre}/acteurs-habilites")
    public List<ActeurHabiliteDto> getActeursHabilites(@PathVariable("idOffre") Long idOffre) {
        return demandeDroitService.findByIdOffre(idOffre);
    }

    @GetMapping("/{idOffre}/acteurs-habilites/export-csv")
    public ResponseEntity<byte[]> exportActeursHabilites(@PathVariable("idOffre") Long idOffre) {
        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export.csv");

        // Return CSV as a ResponseEntity
        return ResponseEntity.ok().headers(headers).body(demandeDroitService.exportActeursHabiltesCsvFile(idOffre));
    }

    @GetMapping("{idOffre}/ParametrageLdap")
    public ParametrageLdap getParametrageLdapByIdOffre(@PathVariable Long idOffre) {
        return offreService.findParametrageLdapByIdOffre(idOffre);
    }

    @GetMapping("{idOffre}/groupe-principal")
    public RfsGroupeApplicationRequestDto getGroupePrincipalByIdOffre(@PathVariable Long idOffre) {
        return offreService.findGroupePrincipalByIdOffre(idOffre);
    }

    @GetMapping("/bouquet")
    public ResponseEntity<List<BouquetDto>> getAllBouquets() {
        return new ResponseEntity<>(this.bouquetService.getAllBouquets(), HttpStatus.OK);
    }

    @PostMapping("/bouquet")
    public ResponseEntity<Void> saveBouquet(@RequestBody List<BouquetDto> bouquets) {
        this.bouquetService.saveAll(bouquets);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/bouquet")
    public ResponseEntity<Void> deleteBouquet(@RequestParam Long idOffre) {
        this.bouquetService.deleteBouquet(idOffre);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{idOffre}/groupes-secondaires")
    public List<RfsGroupeApplicationRequestDto> getGroupesSecondairesByIdOffre(@PathVariable Long idOffre) {
        return offreService.findGroupesSecondairesByIdOffre(idOffre);
    }

    @GetMapping("/{idOffre}/historique-acteurs-habilites")
    public List<ActeurHabiliteDto> getHistoriqueActeursHabilites(@PathVariable("idOffre") Long idOffre) {
        return demandeDroitService.findHistoriqueHabilitationByIdOffre(idOffre);
    }

    @GetMapping("/offre/export-csv")
    public ResponseEntity<byte[]> getExportOffre() {
        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export.csv");

        // Return CSV as a ResponseEntity
        return ResponseEntity.ok().headers(headers).body(offreService.getExportOffre());
    }

}