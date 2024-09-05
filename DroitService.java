package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.DemandeDroitDto;
import fr.vdm.referentiel.refadmin.dto.DroitDto;
import fr.vdm.referentiel.refadmin.model.DemandeDroit;
import fr.vdm.referentiel.refadmin.model.Droit;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DroitService {

    DroitDto getDroitById(Long idDroit);

    List<DroitDto> getAllDroitByidOffre(Long idOffre);

    boolean saveDroitImport(DemandeDroit demandeDroit, Long idActeur, Long idOffre);

    /**
     * Cette méthode permet de trouver un objet {@link Droit} dans la base de donnée
     * par son {@param idActeur} et son {@param idOffre}
     *
     * @param idActeur id de l'acteur
     * @param idOffre  id de l'offre
     * @return retourne un objet {@link Droit}
     */
    Droit getDroitByIdActeurAndIdOffre(Long idActeur, Long idOffre);

    /**
     * Cette methode vérifie si l'objet {@link Droit} existe dans la base donnée par les valeurs en paramètres :
     * {@param idActeur} et {@param idOffre}
     *
     * @param idActeur id de l'acteur
     * @param idOffre  id de l'offre
     * @return retourne {@code TRUE} si l'objet {@link Droit} est trouvé sinon {@code FALSE}
     */
    boolean existsDroitByIdActeurAndIdOffre(Long idActeur, Long idOffre);

    boolean isHabilite(long idActeur, long idOffre);

    List<DroitDto> findByIdActeur(final Long idActeur);

    DroitDto selectByIdActeurIdOffre(long idActeur,
                                     long idOffre);

    void delete(long idActeur, Long idOffre,
                Long idDemande);

    void save(DemandeDroitDto demandeDto) throws ServiceException;
}