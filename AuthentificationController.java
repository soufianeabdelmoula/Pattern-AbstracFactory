package fr.vdm.referentiel.refadmin.controller;


import fr.vdm.referentiel.refadmin.dto.UtilisateurDto;
import fr.vdm.referentiel.refadmin.exception.rest.handler.AuthenticationException;
import fr.vdm.referentiel.refadmin.service.AuthentificationADService;
import fr.vdm.referentiel.refadmin.service.impl.UtilisateurServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Log4j2
public class AuthentificationController {

    private final AuthentificationADService adService;
    private final UtilisateurServiceImpl utilisateurService;

    public AuthentificationController(AuthentificationADService adService, UtilisateurServiceImpl utilisateurService) {
        this.adService = adService;
        this.utilisateurService = utilisateurService;
    }


    @GetMapping("/profile")
    public Authentication getAuth(Authentication authentication){
        return authentication;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(String userName, String password) throws AuthenticationException {

        return new ResponseEntity<>(
                this.adService.authenticationUtilisateur(userName, password),
                HttpStatus.OK);
    }
    @GetMapping("/user-details")
    public UtilisateurDto getUser() {
        return utilisateurService.getUser();
    }
}
