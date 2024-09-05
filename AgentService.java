package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.ActeurVueDto;
import fr.vdm.referentiel.refadmin.dto.AgentDto;
import fr.vdm.referentiel.refadmin.model.Agent;

import java.util.List;

public interface AgentService {

    List<AgentDto> findAllAgent();

    AgentDto findByIdAgent(long id);

    Agent saveAgent(Agent agent);

    void deleteAgent(Agent agent);

    AgentDto findByLogin(String login);
    List<AgentDto> getAgentByMatricule(String matricule);

}
