package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

import java.util.List;

public interface PropagationLDAPService {

    /**
     * Propagation de la sauvegarde d'un droit pour un acteur, une offre et une
     * liste de profil donnés.
     *
     * @param acteurVue
     * @param dnProfilsActeur
     */
    void propagerSauvegardeDroitActeur(final ActeurVue acteurVue, List<String> dnProfilsActeur) throws ServiceException;

}
