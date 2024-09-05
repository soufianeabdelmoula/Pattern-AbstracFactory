package fr.vdm.referentiel.refadmin.repository;


import fr.vdm.referentiel.refadmin.model.LienDemGrpApplicatif;
import fr.vdm.referentiel.refadmin.model.LienDemGrpApplicatifId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LienDemGrpApplicatifRepository extends JpaRepository<LienDemGrpApplicatif, LienDemGrpApplicatifId> {

    @Query("SELECT ldmgr FROM LienDemGrpApplicatif ldmgr WHERE ldmgr.idGrp = :idGrp AND ldmgr.idDemande = :idDemande")
    LienDemGrpApplicatif findLienDemGrpApplicatifByIdGrpAndAndIdDemande(@Param("idGrp") Long idGrp, @Param("idDemande") Long idDemande);

    void deleteAllByDemande_Id(Long idDemande);
}