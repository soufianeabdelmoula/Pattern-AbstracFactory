package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.ReponseDto;
import fr.vdm.referentiel.refadmin.model.Reponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReponseMapper {

    ReponseMapper INSTANCE = Mappers.getMapper(ReponseMapper.class);
    @Mapping(source = "demande.id", target = "idDemande")
    @Mapping(source = "question.id", target = "idQuestion")
    ReponseDto reponseToReponseDto(Reponse reponse);

    @Mappings({})
    List<ReponseDto> reponsesToReponsesDtos(List<Reponse> reponses);

}