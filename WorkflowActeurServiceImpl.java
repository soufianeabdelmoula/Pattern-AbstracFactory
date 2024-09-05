package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.ChampTechniqueDto;
import fr.vdm.referentiel.refadmin.dto.DroitDto;
import fr.vdm.referentiel.refadmin.dto.ExtensionMessagerieDto;
import fr.vdm.referentiel.refadmin.dto.StatutWorkFlowDto;
import fr.vdm.referentiel.refadmin.mapper.DemandeMapper;
import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import fr.vdm.referentiel.refadmin.model.StatutDemande;
import fr.vdm.referentiel.refadmin.model.StatutTache;
import fr.vdm.referentiel.refadmin.model.TacheActeur;
import fr.vdm.referentiel.refadmin.repository.DemandeActeurRepository;
import fr.vdm.referentiel.refadmin.repository.LienActeurGroupeFonctionnelRepository;
import fr.vdm.referentiel.refadmin.repository.StatutDemandeRepository;
import fr.vdm.referentiel.refadmin.repository.StatutTacheRepository;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class WorkflowActeurServiceImpl implements WorkflowActeurService {

    @Autowired
    private TacheActeurService tacheActeurService;
    @Autowired
    private StatutTacheRepository statutTacheRepository;
    @Autowired
    private StatutDemandeRepository statutDemandeRepository;
    @Autowired
    private DemandeActeurRepository demandeActeurRepository;
    @Autowired
    private DroitService droitService;
    @Autowired
    private ActeurService acteurService;
    @Autowired
    private NotificationActeurService notificationActeurService;


    public StatutDemande createDemande(DemandeActeur demande, boolean simplifie) {
        return createDemande(demande, simplifie, false);
    }

    public StatutDemande createDemande(DemandeActeur demande, boolean simplifie, boolean isImport) {
        // Create de la tache en base
        StatutTache statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);
        String activite = calculerActivite(demande, simplifie, 0);
        DemandeActeur demandeActeur = demandeActeurRepository.findById(demande.getId()).orElse(null);
        Serializable tacheId = tacheActeurService.createTache(demandeActeur, activite, statutTache);

        // Envoi du mail de notification
        if (!isImport) {
            notificationActeurService.notifyCreate(tacheId, calculerAffectation(demande));
        }

        if (ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE.equals(activite) || ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL.equals(activite)) {
            Optional<StatutDemande> statutDemande = statutDemandeRepository.findById(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H_ID);
            return statutDemande.orElse(null);
        }

        return statutDemandeRepository.findById(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T_ID).orElse(null);
    }

    @Override
    public StatutWorkFlowDto completerTache(Long idTache, boolean valide, DemandeActeur demande, ChampTechniqueDto champTechnique, ExtensionMessagerieDto messagerie, String oldLogin, String oldEmail, String oldAffectation) throws ServiceException {
        return completerTache(idTache, valide, demande, champTechnique, messagerie, oldLogin, oldEmail, oldAffectation, false);
    }

    public StatutWorkFlowDto completerTache(Long idTache, boolean valide, DemandeActeur demande,
                                            ChampTechniqueDto champTechnique,
                                            ExtensionMessagerieDto messagerie, String oldLogin,
                                            String oldEmail, String oldAffectation, boolean isImport) throws ServiceException {

        // Mise � jour de l'affectation
        String affectation = calculerAffectation(demande);

        boolean attenteDroit = false;
        TacheActeur tacheEnCours = tacheActeurService.findById(idTache);

        String statutCode = "";
        if (tacheEnCours.getActivite().equals(ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE)) {
            statutCode = traitementHIntermediaire(tacheEnCours, valide, affectation);
        } else if (tacheEnCours.getActivite().equals(ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL)) {
            statutCode = traitementHFinal(tacheEnCours, valide, affectation, isImport);
        } else if (tacheEnCours.getActivite().equals(ConstanteWorkflow.ACTIVITY_TECHNIQUE)) {
            statutCode = traitementTechnique(tacheEnCours, valide, demande, messagerie, champTechnique, oldLogin, oldEmail, oldAffectation, isImport);

            // Le statut est toujours en en cours technique apres traitement technique
            if (ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T.equals(statutCode)) {
                log.debug("La tache est en attente de suppression de droits");
                attenteDroit = true;
            }
        }

        StatutDemande statut = statutDemandeRepository.findByCode(statutCode);
        return new StatutWorkFlowDto(statut, attenteDroit);
    }


    /**
     * Méthode permettant de sauvegarder ou de supprimer le droit selon la
     * demande dto passé en paramètre.
     *
     * @param demande          La demande de droit à appliquer.
     * @param messagerie       La messagerie à sauvegarder.
     * @param champs_technique Les champs techniques à sauvegarder
     * @param oldLogin         L'ancien login
     * @param oldEmail         L'ancienne adresse de messagerie
     * @param oldAffectation   L'ancienne affectation
     */
    public void saveAction(final DemandeActeur demande,
                           ExtensionMessagerieDto messagerie,
                           ChampTechniqueDto champs_technique, final String oldLogin,
                           final String oldEmail, final String oldAffectation) throws ServiceException {

        saveAction(demande, messagerie,
                champs_technique, oldLogin, oldEmail, oldAffectation, false);
    }

    public void saveAction(final DemandeActeur demande,
                           ExtensionMessagerieDto messagerie,
                           ChampTechniqueDto champs_technique, final String oldLogin,
                           final String oldEmail, final String oldAffectation, boolean isImport) throws ServiceException {


        if (demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_SUPPRESSION))) {
            deleteActeur(demande.getActeurBenef().getIdActeur(), demande.getId());
        } else {
            /**if (null != messagerie && !messagerie.isValide()) {
             messagerie = null;
             }
             if (!champs_technique.isValide()) {
             champs_technique = null;
             }**/
            try {
                // Sauvegarde ou mise à jour de l'acteur
                acteurService.save(DemandeMapper.INSTANCE.demandeActeurToDemandeActeurDto(demande), champs_technique, messagerie, oldEmail,
                        oldLogin, oldAffectation, isImport);
            } catch (InvalidNameException ine) {
                log.error("Erreur lors de la sauvegarde de l'acteur " + demande.getLogin() + " dans l'annuaire.");
            }
        }
    }


    private String traitementTechnique(TacheActeur tache, boolean valide, final DemandeActeur demande,
                                       ExtensionMessagerieDto messagerie, ChampTechniqueDto champTechnique, String oldLogin, String oldEmail,
                                       String oldAffectation, boolean isImport) throws ServiceException {
        if (valide) {
            if (demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_CREATION)) || demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_MODIFICATION))) {

                saveAction(demande, messagerie, champTechnique, oldLogin, oldEmail, oldAffectation, isImport);

                StatutTache statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_VALIDEE);
                tache.setStatutTache(statutTache);
                tacheActeurService.save(tache);
            } else if (Objects.equals(demande.getTypeDemande(), String.valueOf(ConstanteWorkflow.DEM_ACTEUR_SUPPRESSION))) {
                // Check Attente droits

                // R�cup�ration de la liste des permissions de l'acteur de cette
                // demande de suppression
                List<DroitDto> permissions = droitService.findByIdActeur(demande.getActeurBenef().getIdActeur());

                // Si la liste est vide alors on peut le supprimer, sinon il va
                // falloir attendre qu'on supprime toutes ces permissions
                if (permissions.isEmpty()) {

                    deleteActeur(demande.getActeurBenef().getIdActeur(), demande.getId());

                    StatutTache statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_VALIDEE);
                    tache.setStatutTache(statutTache);
                    tacheActeurService.save(tache);
                } else {
                    // Statut en attente
                    StatutTache statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_ATTENTE_DROITS);
                    tache.setStatutTache(statutTache);
                    tacheActeurService.save(tache);

                    return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
                }
            }

            return ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE;
        } else {
            // Statut refuse
            StatutTache statutTacheEnCours = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_REFUSEE);
            tache.setStatutTache(statutTacheEnCours);
            tacheActeurService.save(tache);

            // Creation tache hierarchique final
            StatutTache statutTacheNew = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);

            String activite = ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL;
            Serializable tacheId = tacheActeurService.createTache(demande, activite, statutTacheNew);

            // Mail
            notificationActeurService.notifyCreate(tacheId, calculerAffectation(demande));

            return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H;
        }
    }

    private String calculerAffectation(DemandeActeur demande) {
        // Non ==> typeActeur=Agent ou externe
        // nombre de niveaux hierarchiques necessaires
        // R�cup�ration de l'affectation terrain si elle existe
        String affectation = "";
        if (demande.getCollTerrain() != null && StringUtils.isNotBlank(demande.getCollTerrain())) {
            affectation = demande.getCellule();
        } else {
            if (demande.getColl() != null && StringUtils.isNotBlank(demande.getCellule())) {
                affectation = demande.getCellule();
            }
        }

        return affectation;
    }

    private String calculerActivite(DemandeActeur demande, boolean simplifie, long nbNiveauxReal) {
        String activite = "";

        if (!simplifie || ConstanteWorkflow.TYPE_APPLICATION.equals(demande.getTypeActeur())) {
            // - Dans le cas d'une demande complete, on passe directement en validation technique
            // - Aucune validation hierarchique pour les comptes applicatifs
            activite = ConstanteWorkflow.ACTIVITY_TECHNIQUE;
        } else {
            activite = ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL;

            // Si la demande ne concerne pas un partenaire
            if (StringUtils.isBlank(demande.getOrganisation().getNom())) {
                String affectation = calculerAffectation(demande);

                long nbNiveauxRequis = 0;
                if (StringUtils.isNotBlank(affectation)) {
                    //nbNiveauxRequis = getRegleValidationService().selectNiveauHierarchique(affectation);
                }

                if (nbNiveauxRequis > nbNiveauxReal) {
                    activite = ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE;
                }
            }
        }

        log.debug(String.format("Activit� calcul�e pour la demande %s : %s", demande.getId(), activite));
        return activite;
    }

    /**
     * Traitement sur une tache en etat de validation hierarchique intermediaire
     */
    private String traitementHIntermediaire(TacheActeur tache, boolean valide, String affectation) {
        log.info("Traitement hiérarchique intermédiaire");
        if (valide) {
            DemandeActeur demandeDto = tache.getDemandeActeur();
            String newActivite = calculerActivite(demandeDto, false, tache.getNiveau());

            Serializable tacheId = null;
            // Niveau hierarchique sup�rieur
            if (ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE.equals(newActivite)) {
                long newNiveau = tache.getNiveau() + 1;
                tache.setNiveau(newNiveau);
                tacheId = tache.getId();
            } else {
                // Fin niveau hierarchique interm�diaire, cr�ation d'une tache hierarchique finale
                StatutTache statutTacheEnCours = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_VALIDEE);
                tache.setStatutTache(statutTacheEnCours);

                // Creation nouvelle demande
                StatutTache statutTacheNew = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);

                String activite = ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL;
                tacheId = tacheActeurService.createTache(demandeActeurRepository.findById(demandeDto.getId()).orElse(null), activite, statutTacheNew);
            }

            // Mise � jour de la tache
            tacheActeurService.save(tache);

            // Envoi de mails de notification
            notificationActeurService.notifyCreate(tacheId, calculerAffectation(demandeDto));

            return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H;
        } else {
            // Refuse en cours
            StatutTache statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_REFUSEE);
            tache.setStatutTache(statutTache);
            tacheActeurService.save(tache);

            // Envoi mail
            notificationActeurService.notifyRefus(tache.getDemandeActeur().getId());

            return ConstanteWorkflow.STATUT_DEMANDE_REFUSEE;
        }
    }


    private String traitementHFinal(TacheActeur tache, boolean valide,
                                    String affectation, boolean isImport) {
        log.info("Traitement hiérarchique final");
        if (valide) {
            StatutTache statutTacheEnCours = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_VALIDEE);
            tache.setStatutTache(statutTacheEnCours);

            // Creation nouvelle demande
            DemandeActeur demande = tache.getDemandeActeur();
            StatutTache statutTacheNew = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);

            String activite = ConstanteWorkflow.ACTIVITY_TECHNIQUE;
            Serializable tacheId = tacheActeurService.createTache(demandeActeurRepository.findById(demande.getId()).orElse(null), activite, statutTacheNew);

            // Mise � jour de la tache
            tacheActeurService.save(tache);

            // Envoi de mails de notification
            if (!isImport)
                notificationActeurService.notifyCreate(tacheId, calculerAffectation(demande));


            return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
        } else {
            // Refuse en cours
            StatutTache statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_REFUSEE);
            tache.setStatutTache(statutTache);
            tache.setIdassigne(null);
            tacheActeurService.save(tache);

            // Envoi mail
            if (!isImport) {
                notificationActeurService.notifyRefus(tache.getDemandeActeur().getId());
            }

            return ConstanteWorkflow.STATUT_DEMANDE_REFUSEE;
        }
    }



    /**
     * Cloture toutes les demandes de l'acteur concern�s et supprime les taches
     * du workflow
     *
     * @param idActeur  : l'id de l'acteur.
     * @param idDemande La demande qui ne doit pas �tre clotur�e (optionnel)
     */
    @Override
    public void cloturerDemande(final long idActeur, final Long idDemande) {
        log.info(String.format("Cloture des demandes pour l'acteur %s", idActeur));
        // Recupere toutes les taches en cours de l'acteur
        StatutTache statutTacheEnCours = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);
        List<TacheActeur> taches = tacheActeurService.findByActeurBeneficiaire(idActeur, statutTacheEnCours);

        StatutTache statutSupprimee = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_SUPPRIMEE);
        for (TacheActeur tache : taches) {
            if (null == idDemande) {
                tache.setStatutTache(statutSupprimee);
                tacheActeurService.save(tache);
            } else if (!idDemande.equals(tache.getDemandeActeur().getId())) {
                DemandeActeur demande = tache.getDemandeActeur();
                demande.setStatut(this.statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_REFUSEE));
                demandeActeurRepository.save(demande);
            }
        }

    }

    @Autowired
    PjService pjService;
    @Autowired
    LienActeurGroupeFonctionnelRepository lienActeurGroupeFonctionnelRepository;
    @Autowired
    WorkflowHabilitationService workflowHabilitationService;


    @Override
    public void deleteActeur(Long idActeur, Long idDemande) throws ServiceException {

        this.cloturerDemande(idActeur,
                idDemande);
        // On supprime toutes les PJ qui sont li� a cet acteur
        this.pjService.deletePieceJointe(idActeur, null, null);

        this.lienActeurGroupeFonctionnelRepository.deleteByIdActeur(idActeur);


        this.workflowHabilitationService.cloturerDemandeByIdActeurBeneficiaire(idActeur);
        acteurService.deleteDemandesActeur(idActeur);

        acteurService.deleteActeur(idActeur);
    }
}
