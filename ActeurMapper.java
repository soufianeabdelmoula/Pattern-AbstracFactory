package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.ActeurDto;
import fr.vdm.referentiel.refadmin.dto.ActeurVueDto;
import fr.vdm.referentiel.refadmin.dto.CreationCompteDto;
import fr.vdm.referentiel.refadmin.model.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ActeurMapper {

    ActeurMapper INSTANCE = Mappers.getMapper(ActeurMapper.class);

    @Mappings({@Mapping(source="email.idEmail",target="idEmail")})
    @Named("acteurToActeurDto")
    ActeurDto acteurToActeurDto(Acteur acteur);

    @InheritInverseConfiguration
    Acteur acteurDtoToActeur(ActeurDto acteur);

    @Mappings({
            @Mapping(target = "nomMarital", source = "nomUsuel")
    })
    ActeurVueDto agentRhToActeurVueDto(AgentRH agentRH);

    List<ActeurVueDto> acteursRhToActeurVueDtoList(List<AgentRH> agentRHList);


    @Mappings({@Mapping(target = "email.email", source = "email"), @Mapping(target = "idTypeCompteService", source = "idType"), @Mapping(target = "cellule", source = "cellule.code")})
    CompteService creationCompteDtoToCompteService(CreationCompteDto creationCompteDto);

    @Mappings({@Mapping(target = "email.email", source = "email"), @Mapping(target = "idTypeCompteRessource", source = "idType"), @Mapping(target = "cellule", source = "cellule.code")})
    CompteRessource creationCompteDtoToCompteRessource(CreationCompteDto creationCompteDto);

    @Mappings({
            @Mapping(target = "email.email", source = "email"),
            @Mapping(target = "idTypeCompteAppli", source = "idType"),
            @Mapping(target = "code", source = "codeMega")
    })
    CompteApplicatif creationCompteDtoToCompteApplicatif(CreationCompteDto creationCompteDto);

    void update(@MappingTarget CompteService cs, CompteService compte);

    void update(@MappingTarget CompteRessource compteRessource, CompteRessource compteRessource1);

    void update(@MappingTarget CompteApplicatif compteApplicatif, CompteApplicatif compteApplicatif1);
}