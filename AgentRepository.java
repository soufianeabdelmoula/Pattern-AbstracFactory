package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Agent;
import fr.vdm.referentiel.refadmin.model.AgentRH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Agent findByIdAgent(long idagent);

    Agent findByLogin(String login);

    @Query(value = "select a from Agent a where concat(a.idta, a.idtn) like concat('%',:matricule, '%') ")
    List<Agent> findByMatricule(@Param("matricule") String matricule);
}