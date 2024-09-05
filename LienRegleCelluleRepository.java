package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.LienRegleCellule;
import fr.vdm.referentiel.refadmin.model.LienRegleCelluleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LienRegleCelluleRepository extends JpaRepository<LienRegleCellule, LienRegleCelluleId> {

    @Query("select l from LienRegleCellule l where trim(l.cellule) in :listeCodeCellule")
    List<LienRegleCellule> findByCodeCellule(@Param("listeCodeCellule") List<String> listeCodeCellule);
}
