package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Offre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    Offre findByCodeOffre(String codeOffre);

    @Query("SELECT o FROM Offre o WHERE " +
            "(COALESCE(:libelle, '') = '' OR LOWER(o.libelle) LIKE LOWER(CONCAT('%', :libelle, '%'))) and " +
            "(COALESCE(:description, '') = '' OR LOWER(o.description) LIKE LOWER(CONCAT('%', :description, '%'))) and " +
            "(COALESCE(:responsable, 0) = 0 OR o.responsable = :responsable) and " +
            "(:label IS NULL OR LOWER(o.label) LIKE LOWER(CONCAT('%', :label, '%'))) and " +
            "((:metier IS NULL AND :transverse IS NULL) OR ((o.metier = :metier or :metier is null) AND ( o.transverse = :transverse or :transverse is null)))")
    Page<Offre> findWithConditions(
            @Param("libelle") String libelle,
            @Param("description") String description,
            @Param("responsable") Long responsable,
            @Param("label") String label,
            @Param("metier") Boolean metier,
            @Param("transverse") Boolean transverse,
            Pageable pageable
    );

    @Query("SELECT distinct o FROM Dependance d, Offre o where idOffrePrinc=:idOffre and d.idOffreSecond=o.id")
    List<Offre> findDependances(@Param("idOffre") Long idOffre);

}