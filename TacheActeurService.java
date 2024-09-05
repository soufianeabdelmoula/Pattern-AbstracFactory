package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.TacheActeurDto;
import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import fr.vdm.referentiel.refadmin.model.DemandeDroit;
import fr.vdm.referentiel.refadmin.model.StatutTache;
import fr.vdm.referentiel.refadmin.model.TacheActeur;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface TacheActeurService {
    Page<TacheActeurDto> getTachesActeurs(Pageable pageable);

    List<TacheActeurDto> getByStatut(String statutCode);

    Page<TacheActeurDto> getAllTaskByInputAndIdStatutForAll(List<String> statutCode, String loginAssigne, String input, Pageable page) throws ServiceException;
    Page<TacheActeurDto> getAllTaskByInputAndIdStatutAndForMyTask(List<String> statutCode, String loginAssigne, String input, Pageable page);



    List<TacheActeur> getByAssigneId(Long assigneId);

    TacheActeurDto getById(Long idTacheActeur);
    TacheActeur findById(Long idTacheActeur);
    Serializable createTache(DemandeActeur demande, String activite, StatutTache statutTache);

    TacheActeur save(TacheActeur tacheActeur);

    List<TacheActeur> findByDemandeActeurId(Long idDemandeActeur);

    Long getLastTache(long idDemande);
    List<TacheActeur> findByActeurBeneficiaire(long idActeur, StatutTache statut);

    /**
     * Permet me réserver une tâche acteur à l'utilisateur connecté
     * @param loginAssigne le login de l'utilisateur connecté
     * @param idTache l'id de la tache sélectionnée
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @throws ServiceException retourner une exception
     */
    void assignerTacheActeur(String loginAssigne, Long idTache, List<String> statutCode) throws ServiceException;

    /**
     * Permet me libérer une tâche acteur de l'utilisateur connecté
     * @param loginAssigne le login de l'utilisateur connecté
     * @param idTache l'id de la tache sélectionnée
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @throws ServiceException retourner une exception
     */
    void libererTacheActeur(String loginAssigne, Long idTache, List<String> statutCode) throws ServiceException;

    /**
     * Permet d'assigner toutes les tâches acteur sélectionnées à l'utilisateur connecté
     * @param loginAssigne le login de l'utilisateur connecté
     * @param idTache liste des "id" des tâches sélectionnées
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @throws ServiceException retourner une exception
     */
    void assignerToutesLesTacheActeur(String loginAssigne, List<Long> idTache, List<String> statutCode) throws ServiceException;

    /**
     * Permet de compter le nombre de tâches acteur réservé par l'utilisateur connecté
     * @param loginAssigne le login de l'utilisateur connecté
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @return le nombre de tâches acteur réservé par l'utilisateur connecté
     */
    Long countTaskActeur(String loginAssigne, List<String> statutCode);

    /**
     * Permet me libérer toutes les tâches acteur sélectionnées par un ADMIN
     * @param loginAdmin le login de l'utilisateur connecté
     * @param idTache liste des "id" des tâches sélectionnées
     * @param statutCode une liste d'états (techniques ou hiérarchiques)
     * @throws ServiceException retourner une exception
     */
    void libererToutesLesTacheActeurParUnAdmin(String loginAdmin, List<Long> idTache, List<String> statutCode) throws ServiceException;

    void traitementTacheEnAttente(DemandeDroit demandeDroit);
    byte[] getExportTacheActeur(List<String> statutCode);

}
