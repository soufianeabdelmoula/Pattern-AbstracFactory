package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.dto.HistoriqueImportDto;
import fr.vdm.referentiel.refadmin.model.ImportActeurGroupeApplicatif;
import fr.vdm.referentiel.refadmin.model.TacheHabilitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportActeurGroupeApplicatifRepository extends JpaRepository<ImportActeurGroupeApplicatif, Long> {

    /**
     * Recherche l'historique des imports applicatifs en appliquant des filtres optionnels sur le nom, le prénom,
     * le login, le libellé de l'offre, la date d'exécution et l'intervenant.
     * Si aucun filtre n'est spécifié (c'est-à-dire que tous les paramètres de filtre sont nuls),
     * la méthode retourne {@code null}.
     *
     * @param nom          Filtre sur le nom.
     * @param prenom       Filtre sur le prénom.
     * @param login        Filtre sur le login.
     * @param offre        Filtre sur le libellé de l'offre.
     * @param intervenant  Filtre sur l'intervenant.
     * @param pageable     Informations de pagination.
     * @return Une page d'objets {@link HistoriqueImportDto} contenant les résultats de la recherche.
     *         Retourne {@code NULL} si aucun filtre n'est spécifié.
     */
    @Query("select new fr.vdm.referentiel.refadmin.dto.HistoriqueImportDto( " +
            "coalesce(actV.nom, imp.idActeur), " +
            "coalesce(actV.prenom, imp.idActeur), " +
            "coalesce(actV.login, imp.idActeur), " +
            "coalesce(offr.libelle, imp.traitement.idOffre), " +
            "imp.traitement.dateExe, " +
            "coalesce(imp.traitement.intervenant, '')) " +
            "from ImportActeurGroupeApplicatif imp " +
            "left join ActeurVue actV on actV.idActeur = imp.idActeur " +
            "left join Offre offr on offr.id = imp.traitement.idOffre " +
            "where (upper(actV.nom) like upper(concat(:nom, '%')) and :nom is not null)" +
            "or (upper(actV.prenom) like upper(concat(:prenom, '%')) and :prenom is not null) " +
            "or (upper(actV.login) like upper(concat(:login, '%')) and :login is not null) " +
            "or (upper(offr.libelle) like upper(concat(:offre, '%')) and :offre is not null) " +
            "or (upper(imp.traitement.intervenant) like upper(concat(:intervenant, '%')) and :intervenant is not null) " +
            "order by imp.traitement.dateExe desc")
    Page<HistoriqueImportDto> findAllImportAppByFilter(@Param("nom") String nom,
                                                       @Param("prenom") String prenom,
                                                       @Param("login") String login,
                                                       @Param("offre") String offre,
                                                       @Param("intervenant") String intervenant,
                                                       Pageable pageable);

    /**
     * Recherche l'historique de tous les imports applicatifs.
     *
     * @param pageable    Les paramètres de pagination pour la liste des résultats.
     * @return Une page paginée d'objets HistoriqueImportDto représentant l'historique des imports applicatifs.
     */
    @Query("select new fr.vdm.referentiel.refadmin.dto.HistoriqueImportDto( " +
            "coalesce(actV.nom, ''), " +
            "coalesce(actV.prenom, ''), " +
            "coalesce(actV.login, ''), " +
            "coalesce(offr.libelle, ''), " +
            "imp.traitement.dateExe, " +
            "coalesce(imp.traitement.intervenant, '')) " +
            "from ImportActeurGroupeApplicatif imp " +
            "left join ActeurVue actV on actV.idActeur = imp.idActeur " +
            "left join Offre offr on offr.id = imp.traitement.idOffre " +
            "order by imp.traitement.dateExe desc")
    Page<HistoriqueImportDto> findAllImportApp(Pageable pageable);

    /**
     * Recherche l'historique de tous les imports applicatifs.
     *
     * @return Une liste d'objets HistoriqueImportDto représentant l'historique des imports applicatifs.
     */
    @Query("select new fr.vdm.referentiel.refadmin.dto.HistoriqueImportDto( " +
            "coalesce(actV.nom, ''), " +
            "coalesce(actV.prenom, ''), " +
            "coalesce(actV.login, ''), " +
            "coalesce(offr.libelle, ''), " +
            "imp.traitement.dateExe, " +
            "coalesce(imp.traitement.intervenant, '')) " +
            "from ImportActeurGroupeApplicatif imp " +
            "left join ActeurVue actV on actV.idActeur = imp.idActeur " +
            "left join Offre offr on offr.id = imp.traitement.idOffre " +
            "order by imp.traitement.dateExe desc")
    List<HistoriqueImportDto> findAllImportApp();
}