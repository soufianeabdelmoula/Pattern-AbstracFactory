package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.mapper.DemandeDroitMapper;
import fr.vdm.referentiel.refadmin.mapper.OffreMapper;
import fr.vdm.referentiel.refadmin.mapper.TacheHabilitationMapper;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.TacheHabilitationRepository;
import fr.vdm.referentiel.refadmin.service.ActeurVueService;
import fr.vdm.referentiel.refadmin.service.TacheHabilitationService;
import fr.vdm.referentiel.refadmin.service.UtilisateurService;
import fr.vdm.referentiel.refadmin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
@Transactional
@Log4j2
public class TacheHabilitationServiceImpl implements TacheHabilitationService {
    @Autowired
    UtilisateurService utilisateurService;
    private final TacheHabilitationRepository tacheHabilitationRepository;
    private static final TacheHabilitationMapper tacheHabilitationMapper = TacheHabilitationMapper.INSTANCE;
    private final ActeurVueService acteurVueService;

    public TacheHabilitationServiceImpl(TacheHabilitationRepository tacheHabilitationRepository, ActeurVueService acteurVueService) {
        this.tacheHabilitationRepository = tacheHabilitationRepository;
        this.acteurVueService = acteurVueService;
    }


    public TacheHabilitation findById(long idTache) {
        Optional<TacheHabilitation> tacheHabilitation = tacheHabilitationRepository.findById(idTache);
        return tacheHabilitation.orElse(null);
    }
    @Override
    public List<TacheHabilitationDto> getTachesHabilitations(Pageable pageable) {
        Page<TacheHabilitation> tacheHabilitations = tacheHabilitationRepository.findAll(pageable);

        return tacheHabilitationMapper.tacheHabilitationToDtoList(tacheHabilitations.getContent());
    }

    @Override
    public List<TacheHabilitationDto> getByAssigneId(Long assigneId) {
        List<TacheHabilitation> tacheHabilitations = tacheHabilitationRepository.findByIdassigne(assigneId);
        return tacheHabilitationMapper.tacheHabilitationToDtoList(tacheHabilitations);
    }

    @Override
    public Page<TacheHabilitationDto> getByInputAndStatut(List<String> statutCode, String loginAssigne, String input, Pageable page) throws ServiceException {
        ActeurVue acteurVue = this.acteurVueService.getActeurVueByLogin(loginAssigne);
        if (acteurVue == null){
            log.warn(String.format("Attention le login %s n'existe pas dans le referentiel.", loginAssigne));
            throw new ServiceException("Votre nom d'utilisateur n'existe pas");
        }
        List<String> cellulesH = new ArrayList<>();
        Arrays.stream(utilisateurService.getUser().getActeurValidation().getValidateursHierarchique()).forEach(cellulesH::addAll);
        cellulesH.addAll(utilisateurService.getUser().getActeurValidation().getValidateurHierarchiqueFinal());


        List<String> cellulesT = new ArrayList<>();
        log.debug("get taches habilitations");
        return tacheHabilitationRepository.findAllByInputAndIdStatut(input, acteurVue.getIdActeur(), statutCode, page).map(TacheHabilitationMapper.INSTANCE::tacheHabilitationToDto);
    }
    @Override
    public Page<TacheHabilitationDto> getByInputAndStatutForMyTask(List<String> statutCode, String loginAssigne, String input, Pageable page) {
        return tacheHabilitationRepository.findAllByInputAndIdStatutForMyTask(loginAssigne, statutCode, page).map(TacheHabilitationMapper.INSTANCE::tacheHabilitationToDto);}


    @Override
    public void validerTachesHabilitations(List<Long> ids) {
        List<TacheHabilitation> tacheHabilitations = tacheHabilitationRepository.findAllById(ids);
        StatutTache tacheValidee = StatutTache.builder()
                .id(TacheStatut.VALIDEE)
                .build();
        tacheHabilitations.forEach(tacheHabilitation ->
                tacheHabilitation.setStatutTache(tacheValidee));
        tacheHabilitationRepository.saveAll(tacheHabilitations);
    }

    @Override
    public void refuserTachesHabilitations(List<Long> ids) {
        List<TacheHabilitation> tacheHabilitations = tacheHabilitationRepository.findAllById(ids);
        StatutTache tacheRefusee = StatutTache.builder()
                .id(TacheStatut.REFUSEE)
                .build();
        tacheHabilitations.forEach(tacheHabilitation ->
                tacheHabilitation.setStatutTache(tacheRefusee));
        tacheHabilitationRepository.saveAll(tacheHabilitations);
    }

    @Override
    public List<TacheHabilitation> findTacheHabilitationByActiviteAndOffreAndStatut(String activite, Long idOffre, String statutTache) {
        return tacheHabilitationRepository.findTacheHabilitationByActiviteAndOffreAndStatut(activite, idOffre, statutTache);
    }

    @Override
    public List<TacheHabilitation> findTachesByAffectationAndNiveau(String activite, Long niveau, List<String> affectations, Long idOffre) {
        return tacheHabilitationRepository.findTachesByAffectationAndNiveau(activite, niveau,affectations, idOffre);
    }

    @Override
    public List<TacheHabilitation> findTachesByAffectation(String activite, List<String> affectations, Long idOffre) {
        return tacheHabilitationRepository.findTachesByAffectation(activite, affectations, idOffre);
    }

    @Override
    public List<TacheHabilitation> findByActeurAndOffre(long idActeur, long idOffre) {
        return tacheHabilitationRepository.findByActeurAndOffre(idActeur, idOffre);
    }

    @Override
    public Long createTache(DemandeDroit demande, Offre offrePrincipale, Offre offreDependance, String activite, StatutTache statutTache, long niveau) {
        TacheHabilitation tache = new TacheHabilitation();
        tache.setOffrePrincipale(offrePrincipale);
        tache.setOffreDependance(offreDependance);
        tache.setDemandeDroit(demande);
        tache.setStatutTache(statutTache);
        tache.setActivite(activite);
        tache.setNiveau(niveau);

        tacheHabilitationRepository.save(tache);
        return tache.getId();
    }

    @Override
    public void save(TacheHabilitation tache) {
        tacheHabilitationRepository.save(tache);
    }

    @Override
    public List<TacheHabilitation> findByIdOffreAndBeneficiaireAndStatut(long idOffrePrincipale, long idActeurBeneficiaire, String statutCode) {
        return tacheHabilitationRepository.findByIdOffreAndBeneficiaireAndStatut(idOffrePrincipale, idActeurBeneficiaire, statutCode);
    }

    @Override
    public Long getLastTache(long idDemande) {
        List<TacheHabilitation> tacheHabilitations = tacheHabilitationRepository.findByDemandeDroitId(idDemande);
        Optional<TacheHabilitation> tache = tacheHabilitations.stream().filter(tacheHabilitation -> ConstanteWorkflow.STATUT_TACHE_EN_COURS.equals(tacheHabilitation.getStatutTache().getCode()))
                .collect(Collectors.toList()).stream().findFirst();
        return tache.map(TacheHabilitation::getId).orElse(0L);
    }

    /** {@inheritDoc} */
    @Override
    public void assignerTacheHabilitation(String loginAssigne, Long idTache, List<String> statutCode) throws ServiceException {
        ActeurVue acteurVue = this.acteurVueService.getActeurVueByLogin(loginAssigne);
        if (acteurVue == null){
            log.warn(String.format("Attention le login %s n'existe pas dans le referentiel.", loginAssigne));
            throw new ServiceException("Votre nom d'utilisateur n'existe pas");
        }

        TacheHabilitation habilitation = this.tacheHabilitationRepository.findTacheHabilitationByIdAndActive(idTache, statutCode);

        if (habilitation == null){
            log.warn(String.format("La tâche %s n'est pas présente dans le référentiel, soit elle a déjà été assignée.", idTache));
            throw new ServiceException("La tâche sélectionnée n'est pas présente dans le référentiel, soit elle a déjà été assignée.");
        }

        habilitation.setIdassigne(acteurVue.getIdActeur());
        log.info(String.format("Assignée la tâche %s à %s", habilitation.getId(), acteurVue.getLogin()));
        this.tacheHabilitationRepository.save(habilitation);
        log.info("Assignation terminé");
    }

    /** {@inheritDoc} */
    @Override
    public void assignerToutesLesTacheHabilitation(String loginAssigne, List<Long> idTaches, List<String> statutCode) throws ServiceException {
        for (Long id: idTaches){
            this.assignerTacheHabilitation(loginAssigne, id, statutCode);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void libererTacheHabilitation(String loginAssigne, Long idTache, List<String> statutCode) throws ServiceException {
        TacheHabilitation habilitation = this.tacheHabilitationRepository.findTacheHabilitationByIdAndAssigneeAndActive(idTache, loginAssigne, statutCode);

        if (habilitation == null){
            log.warn(String.format("La tâche %s est soit absente du référentiel, soit vous n'avez pas l'autorisation de la libérer.", idTache));
            throw new ServiceException("La tâche est soit absente du référentiel, soit vous n'avez pas l'autorisation de la libérer.");
        }

        habilitation.setIdassigne(null);
        log.info(String.format("La tâche %s a été libérer par %s", habilitation.getId(), loginAssigne));
        this.tacheHabilitationRepository.save(habilitation);
        log.info("La tâche libérer, terminé");
    }

    /** {@inheritDoc} */
    @Override
    public void libererToutesLesTacheHabilitationParUnAdmin(String loginAdmin, List<Long> idTaches, List<String> statutCode) throws ServiceException {
        if (loginAdmin.equals("gtaby")){ // Preciser que c'est un ADMIN (Temporaire)
            for (Long id: idTaches){
                TacheHabilitation habilitation = this.tacheHabilitationRepository.findTacheHabilitationByIdAndStatutCodeAndActive(id, statutCode);

                if (habilitation == null){
                    log.warn(String.format("La tâche %s est soit absente du référentiel, soit vous n'avez pas l'autorisation de la libérer.", id));
                    throw new ServiceException("La tâche est soit absente du référentiel, soit vous n'avez pas l'autorisation de la libérer.");
                }

                habilitation.setIdassigne(null);
                log.info(String.format("La tâche %s a été libérer par %s", habilitation.getId(), loginAdmin));
                this.tacheHabilitationRepository.save(habilitation);
                log.info("La tâche libérer, terminé");
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long countMyTaskhabilitations(String loginAssigne, List<String> statut) {
        return tacheHabilitationRepository.countTaskActeur(loginAssigne, statut);
    }

    @Override
    public List<TacheHabilitation> findByIdDemande(Long idDemande) {
        return this.tacheHabilitationRepository.findByDemandeDroitId(idDemande);
    }

    @Override
    public byte[] getExportTacheHabilitation(List<String> statutCode) {
        List<TacheHabilitationDto> tacheHabilitationDtos = tacheHabilitationMapper.tacheHabilitationToDtoList(this.tacheHabilitationRepository.findAllTacheHabilitationForExportCsv(statutCode));
        return ExportFileCsvUtils.exportCsvFile(tacheHabilitationDtos);
    }

    @Override
    public Page<TacheHabilitationDto> findTachesDispo(List<String> status, String loginAssigne, String input, Pageable pageable) throws ServiceException {
        ActeurVue acteurVue = this.acteurVueService.getActeurVueByLogin(loginAssigne);
        if (acteurVue == null) {
            log.warn(String.format("Attention le login %s n'existe pas dans le referentiel.", loginAssigne));
            throw new ServiceException("Votre nom d'utilisateur n'existe pas");
        }

        List<OffreValidationDto> offreValidationDtoList = utilisateurService.getUser().getOffresValidation().getOffresValidation().stream().collect(Collectors.toList());
        ArrayList<OffreValidationDto> list = new ArrayList<>();

        // Filtre user qui ont pas vH Vt
        for (OffreValidationDto offre : offreValidationDtoList) {

            if (offre.getValidateurHierarchiqueFinal() != null
                    && !offre.getValidateurHierarchiqueFinal().isEmpty()
                    && offre.isValideurTechnique()
                    && utilisateurService.getUser().getOffresValidation().isValideur()) {
                list.add(offre);
            }
        }


        Page<TacheHabilitationDto> tacheHabilitationDtos = this.getByInputAndStatut(status, loginAssigne, input, pageable);


        // Filtre entre tacheHabilitation et offre
        List<TacheHabilitationDto> tacheHabilitationFiltredList = new ArrayList<>();
        for (TacheHabilitationDto tache : tacheHabilitationDtos) {
            for (OffreValidationDto offre : list) {
                if (tache != null && offre != null) {
                    if (tache.getNomOffrePrincipal() != null && offre.getNomOffre() != null) {
                        if (tache.getNomOffrePrincipal().equals(offre.getNomOffre())) {
                            tacheHabilitationFiltredList.add(tache);
                        }
                    }
                }
            }
        }

        return new PageImpl<>(tacheHabilitationFiltredList, pageable, tacheHabilitationFiltredList.size());
    }


}