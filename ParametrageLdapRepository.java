package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Offre;
import fr.vdm.referentiel.refadmin.model.ParametrageLdap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParametrageLdapRepository extends JpaRepository<ParametrageLdap,Long> {

    @Query("select p from ParametrageLdap p where p.offre.id = :idOffre")
    List<ParametrageLdap> findByIdOffre(@Param("idOffre") Long idOffre);

    Boolean existsParametrageLdapByDnPrinc(String dnPrinc);

    @Query("select p from ParametrageLdap p where p.dnPrinc = :dnPrinc and p.offre is not null")
    ParametrageLdap findParametrageLdapByDnPrinc(@Param("dnPrinc") String dnPrinc);

}
