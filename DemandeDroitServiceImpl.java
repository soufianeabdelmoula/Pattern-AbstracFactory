package fr.vdm.referentiel.refadmin.service.impl;

import com.opencsv.CSVWriter;
import fr.vdm.referentiel.refadmin.alfresco.dto.AlfrescoFileDTO;
import fr.vdm.referentiel.refadmin.alfresco.dto.PieceJointeDto;
import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.exception.rest.handler.DemandeActeurException;
import fr.vdm.referentiel.refadmin.exception.rest.handler.DemandeDroitException;
import fr.vdm.referentiel.refadmin.mapper.*;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.*;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.utils.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Log4j2
@Service
public class DemandeDroitServiceImpl implements DemandeDroitService {
    @Autowired
    private LienDemGrpApplicatifRepository lienDemGrpApplicatifRepository;

    private OffreMapper offreMapper = OffreMapper.INSTANCE;

    @Autowired
    DroitRepository droitRepository;

    @Autowired
    DemandeDroitRepository demandeDroitRepository;

    @Autowired
    ActeurVueRepository acteurVueRepository;

    @Autowired
    StatutDemandeRepository statutDemandeRepository;

    @Autowired
    ActeurRepository acteurRepository;

    @Autowired
    OffreRepository offreRepository;

    @Autowired
    StatutTacheRepository statutTacheRepository;

    @Autowired
    EtapeDemDroitRepository etapeDemDroitRepository;

    @Autowired
    QuestionRespository questionRespository;

    @Autowired
    private TacheHabilitationRepository tacheHabilitationRepository;

    @Autowired
    private ParametreWorkflowDroitRepository parametreWorkflowDroitRepository;

    @Autowired
    GroupeApplicatifRepository groupeApplicatifRepository;

    @Autowired
    CelluleRepository celluleRepository;

    @Autowired
    PjService pjService;

    @Autowired
    WorkflowHabilitationService workflowHabilitationService;

    @Autowired
    DroitService droitService;

    DemandeMapper demandeMapper = DemandeMapper.INSTANCE;

    @Autowired
    OffreService offreService;

    @Autowired
    UtilisateurServiceImpl utilisateurService;

    ReponseMapper reponseMapper = ReponseMapper.INSTANCE;
    @Autowired
    ReponseRepository reponseRepository;
    @Autowired
    TacheHabilitationService tacheHabilitationService;

    @Autowired
    ParametreWorkflowDroitService parametreWorkflowDroitService;

    @Autowired
    NotificationHabilitationService notificationHabilitationService;

    private String fileUploadPath = "C:\\work\\Refonte-RFA\\RefAdmin";

    private static final Logger K_LOGGER = LoggerFactory.getLogger(fr.vdm.referentiel.refadmin.service.DemandeDroitService.class);


    @Transactional
    public void saveEtapeDemandeDroit(EtapeDemDroitDto etapeDemDroitDto, String loginValideur) throws ServiceException {

        if (!StringUtils.hasText(etapeDemDroitDto.getCommentaire())) {
            etapeDemDroitDto.setCommentaire("Validation sans commentaire");
        }
        this.traiterEtapeDemandeDroit(etapeDemDroitDto.getIdDemande(), loginValideur, etapeDemDroitDto);

    }

    @Override
    public List<ActeurHabiliteDto> findByIdOffre(Long idOffre) {
        UtilisateurDto user = utilisateurService.getUser();
        boolean visualisationTousActeurs = user.isVisualisationTousActeurs() || user.isValideurTechniqueOffre();

        return !visualisationTousActeurs ? acteurVueRepository.findByIdOffre(idOffre) : new ArrayList<>();
    }

    @Override
    public byte[] exportActeursHabiltesCsvFile(Long idOffre) {
        try {
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter);
            String[] csvHeader = generateHeaders();
            csvWriter.writeNext(csvHeader);
            List<ActeurHabiliteDto> acteurHabiliteDtos = findByIdOffre(idOffre);
            acteurHabiliteDtos.forEach(acteurHabiliteDto -> {
                List<String> data = new ArrayList<>();
                data.add(acteurHabiliteDto.getNom());
                data.add(acteurHabiliteDto.getNomUsuel());
                data.add(acteurHabiliteDto.getPrenom());
                data.add(acteurHabiliteDto.getPrenomUsuel());
                data.add(acteurHabiliteDto.getLogin());
                data.add(acteurHabiliteDto.getMatricule());
                data.add(acteurHabiliteDto.getCodeAffectationRH());
                data.add(acteurHabiliteDto.getAffectationRH());
                data.add(acteurHabiliteDto.getCodeAffectationTerrain());
                data.add(acteurHabiliteDto.getAffectationTerrain());
                data.add(String.valueOf(acteurHabiliteDto.getIdActeur()));
                data.add(String.valueOf(acteurHabiliteDto.getIdDroit()));
                data.add(acteurHabiliteDto.getType());
                data.add(acteurHabiliteDto.getProfilsString());
                data.add(acteurHabiliteDto.getDescriptionProfilsString());
                data.add(acteurHabiliteDto.getFonction());

                csvWriter.writeNext(data.toArray(new String[0]));
            });
            csvWriter.close();

            return stringWriter.toString().getBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private String[] generateHeaders() {
        return new String[]{
                "nom",
                "nomUsuel",
                "prenom",
                "prenomUsuel",
                "login",
                "matricule",
                "codeAffectationRH",
                "affectationRH",
                "codeAffectationTerrain",
                "affectationTerrain",
                "idActeur",
                "idDroit",
                "type",
                "profilsString",
                "descriptionProfilsString",
                "fonction"
        };
    }

    /**
     * Methode permettant de verifier si une demande de droit est deja en cours
     * pour un acteur une offre et le type de la demande passe en parametre.
     *
     * @param idActeur L'identifiant de l'acteur.
     * @param idOffre  L'identifiant de l'offre.
     * @param typeDem  Le type de la demande pour lequel on verifie.
     * @throws ServiceException L'exception leve si une demande existe deja.
     */
    private void verifierDemandeExistante(final long idActeur,
                                          final long idOffre, final char typeDem)
            throws ServiceException {

        List<DemandeDroit> demandes = this.demandeDroitRepository
                .findDemandeActeurByBeneficiaireAndTopDem(idActeur, idOffre, String.valueOf(typeDem));
        if (demandes != null && !demandes.isEmpty()) {
            if (typeDem == ConstanteWorkflow.DEM_OFFRE_CREATION) {
                throw new ServiceException(
                        ServiceException.DROITS_DEMANDE_CREATION_ENCOURS);
            }
            if (typeDem == ConstanteWorkflow.DEM_OFFRE_SUPPRESSION) {
                throw new ServiceException(
                        ServiceException.DROITS_DEMANDE_SUPPRESSION_ENCOURS);
            }
        }
    }

    /**
     * Methode permettant d'initialiser une demande simplifie. Celle-ci est
     * utilise lors d'une demande de modification et une demande de suppression.
     *
     * @param idActeur    L'identifiant de l'acteur
     * @param idOffre     L'identifiant de l'offre
     * @param type        Le type de la demande
     * @param commentaire Le commentaire de la demande
     * @return DemandeDroitDto la demande cree.
     */
    public DemandeDroitDto initDemandeSimplifie(final Long idActeur,
                                                final Long idOffre, final String type, final String commentaire) {
        DemandeDroitDto demandeDto = new DemandeDroitDto();
        ActeurVue acteur = acteurVueRepository.findById(idActeur).orElse(null);
        demandeDto.setActeurBenef(acteur);
        demandeDto.setOffre(offreRepository.findById(idOffre).orElse(null));
        demandeDto.setTopDem(type);
        demandeDto.setCommentaire(commentaire);

        return demandeDto;
    }

    /**
     * Methode permettant de creer une etape pour une demande d'habilitation.
     *
     * @param demandeDroit La demande pour laquelle on cree l'etape.
     * @param demandeDto   La demandeDto concerne.
     * @param utilisateur  L'utilisateur responsable de la demande
     * @param typeEtape    le type de l'Etape (Simplifiee/Complete/Technique)
     * @param validation   Si l'etape a ete validee ou non.
     */
    public void createEtpDemDroit(final DemandeDroit demandeDroit,
                                  final DemandeDroitDto demandeDto, final UtilisateurDto utilisateur,
                                  final String typeEtape, final Boolean validation) {

        EtapeDemDroit etapePrecedente = this.etapeDemDroitRepository.findDerniereEtape(demandeDroit.getId());
        EtapeDemDroit etapeDemandeDroit = new EtapeDemDroit();
        etapeDemandeDroit.setDemande(demandeDroit);
        etapeDemandeDroit.setCommentaire(demandeDto.getCommentaire());
        etapeDemandeDroit.setLogin(utilisateur.getLogin());
        etapeDemandeDroit.setTypeEtape(typeEtape);
        etapeDemandeDroit.setTopEtape(validation == null || validation);
        if (etapePrecedente != null) {
            etapeDemandeDroit.setOrdre(etapePrecedente.getOrdre() + 1);
        } else {
            etapeDemandeDroit.setOrdre(0L);
        }
        this.etapeDemDroitRepository.save(etapeDemandeDroit);
    }

    private String calculerAffectation(DemandeDroitDto demande) {
        // recuperation de l'acteur
        ActeurVue acteur = this.acteurVueRepository.findById(demande.getActeurBenef().getIdActeur()).get();

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

    private List<OffreDto> findDependancesNonHabilitees(long idOffrePrincipale, long idActeur) {

        List<OffreDto> offresDep = new ArrayList<OffreDto>();

        List<Offre> dependances = offreService.findDependances(idOffrePrincipale);
        for (Offre offreDep : dependances) {
            // on verifie si l'acteur possede des droits pour cette offre
            if (!droitService.isHabilite(idActeur, offreDep.getId())) {
                OffreDto oDto = offreMapper.offreToOffreDto(offreDep);
                offresDep.add(oDto);
            }
        }

        return offresDep;
    }


    private String calculerActivite(DemandeDroitDto demande, boolean simplifie, long nbNiveauxReal) {

        String typeActeur = demande.getActeurBenef().getTypeActeur();
        //02/01/2018 - ERA - aucune validation hierarchique pour les comptes applicatifs uniquement
        if (simplifie && !(typeActeur.equals(ConstanteActeur.TYPE_APPLICATIF))) {


            String affectation = calculerAffectation(demande);

            ParametreWorkflowDroit param = parametreWorkflowDroitService.select(demande.getOffre().getId(), affectation);

            // recuperation du nombre de niveaux requis
            long nbNiveauxRequis = (param != null) ? param.getNbNiveau() : 0;
            if (nbNiveauxRequis > nbNiveauxReal) {
                return ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE;
            }

            return ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL;
        }

        log.debug("Calcul activite - Validation hierarchique inutile");
        Offre offre = demande.getOffre();
        List<OffreDto> dependances = findDependancesNonHabilitees(offre.getId(), demande.getActeurBenef().getIdActeur());

        // l'offre est parametree pour demander un accore intermediaire du
        // valideur technique
        if (!dependances.isEmpty()) {
            for (OffreDto dep : dependances) {
                List<TacheHabilitation> tachesDep = tacheHabilitationRepository.findByActeurAndOffre(demande.getActeurBenef().getIdActeur(), dep.getId());
                // on verifie si cette demande n'a pas encore ete effectuee
                if (tachesDep.isEmpty()) {
                    if (offre.getTopAccord()) {
                        // on met l offre en attente
                        return ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE;
                    } else {
                        // sinon on créer l offre de dependance
                        return ConstanteWorkflow.ACTIVITY_TECHNIQUE_DEP;
                    }
                }
            }
        }

        return ConstanteWorkflow.ACTIVITY_TECHNIQUE;
    }

    /**
     * Creation d'un nouveau processus
     *
     * @param demande   la demande en cours
     * @param offreDto  l'offre concernee
     * @param simplifie indique si il s'agit d'une etape
     * @return le statut du workflow
     */
    public StatutDemande createDemande(final DemandeDroitDto demande,
                                       final OffreDto offreDto, final boolean simplifie) throws ServiceException {

        // Calcul de l'activite
        String activite = calculerActivite(demande, simplifie, 0);
        StatutTache statutTache = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);

        Offre depOffre = null;

        // Cas de technique intermediaire (accord prealable)
        if (ConstanteWorkflow.ACTIVITY_TECHNIQUE_INTERMEDIAIRE.equals(activite)) {
            if (Boolean.TRUE.equals(offreDto.getTopAccord())) {
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
                            Serializable tacheId = tacheHabilitationService.createTache(demandeDroitRepository.findDemandeDroitById(demande.getId()), offre, offreDep, activite, statutTache, 0);

                            // Envoi du mail de notification
                            notificationHabilitationService.notifyCreate(((TacheHabilitation) tacheId).getId(), affectation);

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
                ActeurVue acteurVue = acteurVueRepository.findById(idActeur).orElseThrow(() -> new ServiceException(DemandeActeurException.ACTEUR_DEMANDE_NULL));
                UtilisateurDto utilisateur = new UtilisateurDto(acteurVue);

                for (OffreDto dep : dependances) {
                    if (Boolean.TRUE.equals(dep.getTopAccord())) {
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
                    Long tacheId = tacheHabilitationService.createTache(demandeDroitRepository.findById(demande.getId()).get(), offre, depOffre, activite, statutTache, 0);
                    TacheHabilitation tache = tacheHabilitationRepository.findById(tacheId).orElseThrow(() -> new ServiceException(DemandeActeurException.ID_INCONNU));

                    // Dans le cas d'une validation technique intermediaire inutile
                    // Creation de la sous demande (dependance)
                    DemandeDroitDto sousDemandeDto = createSousDemande(dep, tache, utilisateur);
                    log.debug(String.format("Creation de la sous demande : %s", sousDemandeDto.getId()));

                    // Creation de la tache pour la sous demande
                    StatutTache statutTacheNew = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);
                    String activiteSousDemande = calculerActivite(sousDemandeDto, false, 0);
                    DemandeDroit sousDemande = demandeDroitRepository.findById(sousDemandeDto.getId()).orElseThrow(() -> new ServiceException(DemandeActeurException.ERREUR_INNATENDU));
                    Offre offreDep = offreMapper.offreDtoToOffre(dep);
                    Offre offrePrincipale = offreRepository.findById(demande.getOffre().getId()).get();
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
            Long tacheId = tacheHabilitationService.createTache(demandeDroitRepository.findById(demande.getId()).get(), offre, depOffre, activite, statutTache, 0);

            // Envoi du mail de notification
            notificationHabilitationService.notifyCreate(tacheId, affectation);

            if (ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE.equals(activite) || ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL.equals(activite)) {
                return statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H);
            }
        }

        return statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T);
    }


    /**
     * Permet de sauvegarder une demande de droit indispensable a la validation
     * d'une demande sur sur une offre principale.
     *
     * @param utilisateur le createur de la demande.
     * @param idActeur    L'identifiant de l'acteur
     * @param idOffre     L'identifiant de l'offre
     * @param commentaire Le commentaire
     * @return DemandeDroit La demande sauvegardee
     */
    public DemandeDroitDto saveDemandeDependance(final Long idActeur,
                                                 final Long idOffre, final String commentaire,
                                                 final UtilisateurDto utilisateur) {
        // creation de la demande
        DemandeDroitDto demandeDto = initDemandeSimplifie(idActeur, idOffre,
                String.valueOf(ConstanteWorkflow.DEM_ACTEUR_CREATION), commentaire);
        // Creation de la demande de droit
        DemandeDroit demandeDroit = demandeMapper.demandeDtoToDemande(demandeDto);
        demandeDroit.setStatut(this.statutDemandeRepository
                .findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T));
        this.demandeDroitRepository.save(demandeDroit);
        // Creation de l'etape correspondante
        createEtpDemDroit(demandeDroit, demandeDto, utilisateur,
                String.valueOf(ConstanteWorkflow.DEM_OFFRE_CREATION), true);
        // Pour la visualisation de la sauvegarde on ajoute l'id de la demande
        // cree au dto.
        demandeDto.setId(demandeDroit.getId());
        log.info("Sauvegarde de la demande de droit ID: "
                + demandeDroit.getId());
        return demandeDto;
    }

    @Override
    public DemandeDroitDto createSousDemande(OffreDto offre, TacheHabilitation tacheParent, UtilisateurDto userDemandeur) {
        log.info(String.format("Création de la sous demande pour l'offre : %s", offre.getLibelle()));


        // Création de la demande
        Long idActeur = null;
        //if (tacheParent.getDemandeDroit().getActeurBenef().getCodeType().equals(ConstanteActeur.TYPE_AGENT)) {
        idActeur = tacheParent.getDemandeDroit().getActeurBenef().getIdActeur();
        //}
        /*else if (tacheParent.getDemandeDroit().getBeneficiaire() instanceof CompteService) {
            tacheParent.getDemandeDroit().getBeneficiaire().getId();
        }*/
        /*else if (tacheParent.getDemandeDroit().getBeneficiaire() instanceof CompteResource) {
            idCompteResource = tacheParent.getDemandeDroit().getBeneficiaire().getId();
        }*/

        Long idOffre = offre.getId();
        String nom = offre.getLibelle();

        DemandeDroitDto demandeDto = saveDemandeDependance(idActeur, idOffre, nom, userDemandeur);

        return demandeDto;
    }

    /**
     * Permet de sauvegarder une demande de supression d'un droit generee
     * automatiquement
     *
     * @param idActeur    L'identifiant de l'acteur
     * @param idOffre     L'identifiant de l'offre
     * @param commentaire Le commentaire
     * @return DemandeDroit La demande sauvegardee
     */
    public DemandeDroitDto saveDemandeAuto(final long idActeur,
                                           final long idOffre, final String commentaire, final char typeDemande) throws ServiceException {

        try {
            verifierDemandeExistante(idActeur, idOffre, typeDemande);
        } catch (ServiceException e) {
            if (typeDemande == ConstanteWorkflow.DEM_OFFRE_CREATION) {
                log.debug("Une demande de creation existe deja pour l'offre "
                        + idOffre + " et l'acteur " + idActeur);
            }
            if (typeDemande == ConstanteWorkflow.DEM_OFFRE_SUPPRESSION) {
                log.debug("Une demande de suppression existe deja pour l'offre "
                        + idOffre + " et l'acteur " + idActeur);
            }
            return null;
        }

        // creation de la demande
        DemandeDroitDto demandeDto = initDemandeSimplifie(idActeur, idOffre,
                String.valueOf(typeDemande), commentaire);
        // Creation de la demande de droit
        DemandeDroit demandeDroit = DemandeMapper.INSTANCE.demandeDtoToDemande(demandeDto);
        demandeDroit.setStatut(this.statutDemandeRepository
                .findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T));
        this.demandeDroitRepository.save(demandeDroit);
        // Creation de l'etape correspondante
        createEtpDemDroit(demandeDroit, demandeDto, new UtilisateurDto(
                "Suppression Auto"), ConstanteWorkflow.TYPE_ETAPE_SIMPLIFIEE, true);

        // Pour la visualisation de la sauvegarde on ajoute l'id de la demande
        // cree au dto.
        demandeDto.setId(demandeDroit.getId());
        log.info("Sauvegarde de la demande de droit ID: "
                + demandeDroit.getId());
        StatutDemande statut;

        OffreDto offreDto = this.offreService.getOffreDtoByIdOffre(demandeDto.getOffre().getId());
        statut = createDemande(demandeDto,
                offreDto, false);

        demandeDroit.setStatut(statut);
        this.demandeDroitRepository.save(demandeDroit);
        if (demandeDroit.getOffre().getBooSuppAutoHabilitation() && demandeDroit.getTopDem().equals("S")) {
            //demandeDroit.setStatut(this.statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE));
            //save(demandeDroit, getUser(), demandeDroit.getEtapesDemDroit(), true, false, );
        }

        return demandeDto;
    }

    @Transactional()
    public void traiterEtapeDemandeDroit(Long idDemande, String loginValideur, EtapeDemDroitDto etapeDemDroitDto) throws ServiceException {
        DemandeDroit demande = this.demandeDroitRepository.findDemandeDroitById(idDemande);
        DemandeDroitDto demandeDroitDto = demandeMapper.demandeToDemandeDto(demande);

        String codeStatut = etapeDemDroitDto.getTopEtape() ? ConstanteWorkflow.STATUT_TACHE_VALIDEE : ConstanteWorkflow.STATUT_TACHE_REFUSEE;

        String codeStatutSuivant = this.workflowHabilitationService.calculerStatutDemande(demande.getOffre().getId(), demande.getStatut().getCode(),
                StringUtils.trimAllWhitespace(demande.getActeurBenef().getCellule()),
                codeStatut, loginValideur);

        EtapeDemDroit etape = DemandeMapper.INSTANCE.etapeDemandeToEtapeDemandeDto(etapeDemDroitDto);
        etape.setDemande(demande);
        etape.setTypeEtape(typeEtapeSuivant(demande, etapeDemDroitDto.getTopEtape()));
        etape.setLogin(loginValideur);
        etape.setVersion(0);
        int size = this.etapeDemDroitRepository.findMaxOrdreEtapeFromIdDemande(idDemande);
        etape.setOrdre((long) (size + 1));

        etapeDemDroitRepository.save(etape);
        long tacheId = tacheHabilitationService.getLastTache(idDemande);

        StatutDemande statutDemandeUpdated = getWorkflowHabilitationService().completerTache(tacheId,
                etapeDemDroitDto.getTopEtape(), demandeDroitDto);

        // StatutDemande statutDemande = this.statutDemandeRepository.findByCode(codeStatutSuivant);

        demande.setStatut(statutDemandeUpdated);
        this.demandeDroitRepository.save(demande);

    }

    public String codeStatutTacheFromStatutDemande(StatutDemande statutDemande){

        if(statutDemande.getCode().equals(ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE)  ){
            return ConstanteWorkflow.STATUT_TACHE_VALIDEE;
        } else if (statutDemande.getCode().equals(ConstanteWorkflow.STATUT_DEMANDE_REFUSEE)){
            return ConstanteWorkflow.STATUT_TACHE_REFUSEE;
        }else{
            return ConstanteWorkflow.STATUT_TACHE_EN_COURS;
        }

    }

    public String typeEtapeSuivant(DemandeDroit demande, boolean topEtape){
        if(topEtape){
            if(demande.getStatut().getCode().equals(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H)){
                return ConstanteWorkflow.TYPE_ETAPE_TECHNIQUE;
            }
        }else{
            if(demande.getStatut().getCode().equals(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T)){
                return  ConstanteWorkflow.TYPE_ETAPE_COMPLETE;
            }
        }

        return "T";
    }


    @Transactional(rollbackOn = {ServiceException.class})
    public Long saveDemandeHabilitation(DemandeDroitDto contents, String login) throws ServiceException {
        K_LOGGER.info("saveDemandeHabilitation");


        if ((!StringUtils.hasText(contents.getCommentaire())) || contents.getCommentaire().trim().length() == 0) {
            throw new ServiceException("Le commentaire est obligatoire.");
        }


        if (this.droitRepository.findIdDroitByIdActeur(contents.getActeurBenef().getIdActeur()).contains(contents.getOffre().getId())) {
            throw new ServiceException("L'acteur possède déjà cette habilitation.");
        }


        if (!this.demandeDroitRepository.findDemandeActeurByBeneficiaire(contents.getActeurBenef().getIdActeur(), contents.getOffre().getId()).isEmpty()) {
            throw new ServiceException("L'acteur possède déjà une demande en cours pour cette habilitation.");
        }
        K_LOGGER.info("Fin vérifications de base.");

        ActeurVue acteurVue = this.acteurVueRepository.findById(contents.getActeurBenef().getIdActeur()).orElseThrow(() -> new ServiceException(ServiceException.ACTEUR_DEMANDE_NULL));

        //Acteur acteur = this.acteurRepository.findById(contents.getActeurBenef().getIdActeur()).orElseThrow(() -> new ServiceException(ServiceException.ACTEUR_DEMANDE_NULL));
        Offre offre = this.offreRepository.findById(contents.getOffre().getId()).orElseThrow(() -> new ServiceException(ServiceException.OFFRE_DEMANDE_NULL));
        StatutDemande statutDemEnCoursH = this.statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H);

        K_LOGGER.info("Création de la demande de droit.");
        DemandeDroit demande = saveDemande(contents, statutDemEnCoursH);

        K_LOGGER.info("Création de l'etape de la demande de droit.");
        EtapeDemDroit etapeDemandeDroit = saveEtapeDemandeDroit(contents.getCommentaire(), login, demande, "S");

        StatutTache statutTache = this.statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_EN_COURS);

        TacheHabilitation tacheHabilitation = saveTacheHabilitation(demande,statutTache, 0L);

        for (ReponseDto reponseDto : contents.getReponses()) {
            Reponse reponse = new Reponse();
            reponse.setQuestion(questionRespository.findById(reponseDto.getIdQuestion()).orElseThrow(
                    () -> new ServiceException(String.format("La question d'Id : %s n'existe pas", reponseDto.getIdQuestion()))));
            reponse.setReponse(reponseDto.getLibelle());
            reponse.setDemande(demande);
        }

        //GroupeSecondaire
        List<GroupeApplicatif> groups = new ArrayList<>();


        for (ProfilDto profils : contents.getProfils()) {
            GroupeApplicatif grpAppliSec = this.groupeApplicatifRepository.findById(profils.getIdGrp()).orElseThrow(() -> new ServiceException(String.format("Erreur lors de la récupération du Grouppe applicatif  %s", profils.getLibelle())));


            groups.add(grpAppliSec);
        }

        demande.setProfils(groups);

        demandeDroitRepository.save(demande);


        gestionAjoutPjDemandeDroit(demande.getId(), contents.getOffre().getId(), login, demande.getOffre().getLibelle(), acteurVue, contents.getPiecesJointes());

        return demande.getId();
    }

    @Override
    public StatutDemande createDem(ActeurVue acteurvue, Acteur acteur, DemandeDroitDto contents, Offre offre, StatutTache statutTache, DemandeDroit demande, String login) throws ServiceException {
        Cellule cellulePere = findCellulePere(acteurvue);

        if (!acteurvue.getTypeActeur().equals("C")) {
            K_LOGGER.info("Demande pour un compte non applicatif");


            ParametreWorkflowDroit parametreWorkflowDroit = this.parametreWorkflowDroitRepository.findParametreWorkflowDroitByCellule(contents.getOffre().getId(), cellulePere.getCode());


            long nbNiveauxRequis = (parametreWorkflowDroit != null) ? parametreWorkflowDroit.getNbNiveau() : 0L;
            if (nbNiveauxRequis > 0L) {
                K_LOGGER.info("ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE");
                saveTacheHabilitation(demande, statutTache, nbNiveauxRequis);
            } else {

                K_LOGGER.info("ACTIVITY_HIERARCHIQUE_FINAL");
                saveTacheHabilitation(demande, statutTache, nbNiveauxRequis);
            }

            return this.statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H);
        }


        K_LOGGER.info("ACTIVITY_TECHNIQUE");
        return this.statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T);
    }

    private Cellule findCellulePere(ActeurVue acteur) throws ServiceException {
        Cellule cellule;
        K_LOGGER.info("Calcul de la cellule pére de niveau 1.");


        if (!StringUtils.isEmpty(acteur.getCelluleTerrain())) {
            cellule = this.celluleRepository.findCelluleActiveByCode(acteur.getCelluleTerrain());
        } else {
            cellule = this.celluleRepository.findCelluleActiveByCode(acteur.getCellule());
        }

        while (cellule.getNiveau() > 1L) {
            cellule = this.celluleRepository.findCelluleActiveByCode(cellule.getCellulePere().getCode());
        }

        if (cellule.getNiveau() == 1L) {
            K_LOGGER.info("cleDG => "  + cellule.getCode());
        } else {
            K_LOGGER.warn("Cellule pére non trouvé");
            throw new ServiceException(ServiceException.CELLULE_PERE_NULL);
        }
        return cellule;
    }


    private void gestionAjoutPjDemandeDroit(Long idDemande, Long idOffre, String login, String nomOffre, ActeurVue acteur, List<File> pjs) throws ServiceException {
        for (File file : pjs) {

            try {

                String name = FilenameUtils.getBaseName(file.getAbsoluteFile().getName());
                String ext = FilenameUtils.getExtension(file.getAbsoluteFile().getName());
                String fileName = name + "-" + idDemande + "." + ext;

                File f = new File(this.fileUploadPath + "/" + fileName);
                f.getParentFile().mkdirs();
                //.transferTo(f);


                MagicMatch match = Magic.getMagicMatch(f, false);

                if (!Arrays.<String>asList(MimeTypeConstant.AUTHORIZED_MIME_TYPE).contains(match.getMimeType())) {
                    throw new ServiceException("Ce type de fichier n'est pas pris en compte");
                }

                AlfrescoFileDTO afd = new AlfrescoFileDTO(fileName, f, match.getMimeType());


                PieceJointeDto pjDto = new PieceJointeDto(name + "." + ext, "PDF", match.getMimeType(), login, ConstanteWorkflow.DEMANDE_HAB, idDemande, idOffre, acteur.getIdActeur());

                this.pjService.saveFile(afd, pjDto, nomOffre, acteur, login);

                f.delete();
            } catch (IOException ex) {
                ex.printStackTrace();
                K_LOGGER.error("erreur de copie de la pj dans le sas");
            } catch (MagicException | net.sf.jmimemagic.MagicParseException |
                     net.sf.jmimemagic.MagicMatchNotFoundException ex) {
                ex.printStackTrace();
                K_LOGGER.error("erreur lors du scan de la pj par jmimemagic");
                throw new ServiceException("erreur lors du scan de la pj par jmimemagic");
            }
        }
    }

    private DemandeDroit saveDemande(DemandeDroitDto demandeDroitDto, StatutDemande statut) {
        DemandeDroit demande = new DemandeDroit();
        demande = DemandeMapper.INSTANCE.demandeDtoToDemande(demandeDroitDto);
        demande.setStatut(statut);
        demande.setOffre(offreRepository.findById(demandeDroitDto.getOffre().getId()).get());
        List<GroupeApplicatif> profils = new ArrayList<>();
        for (GroupeApplicatif groupe : demande.getProfils()) {
            groupe = groupeApplicatifRepository.findById(groupe.getId()).get();
            profils.add(groupe);
        }
        demande.setProfils(profils);
        return this.demandeDroitRepository.save(demande);
    }

    @Override
    public EtapeDemDroit saveEtapeDemandeDroit(String contentsCommentaire, String login, DemandeDroit demande, String typeEtape) {
        EtapeDemDroit etapeDemandeDroit = new EtapeDemDroit();
        etapeDemandeDroit.setDemande(demande);
        etapeDemandeDroit.setCommentaire(contentsCommentaire);
        etapeDemandeDroit.setLogin(login);
        etapeDemandeDroit.setTypeEtape(typeEtape);
        etapeDemandeDroit.setTopEtape(true);
        etapeDemandeDroit.setOrdre(0L);


        return this.etapeDemDroitRepository.save(etapeDemandeDroit);
    }


    @Override
    public TacheHabilitation saveTacheHabilitation(DemandeDroit demande, StatutTache statutTache, Long niveau) {
        TacheHabilitation tacheHabilitation = new TacheHabilitation();
        tacheHabilitation.setStatutTache(statutTache);
        tacheHabilitation.setDemandeDroit(demande);
        tacheHabilitation.setOffrePrincipale(demande.getOffre());
        tacheHabilitation.setNiveau(niveau);
        if (tacheHabilitation.getNiveau() > 0L) {
            tacheHabilitation.setActivite(ConstanteWorkflow.ACTIVITE_HIERARCHIQUE_INTERMEDIAIRE);
        } else {
            tacheHabilitation.setActivite(ConstanteWorkflow.ACTIVITE_HIERARCHIQUE_FINALE);
        }
        return tacheHabilitationRepository.save( tacheHabilitation);
    }

    public DemandeDroitDto getDemandeById(Long idDemande) {
        return DemandeMapper.INSTANCE.demandeToDemandeDto(this.demandeDroitRepository.findDemandeDroitById(idDemande));
    }

    @Override
    public List<ReponseDto> findReponseByIdDemande(Long idDemande) {
        return this.reponseMapper.reponsesToReponsesDtos(reponseRepository.findAllByDemande_IdOrderByIntervModifDesc(idDemande));
    }

    @Override
    public List<ActeurHabiliteDto> findHistoriqueHabilitationByIdOffre(Long idOffre) {
        UtilisateurDto user = utilisateurService.getUser();
        boolean visualisationTousActeurs = user.isVisualisationTousActeurs() || user.isValideurTechniqueOffre();

        return !visualisationTousActeurs ? acteurVueRepository.findHistoriqueHabilitationByIdOffre(idOffre) : new ArrayList<>();
    }

    public boolean existeDemandeDroit(Long idActeur, Long idOffre, List<String> statutCode) {
        List<DemandeDroit> demandeDroits = this.demandeDroitRepository.findAllByIdActeurAndIdOffreAndStatus(idActeur, idOffre, statutCode);
        return !(demandeDroits == null || demandeDroits.isEmpty());

    }


    @Override
    public EtapeDemDroit findFirstEtape(Long idDemande) {
        return etapeDemDroitRepository.findFirstEtape(idDemande);
    }


    /**
     * Permet de sauvegarder une demande d'acces.
     *
     * @param demandeDto  la demande a sauvegarder.
     * @param utilisateur le createur de la demande.
     * @param typeEtape   Le type de l'etape (demande simplifie/complete/technique).
     * @param validation  Permet de savoir si la demande a ete accepte par un valideur
     *                    ou refusee.
     * @param simplifie   Permet de savoir si la demande est simplifie ou non.
     * @param idTache     L'identifiant de la teche dans le workflow concerne par la
     *                    demande.
     * @return DemandeDroit La demande sauvegardee
     * @throws ServiceException Exception pouvant intervenir lors de la sauvegarde.
     */
    @Override
    public DemandeDroit save(final DemandeDroitDto demandeDto,
                             final UtilisateurDto utilisateur, final char typeEtape,
                             final Boolean validation, final Boolean simplifie,
                             final Long idTache, Boolean importMasse) throws ServiceException {

        // Verification des permissions
        if (!importMasse) {
            verifierPermissionDemande(demandeDto, utilisateur);
        }
        // Creation de la demande de droit
        DemandeDroit demandeDroit = demandeMapper.demandeDtoToDemande(demandeDto);
        boolean premiereDemande = demandeDto.getId() == null;
        demandeDroit.setStatut(this.statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H));
        if (this.demandeDroitRepository.findById(demandeDroit.getId()).isPresent()) {
            demandeDroit = this.demandeDroitRepository.findById(demandeDroit.getId()).get();
        }
        this.demandeDroitRepository.save(demandeDroit);
        // Creation de l'etape correspondante
        createEtpDemDroit(demandeDroit, demandeDto, utilisateur, String.valueOf(typeEtape), validation);
        // Sauvegarde des reponses aux questions pour l'offre.
        List<QuestionDto> questions = QuestionMapper.INSTANCE.questionsToQuestionsDtos(demandeDto.getOffre().getQuestions());
        List<ReponseDto> reponses = demandeDto.getReponses();
        if (questions != null) {
            for (int i = 0; i < questions.size(); i++) {
                if (StringUtil.isNotBlank(reponses.get(i).getReponse())) {
                    Reponse reponse = this.reponseRepository.select(demandeDroit
                            .getId(), questions.get(i).getId());
                    if (reponse == null) {
                        reponse = new Reponse();
                        reponse.setDemande(demandeDroit);
                        reponse.setQuestion(this.questionRespository.findById(questions
                                .get(i).getId()).get());
                    }

                    reponse.setReponse(reponses.get(i).getReponse());
                    this.reponseRepository.save(reponse);
                }
            }
        }
        // Sauvegarde des groupes applicatifs secondaires
        if (demandeDto.getProfils() != null) {
            LienDemGrpApplicatif lien = null;
            // Avant d'enregistrer de nouveaux liens, on supprime les anciens.
            lienDemGrpApplicatifRepository
                    .deleteAllByDemande_Id(demandeDroit.getId());
            // Et on sauvegarde les nouveaux liens.
            for (ProfilDto profil : demandeDto.getProfils()) {
                lien = new LienDemGrpApplicatif();
                lien.setIdDemande(demandeDroit.getId());
                lien.setIdGrp(profil.getIdGrp());
                lienDemGrpApplicatifRepository.save(lien);
            }
        }
        // Pour la visualisation de la sauvegarde on ajoute l'id de la demande
        // cree au dto.
        demandeDto.setId(demandeDroit.getId());
        log.info("Sauvegarde de la demande de droit ID: "
                + demandeDroit.getId());
        StatutDemande statut;
        ActeurVue acteur = demandeDto.getActeurBenef();


        if (importMasse) {
            statut = this.statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE);
        } else {
            // Creation du workflow
            if (premiereDemande) {
                OffreDto offreDto = offreMapper.offreToOffreDto(demandeDto
                        .getOffre());
                statut = getWorkflowHabilitationService().createDemande(demandeDto,
                        offreDto, simplifie);
            } else {
                // Cas d'une mise a jour du workflow deja existant
                statut = getWorkflowHabilitationService().completerTache(idTache,
                        validation, demandeDto);
            }
        }
        demandeDroit.setStatut(statut);

        this.demandeDroitRepository.save(demandeDroit);

        if (ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE.equals(statut.getCode())) {
            /**
             if (null != demandeDto.getExtensionOffre() && demandeDto.getExtensionOffre() instanceof ExtensionToipDto) {
             ExtensionToipDto etd = (ExtensionToipDto) demandeDto.getExtensionOffre();
             ActeurVue av = this.acteurVueDao.selectById(demandeDto.getIdActeur());
             if (null != av) {
             // on enregistre les donnees de la TOIP
             this.adDao.saveDonneesToip(av.getLogin(), etd);
             if (null == etd.getDesktopProfil() || etd.getDesktopProfil().length() == 0) {
             this.adDao.saveTelephoneNumber(av.getLogin(), etd.getTelephoneNumber(),
             av.getTypeActeur());
             }
             }

             } else **/if (Objects.equals(demandeDroit.getTopDem(), ConstanteWorkflow.DEM_OFFRE_SUPPRESSION_STRING)) {
                ActeurVue av = demandeDto.getActeurBenef();
                // Dans le cas d'une suppression d'habilitation on supprime les pièces jointes
                this.pjService.deletePieceJointe(av.getIdActeur(), PjService.DEMANDE_HAB, demandeDto.getOffre().getId());

                /**
                 if (null != av && null != demandeDroit.getOffre() && demandeDroit.getOffre().getCode().equals(Constante.OFFRE_TOIP)) {
                 // suppression telephoneNumber du LDAP si on supprime loffre toip ou telfixe hors vdm
                 // faut qu il y est aucune des deux si non on supprime pas !
                 // il faut supprimer le desktop profil et ipphone de l'AD s'il s'agit de toip

                 // on supprime les donnee toip de l'AD
                 List<String> attribToDelete = new ArrayList<>();
                 attribToDelete.add(ADDao.ATTR_AD_TOIP_DESKTOP_PROFIL);
                 attribToDelete.add(ADDao.ATTR_AD_TOIP_IP_PHONE);
                 this.adDao.deleteToipActeur(av.getLogin(), attribToDelete);


                 // on supprimer les telephoneNumber
                 if (!this.droitService.isHabilite(demandeDroit.getBeneficiaire().getId(), Constante.OFFRE_TELFIXE)) {
                 this.adDao.deleteTelephoneNumber(av.getLogin(), av.getTypeActeur());
                 }
                 } else if (null != av && null != demandeDroit.getOffre() && demandeDroit.getOffre().getCode().equals(Constante.OFFRE_TELFIXE)
                 && !this.droitService.isHabilite(
                 demandeDroit.getBeneficiaire().getId(), Constante.OFFRE_TOIP)) {
                 // on supprimer les telephoneNumber
                 this.adDao.deleteTelephoneNumber(av.getLogin(), av.getTypeActeur());
                 }
                 **/
            }/** else if (null != demandeDto.getExtensionOffre() && demandeDto.getExtensionOffre() instanceof ExtensionTelephonieFixeHorsVdmDto) {
             ExtensionTelephonieFixeHorsVdmDto eHorsVdm = (ExtensionTelephonieFixeHorsVdmDto) demandeDto.getExtensionOffre();
             ActeurVue av = this.acteurVueDao.selectById(demandeDto.getIdActeur());
             if (null != av) {
             this.adDao.saveTelephoneNumber(av.getLogin(), eHorsVdm.getTelephoneNumber(), av.getTypeActeur());

             }
             }**/
        }

        return demandeDroit;
    }


    /**
     * Methode permettant de verifier si la demande passe en parametre est
     * valide ou non.
     *
     * @param demandeDto  La demandeDto passe en parametre.
     * @param utilisateur L'utilisateur passe en parametre
     * @return boolean true si l'utilisateur est valideur hierarchique final
     * false sinon.
     * @throws ServiceException Exception renvoye si la demande n'est pas valide.
     */
    public boolean verifierPermissionDemande(final DemandeDroitDto demandeDto,
                                             final UtilisateurDto utilisateur) throws ServiceException {
        // Booleen permettant de savoir si l'utilisateur est valideur
        // hierarchique final.
        ActeurVue acteur = demandeDto.getActeurBenef();
        Offre offre = demandeDto.getOffre();
        boolean valideurHierarchiqueFinal = false;
        if (demandeDto.getId() == null) {
            long resultat = verifierPermissionCreation(utilisateur, demandeDto);
            if (resultat == -1) {
                throw new ServiceException(
                        DemandeDroitException.DROITS_INSUFFISANTS_CREATION_HABILITATION);
            } else if (resultat == 0) {
                valideurHierarchiqueFinal = true;
            }
            // lors de la creation d'une demande de type creation ou
            // suppression on verifie si une demande
            // similaire n'existe pas deja dans le referentiel.

            if (String.valueOf(ConstanteWorkflow.DEM_OFFRE_CREATION).equals(demandeDto.getTopDem())) {
                verifierDemandeExistante(acteur.getIdActeur(), offre.getId(), ConstanteWorkflow.DEM_OFFRE_CREATION);
            }
            if (demandeDto.getTopDem().equals(String.valueOf(ConstanteWorkflow.DEM_OFFRE_SUPPRESSION))) {
                verifierDemandeExistante(acteur.getIdActeur(), offre
                        .getId(), ConstanteWorkflow.DEM_OFFRE_SUPPRESSION);
            }
        }
        if (acteur.getTypeActeur().equals(ConstanteActeur.TYPE_APPLICATIF)
                && utilisateur.hasPermission(ConstanteAdministration.COMPTE_APPLI)) {
            // Les Admins Compte Appli sont considérés comme des VT des offres
            valideurHierarchiqueFinal = true;
        }
        // On verifie si le droit n'a pas deja ete cree en base de donnees.
        if (demandeDto.getTopDem().equals(ConstanteWorkflow.DEM_OFFRE_CREATION)) {
            verifierExistenceDroit(offre.getId(), acteur
                    .getIdActeur());
        }
        return valideurHierarchiqueFinal;
    }


    /**
     * Methode permettant de verifier si le droit demande pour un acteur
     * n'existe pas deja dans le regerentiel
     *
     * @param idOffre  L'identifiant de l'offre
     * @param idActeur L'identifiant de l'acteur
     * @throws DemandeDroitException Exception renvoyee lorsque le droit existe deja.
     */
    private void verifierExistenceDroit(final long idOffre, final long idActeur)
            throws DemandeDroitException {
        long droit = this.droitRepository.countByOffreAndActeur(idActeur, idOffre);
        // FIXME : A supprimer apres 1.4.2
        // 27/07/2015 - BGE - EVT-10859 : Suppression du contrele bloquant
        // lorsque le droit existe deja
        // throw new DemandeDroitException(
        // DemandeDroitException.DROIT_EXISTE_DEJA);
    }

    /**
     * Methode permettant de verifier si l'utilisateur est autorise a faire la
     * demande.
     *
     * @param utilisateur L'utilisateur responsable de la creation de la demande.
     * @param demandeDto  La demandeDto .
     * @return 0 si l'utilisateur est valideur hierarchique pour cet offre -1 si
     * l'utilisateur ne possede pas les droits de creation 1 si
     * l'utilisateur possede le droit de creation
     */
    private long verifierPermissionCreation(final UtilisateurDto utilisateur,
                                            final DemandeDroitDto demandeDto) {
        if (demandeDto.getActeurBenef() != null) {
            ActeurVue acteur = demandeDto
                    .getActeurBenef();
            String cle = CelluleUtils.getCle(acteur.getCellule());
            if (cle == null && acteur.getTypeActeur().equals(ConstanteActeur.TYPE_APPLICATIF)) {
                cle = "MAR/   /63783     "; // les comptes applciatif ont l'affectation DGATRANSFO
            }


            // Si l'utilisateur est valideur hierarchique final alors peut creer
            // la demande
            if (utilisateur.getOffresValidation()
                    .isValideurHierarchiqueOffreFinal(demandeDto.getOffre().getId(),
                            cle)) {
                return 0;
            }

            OffreValidationDto offreValidation = utilisateur
                    .getOffresValidation().getMapOffresValidation().get(
                            demandeDto.getOffre().getId());
            // 28/10/2014 - WVE - 8861 - si l'utilisateur courant est valideur
            // technique de l'offre
            // 15/12/2014 - BGE - 9169 : Correction d'un null pointer exception
            if (offreValidation != null
                    && offreValidation.isValideurTechnique()) {
                if (log.isDebugEnabled()) {
                    log
                            .debug("L'utilisateur "
                                    + utilisateur.getNom()
                                    + " est valideur technique, on autorise la creation");
                }
                return 1;
            }

            if (utilisateur.hasPermission(ConstanteAdministration.GERER_DEMANDES_HABILITATIONS)) {
                return 1;
            }

            // Si l'utilisateur appartient a la meme
            // affectation que l'acteur ou que l'affectation de l'acteur est une
            // subdivision de l'affection de l'utilisateur
            AffectationDto affectUtilisateur = utilisateur
                    .getAffectation();
            // Si l'offre est privee pour la delegation de l'utilisateur alors
            // on rejete la demande.
            ParametreWorkflowDroit parametrage = this.parametreWorkflowDroitRepository.findParametreWorkflowDroitByCellule(
                    demandeDto.getOffre().getId(), affectUtilisateur
                            .getAffectation(1));
            if (String.valueOf(ConstanteWorkflow.DEM_OFFRE_CREATION).equals(demandeDto.getTopDem())
                    && parametrage != null && parametrage.getTopPrive()) {
                if (log.isDebugEnabled()) {
                    log.debug("L'utilisateur " + utilisateur.getNom()
                            + " ne peut creer une demande sur une offre"
                            + " privee pour l'acteur " + acteur.getNom());
                }
                return -1;
            }
            if (affectUtilisateur != null) {
                List<String> cleAutorise = utilisateur.getAffectationsFilles();
                cleAutorise.add(affectUtilisateur.getCodeAffectation());
                // 15/12/2014 - BGE - EVT-9204 : PRise en compte des niveaux de
                // validations hierarchique intermediaire de l'utilisateur
                if (offreValidation != null) {
                    for (List<String> listeCellules : offreValidation
                            .getValideursHierarchique()) {
                        cleAutorise.addAll(listeCellules);
                    }
                }
                // 15/12/2014 - BGE - EVT-9204 : PRise en compte des niveaux de
                // validations hierarchique intermediaire de l'utilisateur - FIN
                for (String cleTmp : cleAutorise) {
                    if (cleTmp.equals(cle)) {
                        return 1;
                    }
                }
            }

            if (acteur.getTypeActeur().equals(ConstanteActeur.TYPE_APPLICATIF)) {

                return 1;
            }

            if (log.isDebugEnabled()) {
                log.debug("L'utilisateur " + utilisateur.getNom()
                        + " n'a pas d'habilitation sur l'acteur "
                        + acteur.getNom());
            }
        }
        return -1;
    }

    /**
     * Cloture toutes les demandes concernant un acteur et une offre passe en
     * parametre. du workflow
     *
     * @param idActeur  l'id de l'acteur concerne.
     * @param idOffre   id de l'offre concerne.
     * @param idDemande id de la demande a ne pas supprimer.(optionnel)
     */
    @Override
    public void cloturerDemande(final long idActeur, final Long idOffre,
                                final Long idDemande) {
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

}