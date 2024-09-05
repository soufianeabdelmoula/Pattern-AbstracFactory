package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.DroitDto;
import fr.vdm.referentiel.refadmin.dto.TacheActeurDto;
import fr.vdm.referentiel.refadmin.mapper.TacheActeurMapper;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.ActeurRepository;
import fr.vdm.referentiel.refadmin.repository.CelluleRepository;
import fr.vdm.referentiel.refadmin.repository.StatutTacheRepository;
import fr.vdm.referentiel.refadmin.repository.TacheActeurRepository;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import fr.vdm.referentiel.refadmin.utils.ExportFileCsvUtils;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TacheActeurServiceImpl implements TacheActeurService {
    @Autowired
    UtilisateurService utilisateurService;

    @Autowired
    private TacheActeurRepository tacheActeurRepository;
    @Autowired
    private StatutTacheRepository statutTacheRepository;
    private static final TacheActeurMapper tacheActeurMapper = TacheActeurMapper.INSTANCE;
    @Autowired
    private ActeurRepository acteurRepository;

    @Autowired
    DroitService droitService;
    @Autowired
    ActeurService acteurService;
    @Autowired
    CelluleService celluleService;
    @Autowired
    private CelluleRepository celluleRepository;

    @Override
    public Page<TacheActeurDto> getTachesActeurs(Pageable pageable) {
        Page<TacheActeur> tacheActeurs = tacheActeurRepository.findAll(pageable);
        return tacheActeurs.map(tacheActeurMapper::tacheActeurToDto);
    }

    @Override
    public List<TacheActeurDto> getByStatut(String statutCode) {
        StatutTache statutTache = statutTacheRepository.findByCode(statutCode);
        List<TacheActeur> tacheActeurs = tacheActeurRepository.findByStatutTacheId(statutTache.getId());
        return tacheActeurMapper.tacheActeurToDtoList(tacheActeurs);
    }

    @Override
    public Page<TacheActeurDto> getAllTaskByInputAndIdStatutForAll(List<String> statutCode, String loginAssigne, String input, Pageable page) throws ServiceException {
        Acteur acteur = this.acteurRepository.findActeurByLogin(loginAssigne);
        if (acteur == null){
            log.warn(String.format("Attention le login %s n'existe pas dans le referentiel.", loginAssigne));
            throw new ServiceException("Votre login %s n'existe pas");
        }
        List<String> cellulesH = new ArrayList<>();
        Arrays.stream(utilisateurService.getUser().getActeurValidation().getValidateursHierarchique()).forEach(cellulesH::addAll);
        cellulesH.addAll(utilisateurService.getUser().getActeurValidation().getValidateurHierarchiqueFinal());


        List<String> cellulesT = new ArrayList<>();
        return tacheActeurRepository.findAllByInputAndIdStatutForAll(input, acteur.getIdActeur(), statutCode, page, cellulesH.stream().distinct().collect(Collectors.toList()), cellulesT.stream().distinct().collect(Collectors.toList()));
    }

    @Override
    public Page<TacheActeurDto> getAllTaskByInputAndIdStatutAndForMyTask(List<String> statutCode, String loginAssigne, String input, Pageable page) {
        return tacheActeurRepository.findAllByInputAndIdStatutAndForMyTask(loginAssigne, statutCode, page);
    }

    @Override
    public List<TacheActeur> getByAssigneId(Long assigneId) {
        return tacheActeurRepository.findByAssigneIdActeur(assigneId);
    }

    @Override
    public TacheActeurDto getById(Long idTacheActeur) {
        Optional<TacheActeur> tacheActeur = tacheActeurRepository.findById(idTacheActeur);
        return tacheActeur.map(tacheActeurMapper::tacheActeurToDto).orElse(null);
    }

    public TacheActeur findById(Long idTacheActeur) {
        Optional<TacheActeur> tacheActeur = tacheActeurRepository.findById(idTacheActeur);
        return tacheActeur.orElse(null);
    }

    @Override
    public Serializable createTache(DemandeActeur demande, String activite, StatutTache statutTache) {
        TacheActeur tache = new TacheActeur();
        tache.setDemandeActeur(demande);
        tache.setStatutTache(statutTache);
        tache.setActivite(activite);
        tache.setNiveau(0L);

        return tacheActeurRepository.save(tache);
    }

    @Override
    public TacheActeur save(TacheActeur tacheActeur) {
        return tacheActeurRepository.save(tacheActeur);
    }

    @Override
    public List<TacheActeur> findByDemandeActeurId(Long idDemandeActeur) {
        return tacheActeurRepository.findByDemandeActeurId(idDemandeActeur);
    }

    /** {@inheritDoc} */
    @Override
    public void assignerTacheActeur(String loginAssigne, Long idTache, List<String> statutCode) throws ServiceException {

        Acteur acteur = this.acteurRepository.findActeurByLogin(loginAssigne);
        if (acteur == null){
            log.warn(String.format("Attention le login %s n'existe pas dans le referentiel.", loginAssigne));
            throw new ServiceException("Votre login %s n'existe pas");
        }

        TacheActeur tacheActeur = this.tacheActeurRepository.findTacheActeurByIdAndActive(idTache, statutCode);

        if (tacheActeur == null){
            log.warn(String.format("La tâche %s n'est pas présente dans le référentiel, soit elle a déjà été assignée.", idTache));
            throw new ServiceException("La tâche sélectionnée n'est pas présente dans le référentiel, soit elle a déjà été assignée.");
        }

        tacheActeur.setIdassigne(acteur.getIdActeur());
        log.info(String.format("Assignée la tâche %s à %s", tacheActeur.getId(), acteur.getLogin()));
        this.tacheActeurRepository.save(tacheActeur);
        log.info("Assignation terminé");
    }

    /** {@inheritDoc} */
    @Override
    public void assignerToutesLesTacheActeur(String loginAssigne, List<Long> idTaches, List<String> statutCode) throws ServiceException {
        for (Long id: idTaches){
            this.assignerTacheActeur(loginAssigne, id, statutCode);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void libererTacheActeur(String loginAssigne, Long idTache, List<String> statutCode) throws ServiceException {
        TacheActeur tacheActeur = this.tacheActeurRepository.findById(idTache).orElse(null);

        if (tacheActeur == null){
            log.warn(String.format("La tâche %s est soit absente du référentiel, soit vous n'avez pas l'autorisation de la libérer.", idTache));
            throw new ServiceException("La tâche est soit absente du référentiel, soit vous n'avez pas l'autorisation de la libérer.");
        }

        tacheActeur.setIdassigne(null);
        log.info(String.format("La tâche %s a été libérer par %s", tacheActeur.getId(), loginAssigne));
        this.tacheActeurRepository.save(tacheActeur);
        log.info("La tâche libérer, terminé");
    }

    /** {@inheritDoc} */
    @Override
    public void libererToutesLesTacheActeurParUnAdmin(String loginAdmin, List<Long> idTaches, List<String> statutCode) throws ServiceException {
        if (loginAdmin.equals("gtaby")){ // Preciser que c'est un ADMIN (Temporaire)
            for (Long id: idTaches){
                TacheActeur tacheActeur = this.tacheActeurRepository.findTacheActeurByIdAndStatutCodeAndActive(id, statutCode);

                if (tacheActeur == null){
                    log.warn(String.format("La tâche %s est soit absente du référentiel, soit vous n'avez pas l'autorisation de la libérer.", id));
                    throw new ServiceException("La tâche est soit absente du référentiel, soit vous n'avez pas l'autorisation de la libérer.");
                }

                tacheActeur.setIdassigne(null);
                log.info(String.format("La tâche %s a été libérer par %s", tacheActeur.getId(), loginAdmin));
                this.tacheActeurRepository.save(tacheActeur);
                log.info("La tâche libérer, terminé");
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Long countTaskActeur(String loginAssigne, List<String> statutCode) {
        return tacheActeurRepository.countTaskActeur(loginAssigne, statutCode);
    }

    @Override
    public byte[] getExportTacheActeur(List<String> statutCode) {
        List<TacheActeurDto> tacheActeurDtos = TacheActeurMapper.INSTANCE.tacheActeurToDtoList(this.tacheActeurRepository.findAllTacheActeurForExportCsv(statutCode));
        return ExportFileCsvUtils.exportCsvFile(tacheActeurDtos);
    }



    public Long getLastTache(long idDemande) {
        List<TacheActeur> tacheActeurs = tacheActeurRepository.findByDemandeActeurId(idDemande);
        Optional<TacheActeur> tache = tacheActeurs.stream().filter(tacheActeur -> ConstanteWorkflow.STATUT_TACHE_EN_COURS.equals(tacheActeur.getStatutTache().getCode()))
                .collect(Collectors.toList()).stream().findFirst();
        return tache.map(TacheActeur::getId).orElse(null);
    }

    @Override
    public List<TacheActeur> findByActeurBeneficiaire(long idActeur, StatutTache statut) {
        return tacheActeurRepository.findByActeurBeneficiaireAndStatut(idActeur, statut);
    }


    @Override
    public void traitementTacheEnAttente(DemandeDroit demandeDroit) {
        long idActeur = demandeDroit.getActeurBenef().getIdActeur();
        log.info(String.format("Traitement des taches de suppression en attente pour l'acteur : %s", idActeur));

        // Recuperation de la liste des permissions de l'acteur de cette
        // demande de suppression
        List<DroitDto> permissions = droitService.findByIdActeur(idActeur);

        // Si la liste est vide alors on peut le supprimer, sinon il va
        // falloir attendre qu'on supprime toutes ces permissions
        if (permissions.isEmpty()) {
            // Find taches en attente selon acteur
            StatutTache statutTacheAttente = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_ATTENTE_DROITS);

            List<TacheActeur> taches = findByActeurBeneficiaire(idActeur, statutTacheAttente);

            if (taches.size() > 1) {
                log.error("Impossible de determiner le numero d'execution de la tache en attente de suppression");
            } else if (1 == taches.size()) {
                try {
                    TacheActeur tache = taches.get(0);
                    // DemandeActeur demandeSuppression = tache.getDemandeActeur();
                    acteurService.deleteActeur(idActeur);
                    StatutTache statutTacheValidee = statutTacheRepository.findByCode(ConstanteWorkflow.STATUT_TACHE_VALIDEE);
                    tache.setStatutTache(statutTacheValidee);
                    save(tache);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}