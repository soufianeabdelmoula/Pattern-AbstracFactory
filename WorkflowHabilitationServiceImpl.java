package fr.vdm.referentiel.refadmin.service.impl;


import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.mapper.CelluleMapper;
import fr.vdm.referentiel.refadmin.mapper.DemandeMapper;
import fr.vdm.referentiel.refadmin.mapper.OffreMapper;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.*;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.utils.CelluleUtils;
import fr.vdm.referentiel.refadmin.utils.Constante;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class WorkflowHabilitationServiceImpl implements WorkflowHabilitationService {

    private static final Log LOGGER = LogFactory.getLog(WorkflowHabilitationServiceImpl.class);
    public static String CODE_VAL_TECHNIQUE = "TECHNIQUE";

    public static String CODE_VAL_HIERARCHIQUE_FINAL = "HIERARCHIQUE_FINAL";

    public static String CODE_VAL_HIERARCHIQUE_1 = "HIERARCHIQUE1";

    @Autowired
    RegleValDroitRepository regleValDroitRepository;

    @Autowired
    CelluleService celluleService;

    @Autowired
    LienRegleCelluleRepository lienRegleCelluleRepository;

    @Autowired
    StatutTacheRepository statutTacheRepository;

    @Autowired
    OffreRepository offreRepository;
    @Autowired
    private StatutDemandeRepository statutDemandeRepository;
    @Autowired
    private DemandeDroitRepository demandeDroitRepository;
    @Autowired
    private TacheHabilitationService tacheHabilitationService;
    @Autowired
    private RegleValidVueRepository regleValidVueRepository;
    @Autowired
    private ParametreWorkflowDroitRepository parametreWorkflowDroitRepository;
    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private NotificationHabilitationService notificationHabilitationService;

    private final CelluleMapper celluleMapper = CelluleMapper.INSTANCE;
    private final OffreMapper offreMapper = OffreMapper.INSTANCE;
    private final DemandeMapper demandeMapper = DemandeMapper.INSTANCE;
    @Autowired
    private TacheActeurRepository tacheActeurRepository;
    @Autowired
    private DroitService droitService;
    @Autowired
    private TacheHabilitationRepository tacheHabilitationRepository;

    @Autowired
    private DemandeDroitService demandeDroitService;

    @Autowired
    private TacheActeurService tacheActeurService;
    @Autowired
    private DependanceRepository dependanceRepository;

    @Autowired
    private DependanceService dependanceService;

    public List<RegleValDroit> getRegleValDroitOffre(Long idOffre) {
        return this.regleValDroitRepository.findReglesValidationByIdOffre(idOffre);
    }

    public List<RegleValDroit> getRegleValDroitOffreTechnique(Long idOffre) {
        return this.regleValDroitRepository.findRegleValidByIdOffreAndCodeRole(idOffre, CODE_VAL_TECHNIQUE);
    }

    public List<RegleValDroit> getRegleValDroitOffreCellule(final Long idOffre, final String codeCellule) {
        List<Cellule> cellules = celluleService.findAllParentsActiveInclus(codeCellule);
        List<String> codeCellules = cellules.stream().map(Cellule::getCode).map(StringUtils::trimAllWhitespace).collect(Collectors.toList());
        return this.regleValDroitRepository.findRegleValDroitByIdOffreCellule(idOffre, codeCellules);
    }

    public List<RegleValDroit> getRegleValDroitOffreCelluleRole(final Long idOffre, final String codeCellule, final String role) {
        List<Cellule> cellules = celluleService.findAllParentsActiveInclus(codeCellule);
        List<String> codeCellules = cellules.stream().map(Cellule::getCode).map(StringUtils::trimAllWhitespace).collect(Collectors.toList());
        return this.regleValDroitRepository.findRegleValDroitByIdOffreCelluleRole(idOffre, codeCellules, role);
    }


    /**
     * Retourne si le login de l'acteur est valideur VH1/VH/VT sur l'offre/Cellule
     * @param login
     * @param idOffre
     * @param cellule
     * @param codeRole
     * @return
     */
    public boolean isValideurCelluleOffre(String login,Long idOffre,String cellule, String codeRole){
        List<Cellule> listCellule = celluleService.findAllParentsActiveInclus(cellule);
        List<RegleValDroit> listRegles = this.regleValDroitRepository.findRegleValDroitByIdOffreCelluleRoleActeur(idOffre, listCellule.stream().map(Cellule::getCode).collect(Collectors.toList()), codeRole, login);

        return listRegles != null && !listRegles.isEmpty();
    }


    /**
     *
     * @param idOffre
     * @param statutDemande
     * @param cellule
     * @param statutTache
     * @param loginValideur
     * @return
     */
    public String calculerStatutDemande(Long idOffre, String statutDemande, String cellule, String statutTache, String loginValideur){

        switch (statutDemande){
            case ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H:

                if(ConstanteWorkflow.STATUT_TACHE_VALIDEE.equals(statutTache)){
                    //Coupure workflow
                    if(isValideurCelluleOffre(loginValideur,idOffre,cellule, CODE_VAL_TECHNIQUE)){
                        return ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE;
                    }
                    return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
                }else{
                    return ConstanteWorkflow.STATUT_DEMANDE_REFUSEE;
                }
            case ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T:

                if(ConstanteWorkflow.STATUT_TACHE_VALIDEE.equals(statutTache)){
                    return ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE;
                }else{
                    // Coupure workflow
                    if(isValideurCelluleOffre(loginValideur,idOffre,cellule, CODE_VAL_HIERARCHIQUE_FINAL)){
                        return ConstanteWorkflow.STATUT_DEMANDE_REFUSEE;
                    }
                    return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H;
                }

            default:
                return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H;

        }


    }


    public List<TacheHabilitationDto> findTachesHabilitation(final UtilisateurDto user){
        // id de la tache
        Long tacheId;
        // liste des taches acteur dispo
        List<TacheHabilitationDto> tachesHabilitationDispo = new LinkedList<TacheHabilitationDto>();
        // la demande rattachee a la tache en cours
        DemandeDroitDto demandeDroit;


        // liste pour lequel l'acteur est valideur
        for (OffreValidationDto offreValidationDto : user.getOffresValidation()
                .getOffresValidation()) {
            // L'utilisateur est valideur hierarchique final
            List<String> valHierarchiqueFinal =
                    offreValidationDto.getValidateurHierarchiqueFinal();

            if (valHierarchiqueFinal.size() > 0) {
                List<TacheHabilitation> tachesHierarchiquesFinales = tacheHabilitationService.findTachesByAffectation(ConstanteWorkflow.ACTIVITE_HIERARCHIQUE_FINALE,
                        valHierarchiqueFinal.stream().map(StringUtils::trimAllWhitespace).collect(Collectors.toList()), offreValidationDto.getIdOffre());
                for (TacheHabilitation tache : tachesHierarchiquesFinales) {
                    tacheId = tache.getId();
                    demandeDroit = DemandeMapper.INSTANCE.demandeToDemandeDto(tache.getDemandeDroit());
                    tachesHabilitationDispo.add(new TacheHabilitationDto(tacheId, tache.getTsCreat(), ConstanteWorkflow.TYPe_ETAPE_CREATION, demandeDroit));

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Chargement de la tache [" + tacheId
                                + "] pour Valideur hiearchique final ["
                                + user.getLogin() + "] ");
                    }
                }
            }

            // L'utilisateur est valideur hierarchique intermediaire
            List<String>[] valHierarchique =
                    offreValidationDto.getValideursHierarchique();
            for (int niv = 0; niv < valHierarchique.length; niv++) {
                List<String> affectations = valHierarchique[niv];
                if (affectations != null && affectations.size() > 0) {

                    List<TacheHabilitation> tachesHierarchiquesIntermediaire = tacheHabilitationService.findTachesByAffectationAndNiveau(
                            ConstanteWorkflow.ACTIVITE_HIERARCHIQUE_INTERMEDIAIRE, (long) niv, affectations.stream().map(StringUtils::trimAllWhitespace).collect(Collectors.toList()), offreValidationDto
                                    .getIdOffre());

                    for (TacheHabilitation tache : tachesHierarchiquesIntermediaire) {
                        tacheId = tache.getId();
                        demandeDroit = DemandeMapper.INSTANCE.demandeToDemandeDto(tache.getDemandeDroit());
                        tachesHabilitationDispo.add(new TacheHabilitationDto(tacheId, tache.getTsCreat(),
                                ConstanteWorkflow.TYPe_ETAPE_CREATION, demandeDroit));
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER
                                    .debug("Chargement de la tache ["
                                            + tacheId
                                            + "] pour Valideur hierarchique intermediaire ["
                                            + user.getLogin() + "] de niveau ["
                                            + niv + "]");
                        }
                    }
                }
            }

            // L'utilisateur est valideur technique
            if (offreValidationDto.isValideurTechnique()) {
                List<TacheHabilitation> tachesTechniques = tacheHabilitationService.findTacheHabilitationByActiviteAndOffreAndStatut(
                        ConstanteWorkflow.ACTIVITE_TECHNIQUE, offreValidationDto.getIdOffre(), ConstanteWorkflow.STATUT_TACHE_EN_COURS);
                tachesTechniques.addAll(tacheHabilitationService.findTacheHabilitationByActiviteAndOffreAndStatut(
                        ConstanteWorkflow.ACTIVITE_TECHNIQUE_INTERMEDIAIRE, offreValidationDto.getIdOffre(),  ConstanteWorkflow.STATUT_TACHE_EN_COURS));

                for (TacheHabilitation tache : tachesTechniques) {
                    tacheId = tache.getId();
                    demandeDroit = DemandeMapper.INSTANCE.demandeToDemandeDto(tache.getDemandeDroit());
                    TacheHabilitationDto tacheHabilitation = new TacheHabilitationDto(tacheId,
                            tache.getTsCreat(),
                            ConstanteWorkflow.TYPe_ETAPE_CREATION,
                            demandeDroit);

                    // Retourne le nom de l'offre principale dans le cas d'une tache dependance
                    if (tache.isDependance()) {
                        OffreDto offrePrincipale = OffreMapper.INSTANCE.offreToOffreDto(tache.getOffrePrincipale());
                        tacheHabilitation.setNomOffrePrincipal(offrePrincipale.getLibelle());
                    }

                    if (tache.getActivite().equals(ConstanteWorkflow.ACTIVITE_TECHNIQUE_INTERMEDIAIRE)) {
                        tacheHabilitation.setIdOffrePrealable(demandeDroit.getOffre().getId());
                    }

                    tachesHabilitationDispo.add(tacheHabilitation);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Chargement de la tache [" + tacheId
                                + "] pour Valideur technique ["
                                + user.getLogin() + "] ");
                    }
                }
            }
        }

        return tachesHabilitationDispo;
    }


    /**
     * Recherche toutes les taches disponibles pour un utilisateur
     * @param user
     *            l'utilisateur concern�
     * @return la liste des t�ches
     */
    public List<TacheActeurDto> findTachesActeur(final UtilisateurDto user) {
        // id de la tache
        long tacheId;
        // liste des taches acteur dispo
        List<TacheActeurDto> tachesActeurDispo = new LinkedList<TacheActeurDto>();
        // la demande rattach�e � la tache en cours
        DemandeActeurDto demandeActeur;

        // L'utilisateur est valideur hi�rarchique final
        List<String> valHierarchiqueFinal =
                user.getActeurValidation().getValidateurHierarchiqueFinal();
        if (valHierarchiqueFinal.size() > 0) {
            List<TacheActeur> tachesHierarchiquesFinales = tacheActeurRepository.findTachesByStatutAndAffectation(ConstanteWorkflow.STATUT_TACHE_EN_COURS,ConstanteWorkflow.ACTIVITE_HIERARCHIQUE_FINALE, valHierarchiqueFinal.stream().map(StringUtils::trimAllWhitespace).collect(Collectors.toList()));
            LOGGER.debug(String.format("%s taches dispo trouv�es en tant que valideur HFinale", tachesHierarchiquesFinales.size()));

            for (TacheActeur tache : tachesHierarchiquesFinales) {
                tacheId = tache.getId();
                demandeActeur = new DemandeActeurDto(tacheActeurRepository.findById(tacheId).get().getDemandeActeur());
                tachesActeurDispo.add(new TacheActeurDto(tacheId, Date.from(tache.getTsCreat()),
                        ConstanteWorkflow.TYPE_ETAPE_COMPLETE, demandeActeur));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Chargement de la tache [" + tacheId
                            + "] pour Valideur hiearchique final ["
                            + user.getLogin() + "] ");
                }
            }
        }


        // L'utilisateur est valideur hi�rarchique interm�diaire
        List<String>[] valHierarchique = user.getActeurValidation().getValidateursHierarchique();
        for (int niv = 0; niv < valHierarchique.length; niv++) {
            List<String> affectations = valHierarchique[niv];
            if (affectations != null && affectations.size() > 0) {
                List<TacheActeur> tachesHierarchiquesIntermediaire =
                        this.tacheActeurRepository.findTachesByStatutAndAffectationAndNiveau(ConstanteWorkflow.STATUT_TACHE_EN_COURS,
                                ConstanteWorkflow.ACTIVITE_HIERARCHIQUE_INTERMEDIAIRE,
                                (long) niv, affectations);
                LOGGER.debug(String.format("%s taches dispo trouv�es en tant que valideur HIntermediaire niveau %s", tachesHierarchiquesIntermediaire.size(), niv));

                for (TacheActeur tache : tachesHierarchiquesIntermediaire) {
                    tacheId = tache.getId();
                    demandeActeur = new DemandeActeurDto(tacheActeurRepository.findById(tacheId).get().getDemandeActeur());
                    tachesActeurDispo.add(new TacheActeurDto(tacheId, Date.from(tache.getTsCreat()),
                            ConstanteWorkflow.TYPE_ETAPE_SIMPLIFIEE, demandeActeur));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Chargement de la tache ["
                                + tacheId
                                + "] pour Valideur hierarchique intermediaire ["
                                + user.getLogin() + "] de niveau ["
                                + niv + "]");
                    }
                }
            }
        }

        // L'utilisateur est valideur technique
        if (user.getActeurValidation().isValidateurTechniqueAct() || user.isBatch()) {
            List<TacheActeur> tachesTechniques =
                    this.tacheActeurRepository.findTachesByStatutAndActivite(ConstanteWorkflow.STATUT_TACHE_EN_COURS,ConstanteWorkflow.ACTIVITE_TECHNIQUE);
            LOGGER.debug(String.format("%s taches dispo trouv�es en tant que valideur technique", tachesTechniques.size()));

            for (TacheActeur tache : tachesTechniques) {
                tacheId = tache.getId();
                demandeActeur = new DemandeActeurDto(tacheActeurRepository.findById(tacheId).get().getDemandeActeur());
                tachesActeurDispo.add(new TacheActeurDto(tacheId,Date.from(tache.getTsCreat()),
                        ConstanteWorkflow.TYPE_ETAPE_SIMPLIFIEE, demandeActeur));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Chargement de la tache [" + tacheId
                            + "] pour Valideur technique [" + user.getLogin()
                            + "] ");
                }
            }
        }

        return tachesActeurDispo;
    }

    public StatutDemande completerTache(Long idTache, boolean valide, DemandeDroitDto demande) throws ServiceException {

        TacheHabilitation tache = tacheHabilitationService.findById(idTache);

        String statutWorkflow;
        switch (tache.getActivite()) {
            case ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE:
                statutWorkflow = traitementHIntermediaire(tache, valide, demande);
                break;
            case ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL:
                statutWorkflow = traitementHFinale(tache, valide, demande);
                break;
            case ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE:
                statutWorkflow = traitementTIntermediaire(tache, valide, demande);
                break;
            case ConstanteWorkflow.ACTIVITY_TECHNIQUE:
                statutWorkflow = traitementTFinale(tache, valide, demande);
                break;
            default: statutWorkflow = "";
        }

        tacheHabilitationService.save(tache);

        return statutDemandeRepository.findByCode(statutWorkflow);
    }

    private String traitementHIntermediaire(TacheHabilitation tache, boolean valide, DemandeDroitDto demande) {
        if (valide) {
            LOGGER.info(String.format("Validation de la tache H intermediaire : %s", tache.getId()));

            long newNiveau = tache.getNiveau() + 1;
            String activite = calculerActivite(demande, true, newNiveau);

            Long idNewTache = null;
            StatutTache statutTacheNew = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_EN_COURS_ID).build();
            if (ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE.equals(activite)) {
                // Creation de la tache HInter de niveau n+1
                idNewTache = tacheHabilitationService.createTache(demandeDroitRepository.findDemandeDroitById(demande.getId()), tache.getOffrePrincipale(), null, activite, statutTacheNew, newNiveau);
            } else if (ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL.equals(activite)) {
                // Creation de la tache HFinal
                idNewTache = tacheHabilitationService.createTache(demandeDroitRepository.findDemandeDroitById(demande.getId()), tache.getOffrePrincipale(), null, activite, statutTacheNew, 0);
            }

            if (null == idNewTache) {
                LOGGER.error("Impossible de creer la tache suivante du WF");
                return Constante.KO;
            }

            // Notification de creation de la nouvelle tache
            Cellule cellule = celluleService.findCellulePere(demande.getActeurBenef());
            // notificationHabilitationService.notifyCreate(idNewTache, cellule.getCode());

            // Validation de la tache en cours
            StatutTache statutTacheEnCours = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_VALIDEE_ID).build();
            tache.setStatutTache(statutTacheEnCours);

            return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H;
        } else {
            LOGGER.info(String.format("Refus de la tache H intermediaire: %s", tache.getId()));
            return refuseTache(tache, demande);
        }
    }

    private String traitementHFinale(TacheHabilitation tache, boolean valide, DemandeDroitDto demande) {
        if (valide) {
            LOGGER.info(String.format("Validation de la tache H finale : %s", tache.getId()));
            String typeDemande = demande.getTopDem();
            // On ne verifie pas les dependances d'offres dans le cas d'une
            // modification ou d'une suppression
            if (ConstanteWorkflow.DEM_OFFRE_MODIFICATION_STRING.equals(typeDemande) ||
                    ConstanteWorkflow.DEM_OFFRE_SUPPRESSION_STRING.equals(typeDemande)) {
                return createTacheTechniqueEnCours(tache, demande);
            }

            DemandeDroit demandeDroit = tache.getDemandeDroit();
            Offre offre = demandeDroit.getOffre();
            long idActeur = demandeDroit.getActeurBenef().getIdActeur();

            List<OffreDto> dependances = findDependancesNonHabilitees(offre.getId(), idActeur);

            if (dependances.isEmpty()) {
                // Pas de dependances, on passe en validation technique finale
                return createTacheTechniqueEnCours(tache, demande);
            } else {
                for (OffreDto dep : dependances) {
                    if (Boolean.TRUE.equals(offre.getTopAccord())) {
                        // l'offre est parametree pour demander un accore intermediaire du
                        // valideur technique
                        // on verifie si cette demande n'a pas encore ete effectuee
                        OffreDto offreDep = offreMapper.offreToOffreDto(tache.getOffreDependance());
                        if (null == tache.getOffreDependance() || !dep.equals(offreDep)) {
                            Offre depOffre = offreMapper.offreDtoToOffre(dep);
                            tache.setOffreDependance(depOffre);

                            // Passage en technique intermediaire
                            tache.setActivite(ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE);
                            tacheHabilitationService.save(tache);

                            // Notification
                            Cellule cellulePere = celluleService.findCellulePere(demandeDroit.getActeurBenef());
                            notificationHabilitationService.notifyCreate(tache.getId(), cellulePere.getCode());

                            return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
                        }
                    }

                    // Dans le cas d'une validation technique intermediaire inutile
                    // Creation de la sous demande (dependance)

                    // Mise a jour de la tache principale
                    StatutTache statutTacheAttenteDep = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_ATTENTE_DEP_ID).build();
                    tache.setStatutTache(statutTacheAttenteDep);
                    tacheHabilitationService.save(tache);

                    return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
                }
            }

            return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
        } else {
            LOGGER.info(String.format("Refus de la tache H finale : %s", tache.getId()));
            return refuseTache(tache, demande);
        }
    }

    private String traitementTIntermediaire(TacheHabilitation tache, boolean valide, DemandeDroitDto demande) {
        if (valide) {
            LOGGER.info(String.format("Validation de la tache T intermediaire : %s", tache.getId()));
            // Creation sous demande
            OffreDto offreDto = offreMapper.offreToOffreDto(tache.getOffreDependance());

            ActeurVue acteurVue =  tache.getActeurAssigne();
            UtilisateurDto utilisateur = utilisateurService.getUser();

            // DemandeDroitDto sousDemande = demandeDroitRepository.create(offreDto, tache, utilisateur);
            // LOGGER.debug(String.format("Creation de la sous demande : %s", sousDemande.getId()));

            // Creation de la tache pour la sous demande
            StatutTache statutTacheNew = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);
            // String activite = calculerActivite(sousDemande, false, 0);
            // Serializable idNewTache = tacheHabilitationService.createTache(demandeDroitDao.select(sousDemande.getId()), tache.getOffrePrincipale(), tache.getOffreDependance(), activite, statutTacheNew, 0);

            // Notification de creation de la nouvelle tache
            // String affectation = calculerAffectation(demande);
            // notificationHabilitationService.notifyCreate(idNewTache, affectation);

            // Mise a jour de la tache principale
            // StatutTache statutTacheValidee = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_VALIDEE);
            // tache.setStatutTache(statutTacheValidee);
            // tacheHabilitationDao.update(tache);

            // StatutTache statutTacheAttenteDep = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_ATTENTE_DEP);
            // tacheHabilitationDao.createTache(tache.getDemandeDroit(), tache.getOffrePrincipale(), tache.getOffreDependance(), ConstanteWorkflow.ACTIVITY_TECHNIQUE, statutTacheAttenteDep, 0);

            return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
        } else {
            LOGGER.info(String.format("Refus de la tache T intermediaire : %s", tache.getId()));
            return evalRefusTechnique(tache, demande);
        }
    }

    private String traitementTFinale(TacheHabilitation tache, boolean valide, DemandeDroitDto demande) throws ServiceException {
        if (valide) {
            LOGGER.info(String.format("Validation de la tache T finale : %s", tache.getId()));
            // Appel SaveDroitAction
            DemandeDroitDto demandeDto = demandeMapper.demandeToDemandeDto(tache.getDemandeDroit());
            demandeDto.setActeurBenef(demande.getActeurBenef());
            // ERU : Tres important, si on ne set pas ici l'extension de la demande recue en parametre, elle ne partira AJMAIS dans l'AD pour la toip ou le LDAP pour la messagerie
            // Il existait peut etre une regression ici, a verifier
            // demandeDto.setExtensionOffre(demande.getExtensionOffre());
            saveAction(demande);

            // Dans le cas d'une suppression de droits, on traite les demandes de suppression d'acteur en attente
            if ((ConstanteWorkflow.DEM_OFFRE_SUPPRESSION_STRING).equals(demande.getTopDem())) {
                tacheActeurService.traitementTacheEnAttente(tache.getDemandeDroit());
            }

            StatutTache statutTache = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_VALIDEE_ID).build();
            tache.setStatutTache(statutTache);
            tacheHabilitationService.save(tache);
            LOGGER.debug(String.format("Mise a jour du statut de la tache %s en statut %s", tache.getId(), statutTache.getLibelle()));

            // S'il s'agit d'une tache de dependance
            if (tache.isDependance()) {
                long idOffrePrincipale = tache.getOffrePrincipale().getId();
                long idActeurBeneficiaire = tache.getDemandeDroit().getActeurBenef().getIdActeur();

                // Recherche de la tache principale en TInter
                List<TacheHabilitation> tachesPrincipalesEnAttente = tacheHabilitationService.findByIdOffreAndBeneficiaireAndStatut(idOffrePrincipale, idActeurBeneficiaire, ConstanteWorkflow.STATUT_TACHE_ATTENTE_DEP);
                if (1 == tachesPrincipalesEnAttente.size()) {
                    TacheHabilitation tachePrincipale = tachesPrincipalesEnAttente.get(0);

                    // Eval dependance
                    DemandeDroit demandePrincipale = tachePrincipale.getDemandeDroit();
                    DemandeDroitDto demandePrincipaleDto = demandeMapper.demandeToDemandeDto(demandePrincipale);
                    String newActivite = calculerActivite(demandePrincipaleDto, false, 0);

                    if (ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE.equals(newActivite)
                            || ConstanteWorkflow.ACTIVITY_TECHNIQUE_DEP.equals(newActivite)) {
                        // Creation des taches de dependances
                        Offre offrePrincipale = tachePrincipale.getOffrePrincipale();
                        long idActeur = demandePrincipale.getActeurBenef().getIdActeur();
                        List<OffreDto> dependances = findDependancesNonHabilitees(offrePrincipale.getId(), idActeur);
                        ActeurVue acteurVue = tache.getActeurAssigne();
                        for (OffreDto dep : dependances) {
                            // on verifie si cette demande n'a pas encore ete effectuee
                            OffreDto depDto = offreMapper.offreToOffreDto(tache.getOffreDependance());
                            if (!dep.equals(depDto)) {
                                if (Boolean.TRUE.equals(dep.getTopAccord())) {
                                    // l'offre est parametree pour demander un accore intermediaire du
                                    // valideur technique
                                    Offre depOffre = offreMapper.offreDtoToOffre(dep);
                                    tache.setOffreDependance(depOffre);

                                    // Passage en technique intermediaire
                                    tache.setActivite(ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE);
                                    tacheHabilitationService.save(tache);

                                    // Notification
                                    Cellule cellulePere = celluleService.findCellulePere(demandePrincipaleDto.getActeurBenef());
                                    notificationHabilitationService.notifyCreate(tache.getId(), cellulePere.getCode());

                                    return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
                                }

                                // Mise a jour de la tache principale
                                StatutTache statutTacheAttenteDep = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_ATTENTE_DEP_ID).build();
                                tache.setStatutTache(statutTacheAttenteDep);
                                tacheHabilitationService.save(tache);


                                return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
                            }
                        }
                    }

                    // Mise a jour de la tache principale
                    tachePrincipale.setActivite(newActivite);
                    tachePrincipale.setActeurAssigne(null);
                    StatutTache statutTacheEnCours = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_EN_COURS_ID).build();
                    tachePrincipale.setStatutTache(statutTacheEnCours);

                    tacheHabilitationService.save(tachePrincipale);
                    Cellule cellulePere = celluleService.findCellulePere(demandePrincipaleDto.getActeurBenef());

                    // Envoi mail
                    notificationHabilitationService.notifyCreate(tachePrincipale.getId(), cellulePere.getCode());
                    notificationHabilitationService.notifyAccept(tache.getId());
                    return ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE;
                } else {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error(String.format("Impossible de determiner la tache principale pour la tache d'id %s", tache.getId()));
                    }
                }
            }

            notificationHabilitationService.notifyAccept(tache.getId());
            return ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE;
        } else {
            LOGGER.info(String.format("Refus de la tache T finale : %s", tache.getId()));
            notificationHabilitationService.notifyRefus(demande.getId(), demande);
            return evalRefusTechnique(tache, demande);
        }
    }


    private String evalRefusTechnique(TacheHabilitation tache, DemandeDroitDto demande) {
        // aucune validation hierarchique pour les comptes applicatifs
        // en cas de refus technique on annule la demande
        String typeActeur = demande.getTopDem();
        if (ConstanteWorkflow.TYPE_APPLICATION.equals(typeActeur)) {
            return refuseTache(tache, demande);
        }

        Offre offrePrincipale = tache.getOffrePrincipale();
        TacheHabilitation tachePrincipale = tache;
        if (null != offrePrincipale) {
            // Tache de dependance
            Long idActeurBeneficiaire = tache.getDemandeDroit().getActeurBenef().getIdActeur();

            List<TacheHabilitation> tachesPrincipales = tacheHabilitationService.findByIdOffreAndBeneficiaireAndStatut(offrePrincipale.getId(), idActeurBeneficiaire, ConstanteWorkflow.STATUT_TACHE_ATTENTE_DEP);

            if (1 == tachesPrincipales.size()) {
                // Recuperation de la tache principale
                tachePrincipale = tachesPrincipales.get(0);
                // Dans le cas d'une dépendance on refuse les deux offres
                if (tachePrincipale != tache) {
                    // refus de la tache principal et de la demande de droit
                    DemandeDroit demandeDroit = tachePrincipale.getDemandeDroit();
                    final String statut = refuseTache(tachePrincipale, demande);
                    demandeDroit.setStatut(statutDemandeRepository.findByCode(statut));
                    demandeDroitRepository.save(demandeDroit);
                    // Refus de la tache secondaire
                    return refuseTache(tache, demande);
                }
            } else {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error(String.format("Impossible de determiner la tache principale pour la tache d'id %s", tache.getId()));
                }
            }
        }

        // Passage en tache de HFinale
        // Creation de la tache hierarchique finale selon l'offre principale
        StatutTache statutTacheEnCours = StatutTache.builder().id(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T_ID).build();
        String activite = ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL;
        tachePrincipale.setStatutTache(statutTacheEnCours);
        tachePrincipale.setActivite(activite);
        tachePrincipale.setActeurAssigne(null);

        tacheHabilitationService.save(tachePrincipale);
        Long tacheId = tachePrincipale.getId();
        DemandeDroitDto demandeDto = demandeMapper.demandeToDemandeDto(tachePrincipale.getDemandeDroit());
        Cellule cellulePere = celluleService.findCellulePere(demandeDto.getActeurBenef());

        // Envoi mail
        notificationHabilitationService.notifyCreate(tacheId, cellulePere.getCode());

        // Le workflow passe en HFinale
        return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H;
    }

    private String createTacheTechniqueEnCours(TacheHabilitation tache, DemandeDroitDto demande) {
        // Creation de la tache technique
        StatutTache statutTacheNew = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_EN_COURS_ID).build();
        String activite = ConstanteWorkflow.ACTIVITY_TECHNIQUE;
        Long idNewTache = tacheHabilitationService.createTache(demandeDroitRepository.findDemandeDroitById(demande.getId()), tache.getOffrePrincipale(), null, activite, statutTacheNew, 0);

        // Notification de creation de la nouvelle tache
        Cellule cellule = celluleService.findCellulePere(demande.getActeurBenef());
        notificationHabilitationService.notifyCreate(idNewTache, cellule.getCode());

        // Validation de la tache en cours
        StatutTache statutTacheEnCours = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_VALIDEE_ID).build();
        tache.setStatutTache(statutTacheEnCours);

        return ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T;
    }

    private String calculerActivite(DemandeDroitDto demande, boolean simplifie, long nbNiveauxReal) {

        String typeActeur = demande.getActeurBenef().getTypeActeur();
        //02/01/2018 - ERA - aucune validation hierarchique pour les comptes applicatifs uniquement
        if (simplifie && !(typeActeur.equals(ConstanteWorkflow.TYPE_APPLICATION))) {

            if (typeActeur.equals(ConstanteWorkflow.TYPE_PARTENAIRE)) {
                // pas de validation intermediaire pour les partenaires
                return ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL;
            }

            Cellule cellule = celluleService.findCellulePere(demande.getActeurBenef());
            ParametreWorkflowDroit param = parametreWorkflowDroitRepository.findParametreWorkflowDroitByCellule(demande.getOffre().getId(), cellule.getCode());

            // recuperation du nombre de niveaux requis
            long nbNiveauxRequis = (param != null) ? param.getNbNiveau() : 0;
            if (nbNiveauxRequis > nbNiveauxReal) {
                return ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE;
            }

            return ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL;
        }

        LOGGER.debug("Calcul activite - Validation hierarchique inutile");
        Offre offre = offreRepository.findById(demande.getOffre().getId()).get();
        OffreDto offreDto = offreMapper.offreToOffreDto(offre);
        List<OffreDto> dependances = findDependancesNonHabilitees(demande.getOffre().getId(), demande.getActeurBenef().getIdActeur());

        // l'offre est parametree pour demander un accore intermediaire du
        // valideur technique
        if (!dependances.isEmpty()) {
            for (OffreDto dep : dependances) {
                List<TacheHabilitation> tachesDep = tacheHabilitationService.findByActeurAndOffre(demande.getActeurBenef().getIdActeur(), dep.getId());
                // on verifie si cette demande n'a pas encore ete effectuee
                if (tachesDep.isEmpty()) {
                    if (Boolean.TRUE.equals(offreDto.getTopAccord())) {
                        // on met l offre en attente
                        return ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE;
                    } else {
                        // sinon on cree l'offre de dependance
                        return ConstanteWorkflow.ACTIVITY_TECHNIQUE_DEP;
                    }
                }
            }
        }

        return ConstanteWorkflow.ACTIVITY_TECHNIQUE;
    }

    private List<OffreDto> findDependancesNonHabilitees(long idOffrePrincipale, long idActeur) {

        List<Offre> dependances = offreRepository.findDependances(idOffrePrincipale);

        return dependances.stream()
                .filter(offreDep -> !droitService.isHabilite(idActeur, offreDep.getId()))
                .map(offreMapper::offreToOffreDto)
                .collect(Collectors.toList());
    }

    private String refuseTache(TacheHabilitation tache, DemandeDroitDto demande) {
        // Refuse en cours
        StatutTache statutTache = StatutTache.builder().id(ConstanteWorkflow.STATUT_DEMANDE_REFUSEE_ID).build();
        tache.setStatutTache(statutTache);
        tacheHabilitationService.save(tache);

        // Envoi mail
        notificationHabilitationService.notifyRefus(tache.getDemandeDroit().getId(), demande);

        return ConstanteWorkflow.STATUT_DEMANDE_REFUSEE;
    }


    /**
     * Creation d'un nouveau processus
     *
     * @param demande   la demande en cours
     * @param offreDto  l'offre concernee
     * @param simplifie indique si il s'agit d'une etape
     * @return le statut du workflow
     */
    @Override
    public StatutDemande createDemande(final DemandeDroitDto demande,
                                       final OffreDto offreDto, final boolean simplifie) {

        // Calcul de l'activite
        String activite = calculerActivite(demande, simplifie, 0);
        StatutTache statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);

        Offre depOffre = null;

        // Cas de technique intermediaire (accord prealable)
        if (ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE.equals(activite)) {
            if (offreDto.getTopAccord()) {
                // l'offre est parametree pour demander un accore intermediaire du
                // valideur technique

                List<OffreDto> dependances = findDependancesNonHabilitees(demande.getOffre().getId(), demande.getActeurBenef().getIdActeur());
                if (!dependances.isEmpty()) {
                    for (OffreDto dep : dependances) {
                        List<TacheHabilitation> tachesDep = tacheHabilitationRepository.findByActeurAndOffre(demande.getActeurBenef().getIdActeur(), dep.getId());
                        // on verifie si cette demande n'a pas encore ete effectuee
                        if (tachesDep.isEmpty()) {
                            // Create de la tache en base
                            String affectation = calculerAffectation(demande);
                            Offre offre = offreMapper.offreDtoToOffre(offreDto);
                            Offre offreDep = offreMapper.offreDtoToOffre(dep);
                            Long tacheId = tacheHabilitationService.createTache(demandeDroitRepository.findById(demande.getId()).get(), offre, offreDep, activite, statutTache, 0);

                            // Envoi du mail de notification
                            notificationHabilitationService.notifyCreate(tacheId, affectation);

                            return statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T);
                        }
                    }
                }
            }
        }

        // Cas de dependance directe
        if (ConstanteWorkflow.ACTIVITY_TECHNIQUE_DEP.equals(activite)) {
            long idActeur = demande.getActeurBenef().getIdActeur();
            List<OffreDto> dependances = findDependancesNonHabilitees(offreDto.getId(), idActeur);

            if (!dependances.isEmpty()) {
                ActeurVue acteurVue = demande.getActeurBenef();
                UtilisateurDto utilisateur = new UtilisateurDto(acteurVue);

                for (OffreDto dep : dependances) {
                    if (dep.getTopAccord()) {
                        // l'offre est parametree pour demander un accore intermediaire du
                        // valideur technique
                        depOffre = offreMapper.offreDtoToOffre(dep);

                        // Passage en technique intermediaire
                        activite = ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE;
                    }

                    // On passe l'activite de la tache principale en technique
                    activite = ConstanteWorkflow.ACTIVITY_TECHNIQUE;
                    statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_ATTENTE_DEP);
                    // Create de la tache en base
                    String affectation = calculerAffectation(demande);
                    Offre offre = offreMapper.offreDtoToOffre(offreDto);
                    Long tacheId = tacheHabilitationService.createTache(demandeDroitRepository.findDemandeDroitById(demande.getId()), offre, depOffre, activite, statutTache, 0);
                    TacheHabilitation tache = tacheHabilitationRepository.findById(tacheId).get();

                    // Dans le cas d'une validation technique intermediaire inutile
                    // Creation de la sous demande (dependance)

                    DemandeDroitDto sousDemandeDto = this.demandeDroitService.createSousDemande(dep, tache, utilisateur);
                    LOGGER.debug(String.format("Creation de la sous demande : %s", sousDemandeDto.getId()));

                    // Creation de la tache pour la sous demande
                    StatutTache statutTacheNew = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);
                    String activiteSousDemande = calculerActivite(sousDemandeDto, false, 0);
                    DemandeDroit sousDemande = demandeDroitRepository.findDemandeDroitById(sousDemandeDto.getId());
                    Offre offreDep = offreMapper.offreDtoToOffre(dep);
                    Offre offrePrincipale = demande.getOffre();
                    Long idNewTache = tacheHabilitationService.createTache(sousDemande, offrePrincipale, offreDep, activiteSousDemande, statutTacheNew, 0);

                    // Notification de creation de la nouvelle tache
                    String affectationSousDemande = calculerAffectation(demande);
                    notificationHabilitationService.notifyCreate(idNewTache, affectationSousDemande);

                    // Envoi du mail de notification
                    notificationHabilitationService.notifyCreate(tacheId, affectation);
                }
            }
        } else {
            // Create de la tache en base
            String affectation = calculerAffectation(demande);
            Offre offre = offreMapper.offreDtoToOffre(offreDto);
            Long tacheId = tacheHabilitationService.createTache(demandeDroitRepository.findDemandeDroitById(demande.getId()), offre, depOffre, activite, statutTache, 0);

            // Envoi du mail de notification
            notificationHabilitationService.notifyCreate(tacheId, affectation);

            if (ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE == activite || ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL == activite) {
                return statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H);
            }
        }

        return statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T);
    }

    private String calculerAffectation(DemandeDroitDto demande) {
        // recuperation de l'acteur
        ActeurVue acteur = demande.getActeurBenef();
        // Non ==> typeActeur=Agent ou externe
        // nombre de niveaux hierarchiques necessaires
        // Recuperation de l'affectation terrain si elle existe
        String affectation = "";
        if (StringUtils.hasText(acteur.getCelluleTerrain())) {
            affectation =
                    CelluleUtils.getCle(acteur.getCollTerrain(),
                            acteur.getSsCollTerrain(), acteur.getCelluleTerrain());

        } else {
            if (StringUtils.hasText(acteur.getCellule())) {
                affectation =
                        CelluleUtils.getCle(acteur.getColl(),
                                acteur.getSsColl(), acteur.getCellule());
            }
        }

        return affectation;
    }


    /**
     * Cloture toutes les demandes de l'acteur concernes et supprime les taches
     * du workflow
     *
     * @param idActeur l'id de l'acteur.
     */
    @Override
    public void cloturerDemandeByIdActeurBeneficiaire(final long idActeur) {
        // Recupere toutes les taches en cours selon l'idActeur beneficiaire
        List<TacheHabilitation> taches = tacheHabilitationRepository.findByIdBeneficiaireAndStatut(idActeur, ConstanteWorkflow.STATUT_TACHE_EN_COURS);

        StatutTache statutAnnulee = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_SUPPRIMEE);
        StatutDemande statutDemRejetee = statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_REFUSEE);
        for (TacheHabilitation tache : taches) {
            LOGGER.info(String.format("Cloture de la tache : %s", tache.getId()));
            tache.setStatutTache(statutAnnulee);
            tache.getDemandeDroit().setStatut(statutDemRejetee);
        }
    }

    /**
     * Méthode permettant de sauvegarder ou de supprimer le droit selon la
     * demande dto passé en paramètre.
     *
     * @param demande
     *            La demande de droit à appliquer.
     */
    public void saveAction(final DemandeDroitDto demande) throws ServiceException {
        // 25/11/2015 – MMN - EVT 10758 :
        // Dans le cas d’une suppression, vérifier si l’offre considérée est une
        // offre avec dépendance ;
        // Si c’est le cas, vérifier si l’acteur possède une autre habilitation
        // nécessitant cette dépendance ;
        // Si ce n’est pas le cas, déclencher une demande de suppression de la
        // dépendance.

        ActeurVue acteur = demande.getActeurBenef();
        Offre offre = demande.getOffre();

        // En cas de suppression
        if (String.valueOf(ConstanteWorkflow.DEM_OFFRE_SUPPRESSION).equals(demande.getTopDem())) {
            List<Dependance> dependances = dependanceRepository.findDependancesByIdOffrePrinc(offre.getId());

            if (dependances != null) {
                for (Dependance dep : dependances) {
                    if (droitService.isHabilite(acteur.getIdActeur(),
                            dep.getIdOffreSecond())) {
                        boolean aSupprimer = true;
                        for (Long idOffre : dependanceService
                                .findOffresByDependance(dep.getIdOffreSecond())) {
                            if (droitService.isHabilite(acteur.getIdActeur(),
                                    idOffre)
                                    && !idOffre.equals(offre.getId())) {
                                aSupprimer = false;
                            }
                        }
                        if (aSupprimer) {

                            DroitDto droit = droitService
                                    .selectByIdActeurIdOffre(
                                            acteur.getIdActeur(),
                                            offre.getId());
                            StringBuffer comment = new StringBuffer();
                            comment.append("Perte de l'habilitation à l'offre : ");
                            if (droit != null) {
                                comment.append(droit.getOffre().getLibelle());
                            }
                            demandeDroitService.saveDemandeAuto(
                                    acteur.getIdActeur(),
                                    dep.getIdOffreSecond(), comment.toString(),
                                    ConstanteWorkflow.DEM_OFFRE_SUPPRESSION);
                        }
                    }
                }
            }

            droitService.delete(acteur.getIdActeur(), offre.getId(),
                    demande.getId());
        } else {
            // Sauvegarde ou mise à jour du droit
            droitService.save(demande);
        }
    }


}


