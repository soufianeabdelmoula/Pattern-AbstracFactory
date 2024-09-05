package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.EtapeDemActeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtapeDemActeurRepository extends JpaRepository<EtapeDemActeur, Long> {
    @Query("select e from EtapeDemActeur e where e.demande.id = :idDemande")
    List<EtapeDemActeur> findEtapesFromIdDemandeActeur(@Param("idDemande") Long idDemande);

    @Query("Select e from EtapeDemActeur e where e.demande.id = (:idDemande) and e.ordre = 0")
    EtapeDemActeur findFirstEtape(@Param("idDemande") Long idDemande);
}