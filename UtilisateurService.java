package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.UtilisateurDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface UtilisateurService {

    /**
     * Retourne l'utilisateur connecté
     * @return l'utilisateur connecté
     */
    UtilisateurDto getUser();

    /**
     * Retourne les informations d'authentication de lutilisateur
     * @return les informations d'authentication de lutilisateur
     */
    Authentication getAuthentication();

    /**
     * Retourne l'etat d'authentication de lutilisateur
     * @return l'etat d'authentication de lutilisateur
     */
    Boolean isAuthenticated();

    /**
     * Retourne la liste des roles de l'utilisateur connecté
     * @return la liste des roles de l'utilisateur connecté
     */
    List<String> getRoles();

    /**
     * Convertir la liste des roles en tableau de GrantedAuthority
     * @return un tableau de GrantedAuthority
     */
    GrantedAuthority [] getGrantedAuthorities();

    Boolean hasPermission(String permission);



}
