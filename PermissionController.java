package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.LienProfilPermissionDto;
import fr.vdm.referentiel.refadmin.dto.PermissionDto;
import fr.vdm.referentiel.refadmin.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {
    private static final Logger K_LOGGER = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        K_LOGGER.info("Récupération de toutes les permissions");
        return new ResponseEntity<>(this.permissionService.getAllPermissions(), HttpStatus.OK);
    }

    @GetMapping("/liens")
    public ResponseEntity<List<LienProfilPermissionDto>> getAllLiensProfilPermission() {
        K_LOGGER.info("Récupération de tous les liens entre les profils et les permissions");
        return new ResponseEntity<>(this.permissionService.getAllLiensProfilPermission(), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Void> saveAllLiensProfilPermission(@RequestBody List<LienProfilPermissionDto> liens) {
        K_LOGGER.info("Sauvegarde de tous les liens entre les profils et les permissions");
        this.permissionService.saveAllLiensProfilPermission(liens);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}