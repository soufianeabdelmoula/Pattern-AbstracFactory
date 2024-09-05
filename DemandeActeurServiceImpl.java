package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.exception.rest.handler.ActeurException;
import fr.vdm.referentiel.refadmin.exception.rest.handler.DemandeActeurException;
import fr.vdm.referentiel.refadmin.mapper.DemandeMapper;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.ActeurVueRepository;
import fr.vdm.referentiel.refadmin.repository.DemandeActeurRepository;
import fr.vdm.referentiel.refadmin.repository.EtapeDemActeurRepository;
import fr.vdm.referentiel.refadmin.service.ActeurService;
import fr.vdm.referentiel.refadmin.service.DemandeActeurService;
import fr.vdm.referentiel.refadmin.service.PjService;
import fr.vdm.referentiel.refadmin.service.WorkflowActeurService;
import fr.vdm.referentiel.refadmin.utils.ConstanteActeur;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import fr.vdm.referentiel.refadmin.utils.ListsUtils;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DemandeActeurServiceImpl implements DemandeActeurService {
    private final ActeurVueRepository acteurVueRepository;
    private ActeurService acteurService;
    private DemandeActeurRepository demandeActeurRepository;
    private PjService pjService;
    private final WorkflowActeurService workflowActeurService;
    private final EtapeDemActeurRepository etapeDemActeurRepository;
    private final CelluleServiceImpl celluleService;
    private final UtilisateurServiceImpl utilisateurService;
    private final TacheActeurServiceImpl tacheActeurService;
    private static final DemandeMapper demandeMapper = DemandeMapper.INSTANCE;

    public DemandeActeurServiceImpl(ActeurService acteurService, DemandeActeurRepository demandeActeurRepository, PjService pjService, WorkflowActeurService workflowActeurService, EtapeDemActeurRepository etapeDemActeurRepository, CelluleServiceImpl celluleService, UtilisateurServiceImpl utilisateurService, TacheActeurServiceImpl tacheActeurService,
                                    ActeurVueRepository acteurVueRepository) {
        this.acteurService = acteurService;
        this.demandeActeurRepository = demandeActeurRepository;
        this.pjService = pjService;
        this.workflowActeurService = workflowActeurService;
        this.etapeDemActeurRepository = etapeDemActeurRepository;
        this.celluleService = celluleService;
        this.utilisateurService = utilisateurService;
        this.tacheActeurService = tacheActeurService;
        this.acteurVueRepository = acteurVueRepository;
    }


    public void saveDemandeActeur(DemandeActeurDto demandeActeurDto, String login) throws ServiceException, DemandeActeurException {
        log.info("Sauvegarde de la demande acteur");


        if ((!StringUtils.isNotBlank(demandeActeurDto.getCommentaire())) || demandeActeurDto.getCommentaire().trim().isEmpty()) {
            throw new ServiceException("Le commentaire est obligatoire.");
        }

        if (this.acteurService.getActeurVueByLogin(demandeActeurDto.getLogin()) != null) {
            throw new ServiceException("Ce login existe déjà.");
        }

        if (demandeActeurDto.getTypeActeur().equals(ConstanteActeur.TYPE_AGENT) && this.acteurService.getActeurVueByIdtaAndIdtn(demandeActeurDto.getIdta(), demandeActeurDto.getIdtn()) != null) {
            throw new ServiceException("L'acteur existe déjà dans le référentiel.");
        }

        log.info("Création de la demande acteur.");

        StatutDemande statutDemande = StatutDemande.builder().id(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H_ID).build();
        DemandeActeur demandeActeur = saveDemande(demandeActeurDto, statutDemande);

        log.info("Création de l'etape de la demande acteur.");

        EtapeDemActeur etapeDemActeur = createEtpDemActeur(demandeActeur.getId(), demandeActeurDto.getCommentaire(), login, ConstanteWorkflow.TYPE_ETAPE_SIMPLIFIEE, null);

        StatutTache statutTache = StatutTache.builder().id(ConstanteWorkflow.STATUT_TACHE_EN_COURS_ID).build();

        log.info("Création de la tache acteur.");
        TacheActeur tacheActeur = saveTacheActeur(demandeActeur,statutTache, 0L);

    }

    private DemandeActeur saveDemande(DemandeActeurDto demandeActeurDto, StatutDemande statut) {
        DemandeActeur demande = demandeMapper.demandeActeurDtoToDemandeActeur(demandeActeurDto);
        if (demande.getCellule() != null) {
            demande.setColl("MAR");
            demande.setSsColl("   ");
        }
        if (demande.getCelluleTerrain() != null) {
            demande.setCollTerrain("MAR");
            demande.setSsCollTerrain("   ");
        }
        demande.setStatut(statut);
        return demandeActeurRepository.save(demande);
    }

    private TacheActeur saveTacheActeur(DemandeActeur demande, StatutTache statutTache, Long niveau){
        TacheActeur tacheActeur = TacheActeur.builder()
                .statutTache(statutTache)
                .demandeActeur(demande)
                .niveau(niveau)
                .build();
        if(tacheActeur.getNiveau() > 0L ){
            tacheActeur.setActivite(ConstanteWorkflow.ACTIVITE_HIERARCHIQUE_INTERMEDIAIRE);
        }else{
            tacheActeur.setActivite(ConstanteWorkflow.ACTIVITE_HIERARCHIQUE_FINALE);
        }
        return tacheActeurService.save(tacheActeur);
    }

    @Override
    public DemandeActeurDto findDemandeActeurByIdDemandeAc(Long idDemandeAc) {

        DemandeActeurDto demandeActeurDto = new DemandeActeurDto(this.demandeActeurRepository.findById(idDemandeAc).orElse(null));
        demandeActeurDto.setPiecesJointes(this.pjService.getAllByIdDemandeAndTypeDemande(demandeActeurDto.getId(), demandeActeurDto.getTypeDemande()));
        return demandeActeurDto;
    }


    @Transactional
    public void saveEtapeDemandeComplete(EtapeDemActeurDto etapeDemActeurDto) throws ServiceException {
        if (!org.springframework.util.StringUtils.hasText(etapeDemActeurDto.getCommentaire())) {
            etapeDemActeurDto.setCommentaire("Validation sans commentaire");
        }
        List<TacheActeur> tacheActeurs = tacheActeurService.findByDemandeActeurId(etapeDemActeurDto.getIdDemande());
        Optional<TacheActeur> tache = tacheActeurs.stream().filter(tacheActeur -> ConstanteWorkflow.STATUT_TACHE_EN_COURS.equals(tacheActeur.getStatutTache().getCode()))
                .collect(Collectors.toList()).stream().findFirst();
        if (!tache.isPresent()) return;
        Long tacheId = tache.get().getId();
        UtilisateurDto utilisateurDto = utilisateurService.getUser();
        saveDemandeCreationComplete(etapeDemActeurDto.getIdDemande(), utilisateurDto, etapeDemActeurDto, tacheId, false);
    }

    @Override
    public EtapeDemActeur findFirstEtap(Long idDemande) {
        return etapeDemActeurRepository.findFirstEtape(idDemande);
    }

    public void saveDemandeCreationComplete(Long idDemandeActeur, UtilisateurDto utilisateurDto, EtapeDemActeurDto etapeDemActeurDto,
                                            Long idTache, boolean isImport) throws ServiceException {

        DemandeActeur demandeActeur = demandeActeurRepository.findById(idDemandeActeur).orElse(null);
        boolean premiereDemande = demandeActeur == null;


        if (premiereDemande) {
            // Verification dans le webservice que l'agent n'existe pas deje
            // dans le
            // referentiel
            // ou encore que le matricule rentre est valide et que l'utilisateur
            // possede les droits
            // de creation sur cet agent.
            if (demandeActeur.getTypeActeur().equals(ConstanteActeur.TYPE_AGENT)) {
                remplirAgent(demandeActeur, utilisateurDto, isImport);
            }
            // Verification de l'email
            // verifierEmail(demandeActeur.getEmail());
            // Verification du login
            // verifierLogin(demandeActeur.getLogin());
            // on pose temporairement le statut en cours hierarchique
            // ce statut est remplace par le workflow
            demandeActeur.setTypeDemande(ConstanteWorkflow.DEM_ACTEUR_CREATION_STRING);
            StatutDemande statutDemandeEnCoursH = StatutDemande.builder()
                    .id(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H_ID).build();
            demandeActeur.setStatut(statutDemandeEnCoursH);
            demandeActeurRepository.saveAndFlush(demandeActeur);
        }


        // Sauvagarde de la demandeActeur acteur.
        // Creation de l'etape de la demandeActeur acteur.
        createEtpDemActeur(demandeActeur.getId(), etapeDemActeurDto.getCommentaire(),
                utilisateurDto.getLogin(), ConstanteWorkflow.TYPE_ETAPE_COMPLETE, etapeDemActeurDto.getTopEtape());

        StatutDemande statut = null;
        StatutWorkFlowDto workflowDto = null;
        // Creation du workflow
        if (premiereDemande) {
            statut = workflowActeurService.createDemande(demandeActeur, false, isImport);
        } else {
            // Cas d'une mise e jour du workflow deje existant
            workflowDto = workflowActeurService.completerTache(idTache, etapeDemActeurDto.getTopEtape(),
                    demandeActeur, null, null, null, null, null, isImport);
        }
        if (workflowDto == null) {
            demandeActeur.setStatut(statut);
        } else {
            demandeActeur.setStatut(workflowDto.getStatut());
            if (ConstanteWorkflow.STATUT_DEMANDE_REFUSEE.equals(workflowDto.getStatut())) {
                // Si la demandeActeur est refuse on supprime toutes les PJ relative a la demandeActeur
                // this.pieceJointeService.deletePieceJointe(null, PieceJointeServiceImpl.DEMANDE_ACTEUR, demandeActeur.getId());
            }
        }
        demandeActeurRepository.save(demandeActeur);
    }

    private void remplirAgent(final DemandeActeur demande, final UtilisateurDto utilisateurDto, boolean isImport) throws ServiceException {
        // On recupere les informations e partir du webservice.
        // On sort de la methode si une exception est releve

        AgentDto agent = null;
        try {
            agent = acteurService.selectAgentRHByMatricule(demande.getIdta(), demande.getIdtn(), utilisateurDto, isImport);
        } catch (ActeurException e) {
            if (e.getCode() == ActeurException.MATRICULE_RH_INTROUVABLE) {
                throw new DemandeActeurException(DemandeActeurException.MATRICULE_RH_INTROUVABLE);
            }
            if (e.getCode() == ActeurException.MATRICULE_REFERENTIEL_EXISTE_DEJA) {
                throw new DemandeActeurException(DemandeActeurException.MATRICULE_REFERENTIEL_EXISTE_DEJA);
            }
            if (e.getCode() == ActeurException.DROITS_INSUFFISANTS_CREATION_AGENT) {
                throw new DemandeActeurException(DemandeActeurException.DROITS_INSUFFISANTS_CREATION_AGENT);
            }
        } catch (Exception e) {
            throw new DemandeActeurException(DemandeActeurException.ERREUR_INNATENDU);
        }

        if (agent == null) {
            throw new ServiceException(ServiceException.ERREUR_INNATENDU);
        }
        // Changement de l'affectation, du nom , du prenom et
        // du nom marital a partir du webservice
        if (ListsUtils.isEmptyOrNull(agent.getAffectationOfficielle())) {
            CelluleDto celluleDto = agent.getAffectationOfficielle().get(
                    agent.getAffectationOfficielle().size() - 1);
            demande.setColl(celluleDto.getCollectivite());
            demande.setSsColl(celluleDto.getSsCollectivite());
            demande.setCellule(celluleDto.getCode());
        }

        // l'affectation terrain est supprimee si elle est egale a l'affectation
        // officielle
        if (demande.getCelluleTerrain() != null
                && demande.getCellule().equals(demande.getCelluleTerrain())) {
            demande.setCelluleTerrain(null);
            demande.setCollTerrain(null);
            demande.setSsCollTerrain(null);
        }
        demande.setNom(agent.getNom());
        demande.setPrenom(agent.getPrenom());
        demande.setNomMarital(agent.getNomMarital());
        demande.setCelluledet(agent.getCelluleDet());
    }
    private EtapeDemActeur createEtpDemActeur(Long demandeId, String commentaire, String utilisateur, String typeDemande, Boolean validation) {

        EtapeDemActeur etapePrecedente = demandeActeurRepository.selectDerniereEtape(demandeId);

        EtapeDemActeur etapeCreation = new EtapeDemActeur();
        etapeCreation.setCommentaire(commentaire);
        DemandeActeur demandeActeur = DemandeActeur.builder().id(demandeId).build();
        etapeCreation.setDemande(demandeActeur);

        etapeCreation.setTopEtape(validation == null || validation);
        if (etapePrecedente != null) {
            etapeCreation.setOrdre(etapePrecedente.getOrdre() + 1);
        } else {
            etapeCreation.setOrdre(0L);
        }
        etapeCreation.setLogin(utilisateur);
        etapeCreation.setTypeEtape(typeDemande);
        return etapeDemActeurRepository.save(etapeCreation);
    }

    @Override
    public List<DemandeActeur> findDemandesByActeur(Long idActeur) {
        return this.demandeActeurRepository.findAllByActeurBenef_IdActeur(idActeur);
    }

}