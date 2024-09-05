package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.model.ParametreWorkflowDroit;
import fr.vdm.referentiel.refadmin.repository.ParametreWorkflowDroitRepository;
import fr.vdm.referentiel.refadmin.service.CelluleService;
import fr.vdm.referentiel.refadmin.service.ParametreWorkflowDroitService;
import fr.vdm.referentiel.refadmin.utils.CelluleUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ParametreWorkflowDroitServiceImpl implements ParametreWorkflowDroitService {
    @Autowired
    private ParametreWorkflowDroitRepository parametreWorkflowDroitRepository;

    @Autowired
    CelluleService celluleService;

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
    public ParametreWorkflowDroit select(final Long idOffre, final String cle) {
        // on recupere la DG correspondant à cette cellule
        String coll = CelluleUtils.getColl(cle);
        String ssColl = CelluleUtils.getSsColl(cle);
        String cellule = CelluleUtils.getCode(cle);

        // on recupere la cle de la DG associée
        String cleDG = this.celluleService.findPere(coll, ssColl, cellule, 1);

        return parametreWorkflowDroitRepository.findParametreWorkflowDroitByCellule(idOffre, CelluleUtils.getCode(cleDG));
    }

}
