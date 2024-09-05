package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.HistoriqueCellule;
import fr.vdm.referentiel.refadmin.model.HistoriqueCelluleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueCelluleRepository extends JpaRepository<HistoriqueCellule, HistoriqueCelluleId> {

    @Query(value = "select c from HistoriqueCellule c " +
            "where trim(c.code) = trim(:code)")
    List<HistoriqueCellule> findHistoriqueCelluleByCode(@Param("code")String code);

}
