package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.ChampTechniqueDto;
import fr.vdm.referentiel.refadmin.dto.ExtensionMessagerieDto;
import fr.vdm.referentiel.refadmin.dto.StatutWorkFlowDto;
import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import fr.vdm.referentiel.refadmin.model.StatutDemande;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

public interface WorkflowActeurService {

    StatutDemande createDemande(DemandeActeur demande, boolean simplifie);

    StatutDemande createDemande(DemandeActeur demande, boolean simplifie, boolean isImport);

    StatutWorkFlowDto completerTache(Long idTache, boolean valide,
                                     DemandeActeur demande,
                                     ChampTechniqueDto champTechnique,
                                     ExtensionMessagerieDto messagerie, final String oldLogin,
                                     String oldEmail, final String oldAffectation) throws ServiceException;

    StatutWorkFlowDto completerTache(Long idTache, boolean valide, DemandeActeur demande,
                                     ChampTechniqueDto champTechnique,
                                     ExtensionMessagerieDto messagerie, String oldLogin,
                                     String oldEmail, String oldAffectation, boolean isImport) throws ServiceException;


    void cloturerDemande(long idActeur, Long idDemande);

    void deleteActeur(Long idActeur, Long idDemande) throws ServiceException;
}
