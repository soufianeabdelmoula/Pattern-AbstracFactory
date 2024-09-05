package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.DemandeDroit;
import fr.vdm.referentiel.refadmin.model.Droit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeDroitRepository extends JpaRepository<DemandeDroit, Long> {

    DemandeDroit findDemandeDroitById(Long id);

    @Query("SELECT distinct dmd FROM DemandeDroit dmd WHERE dmd.acteurBenef.idActeur = :idActeur AND dmd.offre.id = :idOffre AND dmd.topDem = :typeDem AND dmd.statut.code <> 'ACCEPTEE' AND dmd.statut.code <> 'REFUSEE' ")
    List<DemandeDroit> findDemandeActeurByBeneficiaireAndTopDem(@Param("idActeur") Long idActeur, @Param("idOffre") Long idOffre, @Param("typeDem") String typeDem);

    @Query("SELECT distinct dmd FROM DemandeDroit dmd WHERE dmd.acteurBenef.idActeur = :idActeur AND dmd.offre.id = :idOffre AND dmd.statut.code <> 'ACCEPTEE' AND dmd.statut.code <> 'REFUSEE' ")
    List<DemandeDroit> findDemandeActeurByBeneficiaire(@Param("idActeur") Long idActeur, @Param("idOffre") Long idOffre);

    @Query("SELECT distinct dmd from DemandeDroit dmd WHERE dmd.acteurBenef.idActeur = :idActeur OR dmd.acteurBenef.login = :login")
    Page<DemandeDroit[]> findDemandeDroitByidActeurBenefeciaireOrLogin(Pageable paramPageable, @Param("idActeur") Long idActeur, @Param("login") String login);

    Page<DemandeDroit> findAll(@Param("specification") Specification<DemandeDroit> specification, Pageable pageable);

    @Query("SELECT distinct dmd from DemandeDroit dmd WHERE dmd.acteurBenef.idActeur = :idActeur AND " +
            "dmd.offre.id = :idOffre")
    List<DemandeDroit> findDemandeDroitByActeurBenef_IdActeurAndOffre_Id(@Param("idActeur") Long idActeur, @Param("idOffre") Long idOffre);

    @Query("SELECT d from DemandeDroit d " +
            "WHERE d.acteurBenef.idActeur = :idActeur " +
            "and d.offre.id =:idOffre " +
            "and d.statut.code in :statutCode order by d.tsCreat desc")
    List<DemandeDroit> findAllByIdActeurAndIdOffreAndStatus(@Param("idActeur") Long idActeur, @Param("idOffre") Long idOffre, @Param("statutCode") List<String> statutCode);

    @Query("select dmd from DemandeDroit dmd where dmd.acteurBenef.idActeur = :idActeur")
    List<DemandeDroit> findByIdActeur(@Param("idActeur") Long idActeur);

}