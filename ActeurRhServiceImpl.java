package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.ActeurVueDto;
import fr.vdm.referentiel.refadmin.mapper.ActeurMapper;
import fr.vdm.referentiel.refadmin.model.AgentRH;
import fr.vdm.referentiel.refadmin.repository.ActeurRhRepository;
import fr.vdm.referentiel.refadmin.service.ActeurRhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActeurRhServiceImpl implements ActeurRhService {

    @Autowired
    private ActeurRhRepository acteurRhRepository;


    private final ActeurMapper acteurMapper = ActeurMapper.INSTANCE;

    public Page<ActeurVueDto> getActeursRhByFiltre(String nom, String prenom, String login, String codeCellule, Pageable page) {
        Page<AgentRH> agents = this.acteurRhRepository.findByFiltre(nom, prenom, login, codeCellule, page);

        return agents.map(this.acteurMapper::agentRhToActeurVueDto);
    }

    public List<ActeurVueDto> getActeurRhByInput(String input) {
        return this.acteurMapper.acteursRhToActeurVueDtoList(this.acteurRhRepository.findByInput(input));
    }
    public List<AgentRH> getAgentRhByMatricule(String input) {
        return this.acteurRhRepository.findByInput(input);
    }
}