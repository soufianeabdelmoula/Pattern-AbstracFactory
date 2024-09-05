package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.exception.rest.handler.AuthenticationException;

import javax.naming.directory.DirContext;
import java.util.Map;

public interface AuthentificationADService {

    void cleanUpSession();

    void setLdapContext(DirContext contextSource);

    /**
     * Le service d'authentification via un JWT
     * @param userName le nom d'utilisateur (login)
     * @param password le mot de passe
     * @return un le token sous forme d'un objet clé et valeur
     * @throws AuthenticationException
     */
    Map<String, String> authenticationUtilisateur(String userName, String password) throws AuthenticationException;
}
