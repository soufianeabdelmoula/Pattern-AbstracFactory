package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.model.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReponseRepository extends JpaRepository<Reponse, Long> {
    List<Reponse> findAllByDemande_IdOrderByIntervModifDesc(Long id);

    @Query("select r from Reponse r where r.question.id = :idQuestion and r.demande.id = :idDemande")
    Reponse select(@Param("idDemande") final Long idDemande, @Param("idQuestion") final Long idQuestion);
}