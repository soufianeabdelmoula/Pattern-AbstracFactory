package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Dependance;
import fr.vdm.referentiel.refadmin.model.DependanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependanceRepository extends JpaRepository<Dependance, DependanceId> {


    List<Dependance> findDependancesByIdOffrePrinc(Long idOffrePrinc);

    List<Dependance> findDependancesByIdOffreSecond(Long idOffreSecond);
}