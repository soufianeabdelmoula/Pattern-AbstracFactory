package fr.vdm.referentiel.refadmin.controller;

import com.unboundid.util.Nullable;
import fr.vdm.referentiel.refadmin.dto.TacheActeurDto;
import fr.vdm.referentiel.refadmin.dto.TacheHabilitationDto;
import fr.vdm.referentiel.refadmin.dto.UtilisateurDto;
import fr.vdm.referentiel.refadmin.model.TacheActeur;
import fr.vdm.referentiel.refadmin.service.TacheActeurService;
import fr.vdm.referentiel.refadmin.service.TacheHabilitationService;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/")
@Log4j2
public class TacheController {

    private final TacheHabilitationService tacheHabilitationService;
    private final TacheActeurService tacheActeurService;

    public TacheController(TacheHabilitationService tacheHabilitationService, TacheActeurService tacheActeurService) {
        this.tacheHabilitationService = tacheHabilitationService;
        this.tacheActeurService = tacheActeurService;
    }

    @GetMapping("taches-acteurs")
    public Page<TacheActeurDto> getTachesActeurs(Pageable pageable) {
        return tacheActeurService.getTachesActeurs(pageable);
    }
/*    @GetMapping("taches-acteurs/by-statut")
    public ResponseEntity<?> getTachesActeursByStatut(@RequestParam List<String> statut, Optional<String> loginAssigne, Optional<String> input, Pageable pageable) {
        return new ResponseEntity<>(tacheActeurService.getByInputAndStatut(statut, loginAssigne.orElse(null), input.orElse(null), pageable), HttpStatus.OK);

    }*/

    @GetMapping("taches-acteurs/mes-taches")
    public Page<TacheActeurDto> getTachesActeursByStatutAndReserved(@RequestParam List<String> statut,
                                                                    @RequestParam String loginAssigne,
                                                                    Optional<String> input,
                                                                    Pageable pageable) {
        return tacheActeurService.getAllTaskByInputAndIdStatutAndForMyTask(statut, loginAssigne, input.orElse(null), pageable);
    }
    @GetMapping("taches-acteurs/by-statut")
    public Page<TacheActeurDto> getTachesActeursByStatut(@RequestParam List<String> statut,
                                                         @RequestParam String loginAssigne,
                                                         Optional<String> input,
                                                         Pageable pageable) throws ServiceException {
        return tacheActeurService.getAllTaskByInputAndIdStatutForAll(statut, loginAssigne, input.orElse(null), pageable);
    }
    @GetMapping("taches-acteurs/{idTacheActeur}")
    public TacheActeurDto getTacheActeur(@PathVariable Long idTacheActeur) {
        return tacheActeurService.getById(idTacheActeur);
    }
    @GetMapping("taches-acteurs/by-assigne")
    public List<TacheActeur> getTachesActeursByAssignId(@RequestParam Long assigneId) {
        return tacheActeurService.getByAssigneId(assigneId);
    }

    @GetMapping("taches-habilitations")
    public List<TacheHabilitationDto> getTachesHabilitations(Pageable pageable) {
        return tacheHabilitationService.getTachesHabilitations(pageable);
    }
    @GetMapping("taches-habilitations/by-statut")
    public ResponseEntity<?> getTachesHabilitationsByStatut(@RequestParam List<String> statut, @RequestParam String loginAssigne, Optional<String> input, Pageable pageable) throws ServiceException {
        return new ResponseEntity<>(tacheHabilitationService.getByInputAndStatut(statut, loginAssigne, input.orElse(null), pageable), HttpStatus.OK);
    }
    @GetMapping("taches-habilitations/mes-taches")
    public ResponseEntity<?> getTachesHabilitationByStatutAndReserved(@RequestParam List<String> statut, @RequestParam String loginAssigne, Optional<String> input, Pageable pageable) {
        return new ResponseEntity<>(tacheHabilitationService.getByInputAndStatutForMyTask(statut, loginAssigne, input.orElse(null), pageable), HttpStatus.OK);
    }
    @GetMapping("taches-habilitations/by-assigne")
    public List<TacheHabilitationDto> getTachesHabilitationsByAssigneId(@RequestParam Long assigneId) {
        return tacheHabilitationService.getByAssigneId(assigneId);
    }

    @PostMapping("taches-habilitations/valider")
    public void validerTachesHabilitations(@RequestBody List<Long> ids) {
        tacheHabilitationService.validerTachesHabilitations(ids);
    }
    @PostMapping("taches-habilitations/refuser")
    public void refuserTachesHabilitations(@RequestBody List<Long> ids) {
        tacheHabilitationService.refuserTachesHabilitations(ids);
    }

    @PostMapping("taches-habilitations/assigner")
    public void assignerTacheHabilitation(
            @RequestParam String loginAssigne,
            @RequestParam Long idTache,
            @RequestParam List<String> statutCode) throws ServiceException {
        tacheHabilitationService.assignerTacheHabilitation(loginAssigne, idTache, statutCode);
    }

    @PostMapping("taches-habilitations/liberer")
    public void libererTacheHabilitation(
            @RequestParam String loginAssigne,
            @RequestParam Long idTache,
            @RequestParam List<String> statutCode) throws ServiceException {
        tacheHabilitationService.libererTacheHabilitation(loginAssigne, idTache, statutCode);
    }

    @PostMapping("taches-habilitations/assignerTout")
    public void assignerToutesLesTacheHabilitation(
            @RequestParam String loginAssigne,
            @RequestParam List<Long> idTaches,
            @RequestParam List<String> statutCode) throws ServiceException {
        tacheHabilitationService.assignerToutesLesTacheHabilitation(loginAssigne, idTaches, statutCode);
    }

    @PostMapping("taches-acteurs/assigner")
    public void assignerTacheActeur(
            @RequestParam String loginAssigne,
            @RequestParam Long idTache,
            @RequestParam List<String> statutCode) throws ServiceException {
        tacheActeurService.assignerTacheActeur(loginAssigne, idTache, statutCode);
    }

    @PostMapping("taches-acteurs/liberer")
    public void libererTacheActeur(
            @RequestParam String loginAssigne,
            @RequestParam Long idTache,
            @RequestParam List<String> statutCode) throws ServiceException {
        tacheActeurService.libererTacheActeur(loginAssigne, idTache, statutCode);
    }

    @PostMapping("taches-acteurs/assignerTout")
    public void assignerToutesLesTacheActeur(
            @RequestParam String loginAssigne,
            @RequestParam List<Long> idTaches,
            @RequestParam List<String> statutCode) throws ServiceException {
        tacheActeurService.assignerToutesLesTacheActeur(loginAssigne, idTaches, statutCode);
    }

    @GetMapping("taches-acteurs/mes-taches-count")
    Long countTaskActeur(@RequestParam List<String> statut, String loginAssigne) {
        return tacheActeurService.countTaskActeur(loginAssigne, statut);
    }
    @GetMapping("taches-habilitations/mes-taches-count")
    Long countMyTaskhabilitations(@RequestParam List<String> statut, String loginAssigne) {
        return tacheHabilitationService.countMyTaskhabilitations(loginAssigne, statut);
    }

    @PostMapping("taches-acteurs/liberer-tout")
    public void libererToutesLesTacheActeurParUnAdmin(
            @RequestParam String username,
            @RequestParam List<Long> idTaches,
            @RequestParam List<String> statutCode) throws ServiceException {
        tacheActeurService.libererToutesLesTacheActeurParUnAdmin(username, idTaches, statutCode);
    }

    @PostMapping("taches-habilitations/liberer-tout")
    public void libererToutesLesTacheHabilitationParUnAdmin(
            @RequestParam String username,
            @RequestParam List<Long> idTaches,
            @RequestParam List<String> statutCode) throws ServiceException {
        tacheHabilitationService.libererToutesLesTacheHabilitationParUnAdmin(username, idTaches, statutCode);
    }

    @GetMapping("taches-habilitations/export-csv")
    public ResponseEntity<byte[]> getExportTacheHabilitation(@RequestParam List<String> statutCode){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export-demande-tache-habilitation.csv");

        return ResponseEntity.ok().headers(headers).body(this.tacheHabilitationService.getExportTacheHabilitation(statutCode));

    }
    @GetMapping("taches-acteurs/export-csv")
    public ResponseEntity<byte[]> getExportTacheActeur(@RequestParam List<String> statutCode){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export-demande-tache-habilitation.csv");

        return ResponseEntity.ok().headers(headers).body(this.tacheActeurService.getExportTacheActeur(statutCode));
    }

    @GetMapping(value="/taches-habilitations/taches-dispo", produces = "application/json")
    public Page<TacheHabilitationDto> getTachesDispo(@RequestParam List<String> statut,
                                                     @RequestParam String loginAssigne,
                                                     Optional<String> input,
                                                     Pageable pageable) throws ServiceException {
        // Appel de la méthode du service pour récupérer les tâches disponibles
        return tacheHabilitationService.findTachesDispo(statut,loginAssigne,input.orElse(null),pageable);
    }


}