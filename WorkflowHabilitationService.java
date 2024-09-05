package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.RegleValDroit;
import fr.vdm.referentiel.refadmin.model.StatutDemande;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

import java.util.List;

public interface WorkflowHabilitationService {

    List<RegleValDroit> getRegleValDroitOffre(Long idOffre);

    List<RegleValDroit> getRegleValDroitOffreTechnique(Long idOffre);

    List<RegleValDroit> getRegleValDroitOffreCellule(Long idOffre, String codeCellule);

    List<TacheHabilitationDto> findTachesHabilitation(final UtilisateurDto user);

    List<TacheActeurDto> findTachesActeur(final UtilisateurDto user);

    String calculerStatutDemande(Long idOffre, String statutDemande, String cellule, String statutTache, String loginValideur);

    StatutDemande completerTache(Long idTache, boolean valide, DemandeDroitDto demande) throws ServiceException;

    StatutDemande createDemande(DemandeDroitDto demande,
                                OffreDto offreDto, boolean simplifie);

    void cloturerDemandeByIdActeurBeneficiaire(long idActeur);

}