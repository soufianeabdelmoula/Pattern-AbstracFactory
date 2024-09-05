package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

import java.util.List;

public interface DemandeDroitService {
    StatutDemande createDem(ActeurVue acteurvue, Acteur acteur, DemandeDroitDto contents, Offre offre, StatutTache statutTache, DemandeDroit demande, String login) throws ServiceException;

    TacheHabilitation saveTacheHabilitation(DemandeDroit demande, StatutTache statutTache, Long niveau);

    DemandeDroitDto getDemandeById(Long idDemande);

    Long saveDemandeHabilitation(DemandeDroitDto contents, String login) throws ServiceException;

    void saveEtapeDemandeDroit(EtapeDemDroitDto etapeDemDroitDto, String login) throws ServiceException;

    List<ActeurHabiliteDto> findByIdOffre(final Long idOffre);

    byte[] exportActeursHabiltesCsvFile(Long idOffre);

    List<ReponseDto> findReponseByIdDemande(Long idDemande);

    boolean existeDemandeDroit(Long idActeur, Long idOffre, List<String> statutCode);

    EtapeDemDroit saveEtapeDemandeDroit(String contentsCommentaire, String login, DemandeDroit demande, String typeEtape);

    List<ActeurHabiliteDto> findHistoriqueHabilitationByIdOffre(final Long idOffre);

    EtapeDemDroit findFirstEtape(Long id);

    void createEtpDemDroit(final DemandeDroit demandeDroit,
                           final DemandeDroitDto demandeDto, final UtilisateurDto utilisateur,
                           final String typeEtape, final Boolean validation);

    DemandeDroitDto initDemandeSimplifie(final Long idActeur,
                                         final Long idOffre, final String type, final String commentaire);

    DemandeDroitDto createSousDemande(OffreDto offre, TacheHabilitation tacheParent, UtilisateurDto userDemandeur);

    public DemandeDroitDto saveDemandeAuto(final long idActeur,
                                           final long idOffre, final String commentaire, final char typeDemande) throws ServiceException;

    DemandeDroit save(DemandeDroitDto demandeDto,
                      UtilisateurDto utilisateur, char typeEtape,
                      Boolean validation, Boolean simplifie,
                      Long idTache, Boolean importMasse) throws ServiceException;

    void cloturerDemande(long idActeur, Long idOffre,
                         Long idDemande);
}

