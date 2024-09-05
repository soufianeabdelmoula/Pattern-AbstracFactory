package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.ParametreWorkflowDroit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametreWorkflowDroitRepository extends JpaRepository<ParametreWorkflowDroit, Long> {
    @Query("SELECT DISTINCT p from ParametreWorkflowDroit p WHERE p.offre.id =:idOffre AND  p.cellule = :cellule")
    ParametreWorkflowDroit findParametreWorkflowDroitByCellule(@Param("idOffre") Long idOffre, @Param("cellule") String cellule);
}
