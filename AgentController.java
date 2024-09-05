package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.AgentDto;
import fr.vdm.referentiel.refadmin.service.AgentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agent")
@Log4j2
public class AgentController {

    final
    AgentService agentService;



    /**
     * Retourne l'agent d'ID passé dans le path
     * @param id Id de l'agent
     * @return AgentDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgentDto> getAgentById(@PathVariable long id){
        log.info("get agent by id "+ id);
        return new ResponseEntity<>(this.agentService.findByIdAgent(id), HttpStatus.OK);
    }

    /**
     * Retourne l'ensemble des Agents des tables RFATACTEUR/RFATAGNT
     * @return List<AgentDto>
     */
    @GetMapping("/agents")
    public ResponseEntity<List<AgentDto>> getAllAgent(){
        log.info("get agents");
        return new ResponseEntity<>(this.agentService.findAllAgent(),HttpStatus.OK);
    }


    @GetMapping("/agent-by-login")
    public ResponseEntity<AgentDto> geAgentDtoByLogin(@RequestParam String login){
        log.info("get agents by login : " + login);
        return new ResponseEntity<>(this.agentService.findByLogin(login), HttpStatus.OK);
    }


    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }


}
