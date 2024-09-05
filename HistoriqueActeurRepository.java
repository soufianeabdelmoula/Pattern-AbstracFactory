package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.dto.HistoriqueAffectActeurDto;
import fr.vdm.referentiel.refadmin.model.HistoriqueActeur;
import fr.vdm.referentiel.refadmin.model.HistoriqueActeurid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoriqueActeurRepository extends JpaRepository<HistoriqueActeur, HistoriqueActeurid> {

    List<HistoriqueActeur> findAllByIdActeur(Long idActeur);


    /**
     * Recherche l'historique des affectations officielles d'un acteur par son identifiant.
     * Et on recherche le "libLdapGrCel" dans la table "Cellule", sinon dans la table "HistoriqueCellule"
     *
     * @param idActeur L'identifiant de l'acteur pour lequel rechercher l'historique des affectations officielles.
     * @return Une liste HistoriqueAffectationDto représentant l'historique des affectations officielles.
     */
    @Query("SELECT NEW fr.vdm.referentiel.refadmin.dto.HistoriqueAffectActeurDto(" +
            "h.cellule, MIN(h.debHisto), MAX(h.finHisto), FALSE, h.coll, h.ssColl, " +
            "CASE WHEN c.code IS NOT NULL THEN c.libLdapGrCel ELSE hc.libldapgrcel END) " +
            "FROM HistoriqueActeur h " +
            "LEFT JOIN Cellule c ON h.cellule = c.code AND h.coll = c.coll " +
            "LEFT JOIN HistoriqueCellule hc ON hc.code = h.cellule AND hc.coll = h.coll " +
            "WHERE h.idActeur = :idActeur AND h.cellule IS NOT NULL " +
            "GROUP BY h.cellule, h.coll, h.ssColl, " +
            "CASE WHEN c.code IS NOT NULL THEN c.libLdapGrCel ELSE hc.libldapgrcel END")
    List<HistoriqueAffectActeurDto> findHisAffectationOffiByIdActeur(@Param("idActeur") Long idActeur);

    /**
     * Recherche l'historique des affectations terrain d'un acteur par son identifiant.
     * Et on recherche le "libLdapGrCel" dans la table "Cellule", sinon dans la table "HistoriqueCellule"
     *
     * @param idActeur L'identifiant de l'acteur pour lequel rechercher l'historique des affectations officielles.
     * @return Une liste HistoriqueAffectationDto représentant l'historique des affectations terrain.
     */
    @Query("SELECT NEW fr.vdm.referentiel.refadmin.dto.HistoriqueAffectActeurDto(" +
            "h.celluleTerrain, MIN(h.debHisto), MAX(h.finHisto), TRUE, h.collTerrain, h.ssCollTerrain, " +
            "CASE WHEN c.code IS NOT NULL THEN c.libLdapGrCel ELSE hc.libldapgrcel END) " +
            "FROM HistoriqueActeur h " +
            "LEFT JOIN Cellule c ON h.celluleTerrain = c.code AND h.collTerrain = c.coll " +
            "LEFT JOIN HistoriqueCellule hc ON hc.code = h.celluleTerrain AND hc.coll = h.collTerrain " +
            "WHERE h.idActeur = :idActeur AND h.celluleTerrain IS NOT NULL " +
            "GROUP BY h.celluleTerrain, h.collTerrain, h.ssCollTerrain, " +
            "CASE WHEN c.code IS NOT NULL THEN c.libLdapGrCel ELSE hc.libldapgrcel END")
    List<HistoriqueAffectActeurDto> findHisAffectationTerrainByIdActeur(@Param("idActeur") Long idActeur);


}
