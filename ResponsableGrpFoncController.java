package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.ResponsableGrpFoncDto;
import fr.vdm.referentiel.refadmin.service.ResponsableGrpFonctionnelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/responsable-groupe-Fonctionnel")

public class  ResponsableGrpFoncController {
    private static final Logger K_LOGGER =  LoggerFactory.getLogger(GroupeFonctionnelController.class);

    @Autowired
    private ResponsableGrpFonctionnelService responsableGrpFonctionnelService ;
    @GetMapping("/all")
    public ResponseEntity<?> getAllgroupesFonctionnels(Pageable pageable) {
        K_LOGGER.info("Récupération de tous les groupes fonctionnels");
        return new ResponseEntity<>(this.responsableGrpFonctionnelService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ResponsableGrpFoncDto>> filterResponsables(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String responsable, Pageable pageable) {

        return new ResponseEntity<>(this.responsableGrpFonctionnelService.filterGrpFonc(nom,responsable, pageable), HttpStatus.OK);

    }

}
