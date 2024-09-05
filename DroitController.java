package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.DroitDto;
import fr.vdm.referentiel.refadmin.service.DroitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/droit/")
public class DroitController {

    private static final Logger K_LOGGER = LoggerFactory.getLogger(DroitController.class);

    @Autowired
    private DroitService droitService;

    @GetMapping("/")
    public ResponseEntity<DroitDto> getDroitById(@RequestParam Long idDroit) {
        return new ResponseEntity<>(this.droitService.getDroitById(idDroit), HttpStatus.OK);
    }
}