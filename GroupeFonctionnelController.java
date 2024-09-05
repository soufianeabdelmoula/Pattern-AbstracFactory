package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.ActeurVueDto;
import fr.vdm.referentiel.refadmin.service.GroupeFonctionnelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/groupe-fonctionnel")
public class GroupeFonctionnelController {
    private static final Logger K_LOGGER = LoggerFactory.getLogger(GroupeFonctionnelController.class);


    private final GroupeFonctionnelService groupeFonctionnelService;

    public GroupeFonctionnelController(GroupeFonctionnelService groupeFonctionnelService) {
        this.groupeFonctionnelService = groupeFonctionnelService;
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllgroupesFonctionnels() {
        K_LOGGER.info("Récupération de tous les groupes fonctionnels");
        return new ResponseEntity<>(this.groupeFonctionnelService.findAll(), HttpStatus.OK);
    }
    @GetMapping("{idGrpFonc}/acteurs")
    public List<ActeurVueDto> getActeursGroupeFonctionnel(@PathVariable("idGrpFonc") Long idGrFonc) {
        return this.groupeFonctionnelService.getActeurs(idGrFonc);
    }

    @PostMapping("{idGrpFonc}/typeParametrage")
    public ResponseEntity<Void> setTypeParametrage(@RequestParam String typeParametrage, @PathVariable("idGrpFonc") Long idGrpFonc) {
        this.groupeFonctionnelService.setTypeParametrage(typeParametrage, idGrpFonc);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/acteurs")
    public ResponseEntity<List<ActeurVueDto>> addActeurGroupeFonctionnel(@RequestBody List<Long> selectedActeurs, @RequestParam Long idGrpFonc) {
        return new ResponseEntity<List<ActeurVueDto>>(this.groupeFonctionnelService.addActeurs(selectedActeurs, idGrpFonc),HttpStatus.OK) ;
    }


    @DeleteMapping("/{idActeur}")
    public ResponseEntity<Void> deleteActeur(@PathVariable Long idActeur, @RequestParam Long idGrp) {

        this.groupeFonctionnelService.deleteActeur(idGrp, idActeur);
        return new ResponseEntity<>(HttpStatus.OK);

    }

}