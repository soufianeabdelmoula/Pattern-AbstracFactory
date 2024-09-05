package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.AgentDto;
import fr.vdm.referentiel.refadmin.mapper.AgentMapper;
import fr.vdm.referentiel.refadmin.model.Agent;
import fr.vdm.referentiel.refadmin.repository.AgentRepository;
import fr.vdm.referentiel.refadmin.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    AgentRepository agentRepository;

    @Override
    public AgentDto findByIdAgent(long id) {

        return AgentMapper.INSTANCE.agentToAgentDto(this.agentRepository.findByIdAgent(id));
    }

    public List<AgentDto> findAllAgent(){
       List<Agent> listAgents=this.agentRepository.findAll();
       return AgentMapper.INSTANCE.listAgentToAgentDto(listAgents);
    }

    public Agent saveAgent(Agent agent){
        return this.agentRepository.save(agent);
    }

    public void deleteAgent(Agent agent){
      this.agentRepository.delete(agent);
    }

    public AgentDto findByLogin(String login){
        return AgentMapper.INSTANCE.agentToAgentDto(this.agentRepository.findByLogin(login));
    }

    @Override
    public List<AgentDto> getAgentByMatricule(String matricule) {
        return AgentMapper.INSTANCE.listAgentToAgentDto(this.agentRepository.findByMatricule(matricule));
    }
}
