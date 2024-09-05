package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.dto.HistoriqueHabilitActeurDto;
import fr.vdm.referentiel.refadmin.model.HistoriqueDroit;
import fr.vdm.referentiel.refadmin.model.HistoriqueDroitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueDroitRepository extends JpaRepository<HistoriqueDroit, HistoriqueDroitId> {
    List<HistoriqueDroit> findAllByIdActeur(Long idActeur);


    @Query("SELECT NEW fr.vdm.referentiel.refadmin.dto.HistoriqueHabilitActeurDto( " +
            "CASE WHEN o.id IS NOT NULL THEN o.libelle ELSE ho.libelle END, " +
            "MIN(h.debHisto), MAX(h.finHisto) ) " +
            "FROM HistoriqueDroit h " +
            "LEFT JOIN Offre o ON o.id = h.idOffre " +
            "LEFT JOIN HistoriqueOffre ho ON ho.idOffre = h.idOffre " +
            "WHERE h.idActeur = :idActeur AND h.idOffre IS NOT NULL " +
            "GROUP BY CASE WHEN o.id IS NOT NULL THEN o.libelle ELSE ho.libelle END")
    List<HistoriqueHabilitActeurDto> findHistoHabilitActeurByIdActeur(@Param("idActeur") Long idActeur);
}
