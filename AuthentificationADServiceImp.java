package fr.vdm.referentiel.refadmin.service.impl;
import fr.vdm.referentiel.refadmin.exception.rest.handler.AuthenticationException;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.service.ActeurService;
import fr.vdm.referentiel.refadmin.service.AuthentificationADService;
import fr.vdm.referentiel.refadmin.service.ContexteActeurService;
import fr.vdm.referentiel.refadmin.service.WorkflowHabilitationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@Log4j2
public class AuthentificationADServiceImp implements AuthentificationADService {

    @Value("${jwt.jwtExpirationMs}")
    private Long jwtExpirationMs;
    private DirContext contextSource;

    private final ActeurService acteurService;
    private final ActiveDirectoryLdapAuthenticationProvider authenticationProvider;
    private final JwtEncoder jwtEncoder;

    @Autowired
    private ContexteActeurService ContexteActeurService;

    public AuthentificationADServiceImp(
            ActeurService acteurService,
            ActiveDirectoryLdapAuthenticationProvider authenticationManager,
            JwtEncoder jwtEncoder, WorkflowHabilitationService workflowHabilitationService) {
        this.acteurService = acteurService;
        this.authenticationProvider = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public void cleanUpSession() {
        // clear cache when the user is logout
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ActeurVue acteurVue = this.acteurService.getActeurVueByLogin(username);
        ContexteActeurService.clearCache(acteurVue.getIdActeur());
        if (this.contextSource != null) {
            try {
                // Fermeture de la session LDAP
                this.contextSource.close();
                log.info("Fermeture de la session LDAP");
            } catch (NamingException e) {
                // Gestion des exceptions, si nécessaire
                throw new RuntimeException(e);
            }
        }
    }

    // Méthode pour initialiser le contexte LDAP (vous pouvez injecter ce contexte lors de l'initialisation)
    @Override
    public void setLdapContext(DirContext contextSource) {
        this.contextSource = contextSource;
    }


    /**
     * Cette methode permet de générer le JWT
     * @param authentication
     * @return
     */
    private Map<String, String> generateToken(Authentication authentication) {
        log.info("Début de la génération du jeton d'authentification.");


        List<String> roles = new ArrayList<>(
                Arrays.asList(
                        authentication
                                .getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(","))
                                .split(",")
                )
        );

        LdapUserDetails userDetails = (LdapUserDetails) authentication.getPrincipal();

        Instant instant = Instant.now();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuedAt(instant)
                .expiresAt(instant.plus(jwtExpirationMs, ChronoUnit.MILLIS))
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .build();

        JwtEncoderParameters jwtEncoderParameters =
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS512).build(),
                        jwtClaimsSet
                );

        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        log.info("Le jeton d'authentification a été généré avec succès");

        return Collections.singletonMap("access-token", jwt);

    }

    public Map<String, String> authenticationUtilisateur(String userName, String password) throws AuthenticationException {
        Map<String, String> token;

        ActeurVue acteurVue = this.acteurService.getActeurVueByLogin(userName);

        if (acteurVue != null){
            log.info(String.format("Votre login %s  existe.", userName) );

            try {
                Authentication authentication = authenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(userName, password)
                );

                // Authentification réussie
                log.info(String.format("Authentification réussie %s.", userName));
                ContexteActeurService.buildValidationOffreDto(acteurVue.getIdActeur());
                ContexteActeurService.buildValidationActeurDto(acteurVue.getIdActeur());

                token = this.generateToken(authentication);
            } catch (Exception e) {
                // Gérer l'erreur d'authentification, par exemple :
                System.out.println("Erreur d'authentification : " + e.getMessage());
                throw new AuthenticationException("Erreur d'authentification : " + e.getMessage());
            }

        }else {
            log.info(String.format("Erreur : Votre login %s  n'existe pas.", userName) );
            throw new AuthenticationException(String.format("Erreur : Votre login %s  n'existe pas.", userName) );
        }

        return token;

    }

}
