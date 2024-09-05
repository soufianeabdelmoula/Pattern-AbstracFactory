package fr.vdm.referentiel.refadmin.mapper;


import fr.vdm.referentiel.refadmin.dto.AgentDto;
import fr.vdm.referentiel.refadmin.model.Agent;
import fr.vdm.referentiel.refadmin.model.AgentRH;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface AgentMapper {

    AgentMapper INSTANCE = Mappers.getMapper(AgentMapper.class);

    @Mappings({@Mapping(source="email.idEmail",target="idEmail")})
    AgentDto agentToAgentDto(Agent agent);

    AgentDto agentRHToAgentDto(AgentRH agentRH);

    List<AgentDto> listAgentToAgentDto(List<Agent> listAgent);




}
