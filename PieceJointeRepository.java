package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.PieceJointe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PieceJointeRepository extends JpaRepository<PieceJointe,Long> {

    List<PieceJointe> findAllByIdDemandeAndTypeDemande(Long idDemande, String typeDemande);
}
