package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.ActeurValidationDto;
import fr.vdm.referentiel.refadmin.dto.OffresValidationDto;
import fr.vdm.referentiel.refadmin.dto.UtilisateurDto;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.LienProfilPermission;
import fr.vdm.referentiel.refadmin.model.Permission;
import fr.vdm.referentiel.refadmin.repository.ActeurVueRepository;
import fr.vdm.referentiel.refadmin.repository.LienProfilPermissionRepository;
import fr.vdm.referentiel.refadmin.repository.PermissionRepository;
import fr.vdm.referentiel.refadmin.service.ContexteActeurService;
import fr.vdm.referentiel.refadmin.service.UtilisateurService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Log4j2
public class UtilisateurServiceImpl implements UtilisateurService {
    @Autowired
    private ActeurVueRepository acteurVueRepository;

    @Autowired
    private LienProfilPermissionRepository lienProfilPermissionRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ContexteActeurService contexteActeurService;


    /**
     * {@inheritDoc}
     */
    @Override
    public UtilisateurDto getUser() {
        Authentication auth = getAuthentication();
        ActeurVue acteurVue = acteurVueRepository.findActeurVueByLogin(auth.getName());


        if (acteurVue != null && isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {

            OffresValidationDto offresValidationDto = contexteActeurService.buildValidationOffreDto(acteurVue.getIdActeur());
            ActeurValidationDto acteurValidationDto = contexteActeurService.buildValidationActeurDto(acteurVue.getIdActeur());

            //this.workflowService.findTachesHabilitation(user, new Date());

            return new UtilisateurDto(getGrantedAuthorities(), acteurVue, "", acteurValidationDto, offresValidationDto);
        }

        //Cette exception est utilisée pour signaler une absence
        // d'informations d'identification lors de l'authentification.
        throw new AuthenticationCredentialsNotFoundException("Aucune information d'identification trouvée.");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isAuthenticated() {
        return getAuthentication()!= null && getAuthentication().isAuthenticated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRoles() {
        Authentication auth = getAuthentication();

        if (isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken) && auth.getPrincipal() instanceof Jwt){

            Jwt jwt = (Jwt) this.getAuthentication().getPrincipal();

            // Récupération des claims du JWT
            return jwt.getClaimAsStringList("roles");
        }
        //Cette exception est utilisée pour signaler une absence
        // d'informations d'identification lors de l'authentification.
        throw new AuthenticationCredentialsNotFoundException("Aucune information d'identification trouvée.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrantedAuthority[] getGrantedAuthorities() {
        return   getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toArray(GrantedAuthority[]::new);
    }


    public Boolean hasPermission(String permission){
        //Récupération des profils RFA de l'acteur
        List<String> profilsRFA = getRoles().stream().filter(droit -> droit.toUpperCase().startsWith("RFA")).collect(Collectors.toList());

        for (String profil : profilsRFA){
            //On met en forme le nom du profil récupéré
            profil = profil.split(",")[0].substring(3);

            //On récupère les permissions de chaque profil
            List<Long> listIdPerm = lienProfilPermissionRepository.findAllByProfil(profil).stream().map(LienProfilPermission::getIdPerm).collect(Collectors.toList());
            List<Permission> permissions = new ArrayList<>();
            listIdPerm.forEach(id -> {
                permissions.add(permissionRepository.findPermissionById(id));
            });
            //Si on trouve la permission demandée
            if(permissions.stream().map(Permission::getCodeParam).collect(Collectors.toList()).contains(permission)){
                return true;
            };
        };

        //Si on n'a pas trouvé la permission demandée parmis tous les profils RFA de l'acteur
        return false;
    }

}
