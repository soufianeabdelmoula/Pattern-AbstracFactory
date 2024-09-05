package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.dto.ActeurHabiliteDto;
import fr.vdm.referentiel.refadmin.dto.AgentDto;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActeurVueRepository extends JpaRepository<ActeurVue,Long> {
    ActeurVue findByIdtaAndIdtn(String idta, String idtn);

    Page<ActeurVue> findAllBy(Pageable pageable);


    @Query(value = "select av from ActeurVue av where upper(av.login) like upper(concat('%', :input, '%')) " +
            "or upper(av.nom) like upper(concat('%', :input, '%')) " +
            "or upper(av.nomMarital) like upper(concat('%', :input, '%')) " +
            "or upper(av.nomUsuel) like upper(concat('%', :input, '%')) " +
            "or upper(av.prenom) like upper(concat('%', :input, '%')) " +
            "or upper(av.prenomUsuel) like upper(concat('%', :input, '%')) "
    )
    Page<ActeurVue> findAllByInput(@Param("input") String input,
                                   Pageable pageable);
    @Query("select av from ActeurVue av where av.login = :login")
    ActeurVue findActeurVueByLogin(@Param("login") String login);

    @Query("SELECT new fr.vdm.referentiel.refadmin.dto.ActeurHabiliteDto(acteurVue.nom, acteurVue.prenom, acteurVue.idActeur, acteurVue.typeActeur, groupe.dn, acteurVue.prenomUsuel, acteurVue.nomUsuel, acteurVue.idta, acteurVue.idtn, acteurVue.login, acteurVue.fonction, acteurVue.cellule, acteurVue.celluleTerrain,  droitHistorique.finHisto)" +
            " FROM ActeurVue acteurVue" +
            " LEFT JOIN Droit droit ON acteurVue.idActeur = droit.acteurBenef.idActeur" +
            " LEFT JOIN Offre offre ON droit.offre.id = offre.id" +
            " LEFT JOIN DemandeDroit demande ON droit.demande.id = demande.id" +
            " LEFT JOIN LienDemGrpApplicatif demandeGroupe ON demande.id = demandeGroupe.idDemande" +
            " LEFT JOIN GroupeApplicatif groupe ON demandeGroupe.groupeApplicatif.id = groupe.id" +
            " LEFT JOIN HistoriqueDroit droitHistorique ON acteurVue.idActeur = droitHistorique.idActeur" +
            " WHERE offre.id = :idOffre")
    List<ActeurHabiliteDto> findByIdOffre(@Param("idOffre") Long idOffre);

    @Query("SELECT new fr.vdm.referentiel.refadmin.dto.ActeurHabiliteDto(acteurVue.nom, acteurVue.prenom, acteurVue.idActeur, acteurVue.typeActeur, groupe.dn, acteurVue.prenomUsuel, acteurVue.nomUsuel, acteurVue.idta, acteurVue.idtn, acteurVue.login, acteurVue.fonction, acteurVue.cellule, acteurVue.celluleTerrain, droitHistorique.finHisto)" +
            " FROM ActeurVue acteurVue" +
            " LEFT JOIN HistoriqueDroit droitHistorique ON acteurVue.idActeur = droitHistorique.idActeur" +
            " LEFT JOIN Offre offre ON droitHistorique.idOffre = offre.id" +
            " LEFT JOIN DemandeDroit demande ON droitHistorique.idDemande = demande.id" +
            " LEFT JOIN LienDemGrpApplicatif demandeGroupe ON droitHistorique.idDemande = demandeGroupe.idDemande" +
            " LEFT JOIN GroupeApplicatif groupe ON demandeGroupe.groupeApplicatif.id = groupe.id" +
            " WHERE offre.id = :idOffre")

    List<ActeurHabiliteDto> findHistoriqueHabilitationByIdOffre(@Param("idOffre") Long idOffre);
    Page<ActeurVue> findAll(@Param("specification") Specification<ActeurVue> specification, Pageable pageable);

    @Query("select act  FROM ActeurVue act where act.typeActeur = 'A' AND act.idta = :idta AND act.idtn = :idtn")
    AgentDto findByMatricule(@Param("idta") String idta, @Param("idtn") String idtn);

    @Query(value = "select * from RFAVACTEUR a " +
            "left join RFATCOMPTEAPP app on a.IDACTEUR = app.IDAPPLI " +
            "where a.typeActeur = :typeActeur " +
            "and (upper(a.nom) like upper('%' || :nom || '%') or :nom is null or :nom = '') " +
            "and (upper(a.login) like upper('%' || :login || '%') or :login is null or :login = '') " +
            "and (a.cellule in (select c.CELLULE from RFSTGRCEL c START WITH c.COLL = 'MAR' AND trim(c.CELLULE) = :codeCellule connect by c.CELPERE = prior c.CELLULE ) or :codeCellule is null or :codeCellule = '') " +
            "and (a.typeCompte = :idType or :idType is null or :idType = -1) " +
            "and (app.IDDEMANDEUR = :idDemandeur or :idDemandeur is null or :idDemandeur = -1) " +
            "and (app.CODE = :codeMega or :codeMega is null or :codeMega = '') " +
            "and (exists (select * from RFATDROIT d where d.IDOFFRE = :idOffre and d.IDACTEURBEN = a.IDACTEUR) or :idOffre is null or :idOffre = -1)", nativeQuery = true)
    Page<ActeurVue> findByFilter(@Param("typeActeur") String typeActeur, @Param("nom") String nom, @Param("codeCellule") String codeCellule, @Param("idType") Long idType, @Param("idOffre") Long idOffre, @Param("login") String login, @Param("codeMega") String codeMega, @Param("idDemandeur") Long idDemandeur, Pageable pageable);

    ActeurVue findActeurVueByIdEmail(Long idEamil);

    Boolean existsActeurVueByLogin(String login);
    List<ActeurVue> findAllByTypeActeur(String typeActeur);



}