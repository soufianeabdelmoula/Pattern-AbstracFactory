package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.model.RoleActeur;

import java.util.List;

public interface RoleActeurService {

    /**
     * Permet de récupérer l'ensemble des roles acteurs classés selon leur
     * numéro d'ordre.
     * @return List<RoleActeur> la liste des roles acteurs demandes.
     */
    List<RoleActeur> findAll();

    /**
     * Selectionne l'id du role grâce à son code
     * @param code
     * @return
     */
    Long findByCode(final String code);
}
