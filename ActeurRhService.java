package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.ActeurVueDto;
import fr.vdm.referentiel.refadmin.model.AgentRH;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActeurRhService {

    Page<ActeurVueDto> getActeursRhByFiltre(String nom, String prenom, String login, String codeCellule, Pageable page);

    List<ActeurVueDto> getActeurRhByInput(String input);

    List<AgentRH> getAgentRhByMatricule(String input);
}