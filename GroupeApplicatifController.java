package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.ProfilDto;
import fr.vdm.referentiel.refadmin.service.GroupeApplicatifService;
import fr.vdm.referentiel.refadmin.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groupe-applicatif/")
public class GroupeApplicatifController {

    @Autowired
    PermissionService permissionService;

    @Autowired
    GroupeApplicatifService groupeApplicatifService;

    @GetMapping("profils-rfa")
    public ResponseEntity<List<ProfilDto>> getProfilsRfa() {
        return new ResponseEntity<>(this.permissionService.getProfilsRfa(), HttpStatus.OK);
    }


}