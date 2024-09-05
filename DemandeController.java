package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.exception.rest.handler.DemandeActeurException;
import fr.vdm.referentiel.refadmin.service.DemandeActeurService;
import fr.vdm.referentiel.refadmin.service.DemandeDroitService;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/demande")
public class DemandeController {

    @Autowired
    private DemandeDroitService demandeDroitService;

    @Autowired
    private DemandeActeurService demandeActeurService;

    private static final Logger K_LOGGER = LoggerFactory.getLogger(DemandeController.class);

    @PostMapping("/offre")
    public ResponseEntity<Void> saveDemandeOffre(@RequestBody DemandeDroitDto demandeDroitDto) throws ServiceException {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        this.demandeDroitService.saveDemandeHabilitation(demandeDroitDto,login);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/offre/etape")
    public ResponseEntity<Void> saveEtapeDemandeOffre(@RequestBody EtapeDemDroitDto etapeDemDroitDto) throws  ServiceException{
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        this.demandeDroitService.saveEtapeDemandeDroit(etapeDemDroitDto, login);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/offre/{idDemande}")
    public ResponseEntity<DemandeDroitDto> getDemandeDroitById(@PathVariable Long idDemande) {
        return new ResponseEntity<>(this.demandeDroitService.getDemandeById(idDemande), HttpStatus.OK);
    }

    @PostMapping("/acteur")
    public ResponseEntity<Void> saveDemandeActeur(@RequestBody DemandeActeurDto demandeActeurDto) throws ServiceException, DemandeActeurException {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        this.demandeActeurService.saveDemandeActeur(demandeActeurDto, login);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/acteur/etape")
    public void saveEtapeDemandeActeur(@RequestBody EtapeDemActeurDto etapeDemActeurDto) throws ServiceException {
        demandeActeurService.saveEtapeDemandeComplete(etapeDemActeurDto);
    }

    @GetMapping("/reponse/{idDemande}")
    public ResponseEntity<List<ReponseDto>> getReponseByIdDemand(@PathVariable Long idDemande) {
        return new ResponseEntity<>(this.demandeDroitService.findReponseByIdDemande(idDemande), HttpStatus.OK);
    }

    @GetMapping("/demande-acteur/{idDemandeActeur}")
    public ResponseEntity<DemandeActeurDto> getDemandeActeurByIdDemandeAc(@PathVariable Long idDemandeActeur) {
        return new ResponseEntity<>(this.demandeActeurService.findDemandeActeurByIdDemandeAc(idDemandeActeur), HttpStatus.OK);
    }
}