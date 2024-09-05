package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.EtapeDemDroit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtapeDemDroitRepository extends JpaRepository<EtapeDemDroit, Long> {

    @Query("select e from EtapeDemDroit e where e.demande.id = :idDemande")
    List<EtapeDemDroit> findEtapesFromIdDemande(@Param("idDemande") Long idDemande);

    @Query("select max(e.ordre) from EtapeDemDroit e where e.demande.id = :idDemande")
    int findMaxOrdreEtapeFromIdDemande(@Param("idDemande") Long idDemande);

    @Query("Select e from EtapeDemDroit e where e.demande.id = :idDemande and e.ordre = 0")
    EtapeDemDroit findFirstEtape(@Param("idDemande") Long idDemande);

    @Query("Select e from EtapeDemDroit e where e.demande.id = (:demande) and e.ordre = (select max(e2.ordre) from EtapeDemDroit e2 where e2.demande.id = (:demande))")
    EtapeDemDroit findDerniereEtape(@Param("demande") Long idDemande);

}