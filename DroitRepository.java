package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Droit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroitRepository extends JpaRepository<Droit,Long> {
    List<Droit> findByActeurBenef_IdActeur(Long idActeur);

    @Query("SELECT d.id from Droit d WHERE d.acteurBenef.idActeur = :idActeur order by d.tsCreat ASC")
    List<Long> findIdDroitByIdActeur(@Param("idActeur") Long paramLong);

    List<Droit> findAllByOffre_Id(Long id);

    List<Droit> findAllByActeurBenef_IdActeur(Long idActeur);

    Boolean existsDroitByActeurBenef_IdActeurAndOffre_Id(Long idActeur, Long idOffre);
    @Query("SELECT d from Droit d where d.acteurBenef.idActeur = :idActeur " +
            "and d.offre.id =:idOffre")
    Droit findDroitByIdActeurAndIdOffre(@Param("idActeur") Long idActeur, @Param("idOffre") Long idOffre);

    @Query("SELECT count(d) from Droit d WHERE d.acteurBenef.idActeur = :idActeur and d.offre.id= :idOffre")
    long countByOffreAndActeur(@Param("idActeur") long idActeur, @Param("idOffre") long idOffre);

    Droit findDroitByActeurBenef_IdActeurAndOffre_Id(Long idActeur, Long idOffre);


}