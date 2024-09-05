package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.GroupeApplicatif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupeApplicatifRepository extends JpaRepository<GroupeApplicatif, Long> {
    List<GroupeApplicatif> findByParametrageLdap_Offre_IdOrderByDnAsc(Long idOffre);

    List<GroupeApplicatif> findByParametrageLdap_Offre_CodeOffreOrderByDnAsc(String codeOffre);

    @Query("SELECT g FROM GroupeApplicatif g WHERE LOWER(g.dn) LIKE LOWER('%', :commonName, '%')")
    List<GroupeApplicatif> findByCnAndDescription(@Param("commonName") String commonName);

    GroupeApplicatif findGroupeApplicatifByDn(String dn);
    Boolean existsGroupeApplicatifByDn(String dn);


    @Query("SELECT g FROM GroupeApplicatif g WHERE LOWER(g.dn) = :commonName")
    GroupeApplicatif findByDn(@Param("commonName") String commonName);


}