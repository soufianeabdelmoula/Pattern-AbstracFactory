package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import fr.vdm.referentiel.refadmin.model.EtapeDemActeur;
import fr.vdm.referentiel.refadmin.model.StatutDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeActeurRepository extends JpaRepository<DemandeActeur, Long> {

    Page<DemandeActeur> findAll(@Param("specification") Specification<DemandeActeur> spec, Pageable pageable);

    @Query("select dAc.statut from DemandeActeur dAc where dAc.id =:id")
    StatutDemande findStautDemandeByIdDemandeActeur(@Param("id") Long id);

    @Query("Select e from EtapeDemActeur e where e.demande.id = (:demandeId)" +
            " and e.ordre = (select max(e2.ordre) from EtapeDemActeur e2 where e2.demande.id = (:demandeId))")
    public EtapeDemActeur selectDerniereEtape(@Param("demandeId") Long id);

    List<DemandeActeur> findAllByActeurBenef_IdActeur(Long idActeur);
}
