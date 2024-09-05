package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.TacheHabilitationDto;
import fr.vdm.referentiel.refadmin.dto.UtilisateurDto;
import fr.vdm.referentiel.refadmin.model.DemandeDroit;
import fr.vdm.referentiel.refadmin.model.Offre;
import fr.vdm.referentiel.refadmin.model.StatutTache;
import fr.vdm.referentiel.refadmin.model.TacheHabilitation;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TacheHabilitationService {

    TacheHabilitation findById(long idTache);
    List<TacheHabilitationDto> getTachesHabilitations(Pageable pageable);

    List<TacheHabilitationDto> getByAssigneId(Long assigneId);

    Page<TacheHabilitationDto> getByInputAndStatut(List<String> statutCode, String loginAssigne, String input, Pageable page) throws ServiceException;
    Page<TacheHabilitationDto> getByInputAndStatutForMyTask(List<String> statutCode, String loginAssigne, String input, Pageable page);


    void validerTachesHabilitations(List<Long> ids);

    void refuserTachesHabilitations(List<Long> ids);
    List<TacheHabilitation> findTacheHabilitationByActiviteAndOffreAndStatut(String activite, Long idOffre, String statutTache);
    List<TacheHabilitation> findTachesByAffectationAndNiveau(String activite, Long niveau, List<String> affectations, Long idOffre);
    List<TacheHabilitation> findTachesByAffectation(String activite, List<String> affectations, final Long idOffre);
    List<TacheHabilitation> findByActeurAndOffre(long idActeur, long idOffre);


    Long createTache(DemandeDroit demande, Offre offrePrincipale, Offre offreDependance, String activite, StatutTache statutTache, long niveau);

    void save(TacheHabilitation tache);
    List<TacheHabilitation> findByIdOffreAndBeneficiaireAndStatut(long idOffrePrincipale, long idActeurBeneficiaire, String statutCode);

    Long getLastTache(long idDemande);


    /**
     * Permet de réserver une tâche habilitation à l'utilisateur connecté
     * @param loginAssigne le login de l'utilisateur connecté
     * @param idTache l'id de la tache sélectionnée
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @throws ServiceException retourner une exception
     */
    void assignerTacheHabilitation(String loginAssigne, Long idTache, List<String> statutCode) throws ServiceException;

    /**
     * Permet de libérer une tâche habilitation
     * de l'utilisateur connecté
     * @param loginAssigne le login de l'utilisateur connecté
     * @param idTache l'id de la tache sélectionnée
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @throws ServiceException retourner une exception
     */
    void libererTacheHabilitation(String loginAssigne, Long idTache, List<String> statutCode) throws ServiceException;

    /**
     * Permet d'assigner toutes les tâches habilitation sélectionnées
     * à l'utilisateur connecté
     * @param loginAssigne le login de l'utilisateur connecté
     * @param idTache liste des "id" des tâches sélectionnées
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @throws ServiceException retourner une exception
     */
    void assignerToutesLesTacheHabilitation(String loginAssigne, List<Long> idTache, List<String> statutCode) throws ServiceException;

    /**
     * Permet de compter le nombre de tâches habilitation réservé par l'utilisateur connecté
     * @param loginAssigne le login de l'utilisateur connecté
     * @param statut une liste d'états (techniques ou hiérarchiques)
     * @return le nombre de tâches habilitation réservé par l'utilisateur connecté
     */
    Long countMyTaskhabilitations(String loginAssigne, List<String> statut);

    /**
     * Permet me libérer toutes les tâches habilitation sélectionnées par un ADMIN
     *
     * @param loginAdmin le login de l'utilisateur connecté
     * @param idTache    liste des "id" des tâches sélectionnées
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @throws ServiceException retourner une exception
     */
    void libererToutesLesTacheHabilitationParUnAdmin(String loginAdmin, List<Long> idTache, List<String> statutCode) throws ServiceException;

    List<TacheHabilitation> findByIdDemande(Long idDemande);

    byte[] getExportTacheHabilitation(List<String> statutCode);

    Page<TacheHabilitationDto> findTachesDispo(List<String> statut, String loginAssigne, String input, Pageable pageable) throws ServiceException;
}
