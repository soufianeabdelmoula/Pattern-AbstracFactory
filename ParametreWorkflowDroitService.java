package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.model.ParametreWorkflowDroit;

public interface ParametreWorkflowDroitService {

    /**
     * Sélection des parametres workflow s'appliquant pour une cellule et une
     * offre de service.
     * @param idOffre
     *            l'identifiant de l'offre
     * @param cle
     *            la cle de la cellule.
     * @return ParametreWorkflowDroit les parametre de gestion duworkflow pour
     *         l'offre et la DG.
     */
    public ParametreWorkflowDroit select(final Long idOffre, final String cle);
}
