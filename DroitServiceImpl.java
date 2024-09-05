package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.DemandeDroitDto;
import fr.vdm.referentiel.refadmin.dto.DroitDto;
import fr.vdm.referentiel.refadmin.dto.GroupeADDto;
import fr.vdm.referentiel.refadmin.mapper.DroitMapper;
import fr.vdm.referentiel.refadmin.mapper.GroupeADMapper;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.*;
import fr.vdm.referentiel.refadmin.repository.ad.GroupeADRepository;
import fr.vdm.referentiel.refadmin.service.DroitService;
import fr.vdm.referentiel.refadmin.service.GroupeADService;
import fr.vdm.referentiel.refadmin.service.PropagationLDAPService;
import fr.vdm.referentiel.refadmin.utils.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Service
@Log4j2
public class DroitServiceImpl implements DroitService {
    private final GroupeApplicatifRepository groupeApplicatifRepository;
    private final TacheHabilitationRepository tacheHabilitationRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final StatutTacheRepository statutTacheRepository;
    private final GroupeADRepository groupeADRepository;
    @Autowired
    private ActeurRepository acteurRepository;
    @Autowired
    private DemandeDroitRepository demandeDroitRepository;
    @Autowired
    private ActeurVueRepository acteurVueRepository;

    @Autowired
    private DroitRepository droitRepository;

    @Autowired
    PropagationLDAPService propagationLDAPService;

    public DroitServiceImpl(ActeurRepository acteurRepository,
                            GroupeADRepository groupeADRepository,
                            StatutTacheRepository statutTacheRepository,
                            StatutDemandeRepository statutDemandeRepository,
                            TacheHabilitationRepository tacheHabilitationRepository,
                            GroupeApplicatifRepository groupeApplicatifRepository) {
        this.acteurRepository = acteurRepository;
        this.groupeADRepository = groupeADRepository;
        this.statutTacheRepository = statutTacheRepository;
        this.statutDemandeRepository = statutDemandeRepository;
        this.tacheHabilitationRepository = tacheHabilitationRepository;
        this.groupeApplicatifRepository = groupeApplicatifRepository;
    }

    @Autowired
    GroupeADService groupeADService;
    private static final DroitMapper droitMapper = DroitMapper.INSTANCE;

    public DroitDto getDroitById(Long idDroit) {
        Optional<Droit> optionalDroit = this.droitRepository.findById(idDroit);
        return optionalDroit.map(DroitMapper.INSTANCE::droitToDroitDto).orElse(null);
    }

    @Override
    public List<DroitDto> getAllDroitByidOffre(Long idOffre) {
        List<Droit> droitList = this.droitRepository.findAllByOffre_Id(idOffre);
        return DroitMapper.INSTANCE.droitsToDroitDtos(droitList);
    }

    public boolean saveDroitImport(DemandeDroit demandeDroit, Long idActeur, Long idOffre) {
        log.info("On verifie si le droit existe déjà");
        boolean hasDroit = existsDroitByIdActeurAndIdOffre(idActeur, idOffre);
        String loginActeurDem = demandeDroit.getIdentifiant();
        String codeOffre = demandeDroit.getOffre().getCodeOffre();
        if (hasDroit) {
            log.warn(String.format("le droit existe deja: %s, %s. Impossible de créer un droit.", loginActeurDem, codeOffre));
            return false;
        }
        Acteur acteur = acteurRepository.findById(idActeur).orElse(null);

        if (acteur == null) {
            log.error(String.format("L'acteur %s n'existe pas dans la table acteur. Impossible de créer un droit", loginActeurDem));
            return false;
        }

        log.info(String.format("Création des droits de l'acteur : %s pour l'offre %s.", loginActeurDem, codeOffre));

        Droit droit = new Droit();
        droit.setOffre(demandeDroit.getOffre());
        droit.setDemande(demandeDroit);
        droit.setActeurBenef(acteur);
        droit.setIdentifiant(acteur.getLogin());

        this.droitRepository.save(droit);
        log.info(String.format("La creation du droit de l'acteur %s pour l'offre %s est terminé avec succèss.", loginActeurDem, codeOffre));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Droit getDroitByIdActeurAndIdOffre(Long idActeur, Long idOffre) {
        return this.droitRepository.findDroitByIdActeurAndIdOffre(idActeur, idOffre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsDroitByIdActeurAndIdOffre(Long idActeur, Long idOffre) {
        Droit droit = this.droitRepository.findDroitByIdActeurAndIdOffre(idActeur, idOffre);
        return droit != null;
    }


    @Override
    public List<DroitDto> findByIdActeur(Long idActeur) {
        List<Droit> mesDroits = droitRepository.findAllByActeurBenef_IdActeur(idActeur);
        List<DroitDto> mesDroitsDto = new LinkedList<>();

        for (Droit monDroit : mesDroits) {
            DroitDto tempDroit = droitMapper.droitToDroitDto(monDroit);
            if (monDroit.getDemande() != null && monDroit.getDemande().getProfils() != null) {
                StringBuilder tempProfilsString = null;
                for (GroupeApplicatif profil : monDroit.getDemande().getProfils()) {
                    if (tempProfilsString == null) {
                        tempProfilsString = new StringBuilder("(");
                    } else {
                        tempProfilsString.append(", ");
                    }
                    tempProfilsString.append(LDAPUtil.extraireValeurAttributFromDn(profil.getDn(), BaseADUtil.ATTR_AD_CN));
                }
                if (tempProfilsString != null) {
                    tempProfilsString.append(")");
                    tempDroit.setProfilsString(tempProfilsString.toString());
                }
            }
            mesDroitsDto.add(tempDroit);
        }

        return mesDroitsDto;
    }


    /**
     * Selectionne un droit correspondant e un acteur et une offre de service
     *
     * @param idActeur l'identifiant de l'acteur
     * @param idOffre  l'identifiant de l'offre
     * @return le droit demande
     */
    @Override
    public DroitDto selectByIdActeurIdOffre(final long idActeur,
                                            final long idOffre) {
        Droit droit = this.droitRepository.findDroitByActeurBenef_IdActeurAndOffre_Id(idActeur, idOffre);
        DroitDto dto = droitMapper.droitToDroitDto(droit);
        /**
         if (droit.getDemande() != null) {
         List<Reponse> result = this.reponseDao.findByIdDemande(droit
         .getDemande().getId());
         List<ReponseDto> reponses = new ArrayList<ReponseDto>();
         for (Reponse reponse : result) {
         reponses.add(new ReponseDto(reponse));
         }

         dto.setReponses(reponses);
         }**/
        return dto;
    }

    /**
     * Methode permettant de supprimer un droit e un utilisateur.
     *
     * @param idActeur  L'identifiant de l'acteur concerne.
     * @param idOffre   L'identifiant de l'offre concerne.
     * @param idDemande L'identifiant de la demande e ne pas supprimer.
     */
    @Override
    public void delete(final long idActeur, final Long idOffre,
                       final Long idDemande) {
        delete(idActeur, idOffre, false);
        // cloture des demandes en cours associees a cet acteur pour cet offre

        // Recupere toutes les taches en cours selon l'idActeur beneficiaire
        List<TacheHabilitation> taches = tacheHabilitationRepository.findByActeurAndOffre(idActeur, idOffre);

        StatutTache statutAnnulee = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_SUPPRIMEE);
        StatutDemande statutDemRejetee = statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_REFUSEE);
        for (TacheHabilitation tache : taches) {
            if (!idDemande.equals(tache.getDemandeDroit().getId())) {
                log.info(String.format("Cloture de la tache : %s", tache.getId()));
                tache.setStatutTache(statutAnnulee);
                tache.getDemandeDroit().setStatut(statutDemRejetee);
            }
        }

    }


    /**
     * Methode permettant de supprimer un droit e un utilisateur.
     *
     * @param idActeur     L'identifiant de l'acteur concerne.
     * @param idOffre      L'identifiant de l'offre concernee.
     * l'offre concernee est parametree pour une propagation SGBD
     */
    public void delete(final long idActeur, final Long idOffre,
                      boolean simu) {
        if (log.isInfoEnabled()) {
            log.info(String.format(
                    "Suppression du droit de l'acteur %1$s sur l'offre %2$s",
                    idActeur, idOffre));
        }
        // On charge les donnees du droit
        Droit droit = this.droitRepository.findDroitByActeurBenef_IdActeurAndOffre_Id(idActeur, idOffre);
        ActeurVue acteurVue = acteurVueRepository.findById(idActeur).get();

        // On garde quelques donnees qui vont servir apres le suppression du
        // droit.

        Acteur beneficiaire = droit.getActeurBenef();

        Offre offre = droit.getOffre();
        DemandeDroit demandeDroit = droit.getDemande();

        // On verifie si on doit supprimer des donnees d'extension d'offre
        //deleteDonneesExtension(droit, offre, simu);
        if (!simu) {
            // On supprime le droit de la base
            this.droitRepository.delete(droit);
        } else {
            // 13/10/2014 - BGE - EVT-8807
            TransactionAspectSupport.currentTransactionStatus()
                    .setRollbackOnly();

            log
                    .info(String
                            .format("PSEUDO SQL - DELETE VIRTUEL DU DROIT DE L'ACTEUR / ACTEUR LOGIN %s / OFFRE CODE %s",
                                    beneficiaire.getLogin(),
                                    offre.getCodeOffre()));

        }
        // On propage la suppression dans l'annuaire AD VDM.
        groupeADService.supprimerActeurGroupe(acteurVue.getLogin(), LDAPUtil.extraireValeurAttributFromDn(offre.getParametrageLdap().getDnPrinc(), ConstanteAD.ATTR_AD_CN), 'A');

        if (demandeDroit != null && demandeDroit.getProfils() != null && !demandeDroit.getProfils().isEmpty()) {
            for (GroupeApplicatif groupe : demandeDroit.getProfils()) {
                groupeADService.supprimerActeurGroupe(acteurVue.getLogin(), LDAPUtil.extraireValeurAttributFromDn(groupe.getDn(), ConstanteAD.ATTR_AD_CN), 'A');
            }
        }

        // on propage dans un eventuel SGBD
    }


    /**
     * Methode permettant de sauvegarder ou de mettre e jour un droit.
     *
     * @param demandeDto Le dto de la demande de droit e sauvegarder
     */
    @Override
    public void save(final DemandeDroitDto demandeDto) throws ServiceException {
        ActeurVue acteurVue = demandeDto.getActeurBenef();
        Offre offre = demandeDto.getOffre();
        // On vérifie si le droit existe déjà.
        List<GroupeApplicatif> ancienProfils = null;
        Droit droit = this.droitRepository.findDroitByActeurBenef_IdActeurAndOffre_Id(acteurVue.getIdActeur(),
                offre.getId());
        if (droit == null) {
            droit = new Droit();
        } else if (droit.getDemande() != null) {
            ancienProfils = droit.getDemande().getProfils();
        }
        DemandeDroit demandeDroit = demandeDroitRepository.findDemandeDroitById(demandeDto
                .getId());
        Acteur acteur = acteurRepository.findActeurByLogin(acteurVue.getLogin());
        droit.setActeurBenef(acteur);
        droit.setDemande(demandeDroit);
        droit.setOffre(offre);
        droit.setIdentifiant(demandeDto.getIdentifiant());
        if (ancienProfils != null && !ancienProfils.isEmpty()) {
            ancienProfils.removeAll(droit.getDemande().getProfils());
        }
        this.droitRepository.save(droit);
        // On vérifie si on doit sauver des donnees d'extension
        //saveDonneesExtension(offre, acteur, demandeDto);
        // On propage la creation du du droit dans le LDAP
        if (offre.getParametrageLdap() != null) {

            try {
                log.info(String.format("Récupération du groupe de DN %s", offre.getParametrageLdap().getDnPrinc()));
                GroupeADDto groupe = GroupeADMapper.INSTANCE.groupeADToGroupeADDto(groupeADRepository.findByCommonName(LDAPUtil.extraireValeurAttributFromDn(offre.getParametrageLdap().getDnPrinc(), ConstanteAD.ATTR_AD_CN)));
                groupeADService.affecterActeur(acteur.getLogin(), groupe);
                if (ancienProfils != null) {
                    for (GroupeApplicatif profilASupprimer : ancienProfils) {
                        log.info(String.format("Suppression du profil de DN %s", profilASupprimer.getDn()));
                        groupeADService.supprimerActeurGroupe(acteur.getLogin(), LDAPUtil.extraireValeurAttributFromDn(profilASupprimer.getDn(), ConstanteAD.ATTR_AD_CN), 'A');
                    }
                }
                if (demandeDroit.getProfils() != null && !demandeDroit.getProfils().isEmpty()) {
                    for (GroupeApplicatif profil : demandeDroit.getProfils()) {
                        log.info(String.format("Récupération du profil de DN %s", profil.getDn()));
                        groupe = GroupeADMapper.INSTANCE.groupeADToGroupeADDto(groupeADRepository.findByCommonName(LDAPUtil.extraireValeurAttributFromDn(profil.getDn(), ConstanteAD.ATTR_AD_CN)));
                        groupeADService.affecterActeur(acteur.getLogin(), groupe);
                    }
                }

            } catch (Exception e) {
                throw new ServiceException("Erreur lors de l'ajout dans le groupe Annuaire", e);
            }


        }

    }

    @Override
    public boolean isHabilite(long idActeur, long idOffre) {
        return droitRepository.countByOffreAndActeur(idActeur, idOffre) > 0;
    }

}