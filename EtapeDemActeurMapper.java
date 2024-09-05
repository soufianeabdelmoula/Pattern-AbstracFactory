package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.EtapeDemActeurDto;
import fr.vdm.referentiel.refadmin.model.EtapeDemActeur;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EtapeDemActeurMapper {

    EtapeDemActeurMapper INSTANCE = Mappers.getMapper(EtapeDemActeurMapper.class);

    EtapeDemActeurDto etapeDemActeurToDto(EtapeDemActeur etapeDemActeur);

    List<EtapeDemActeurDto> tapeDemActeurToDtoList(List<EtapeDemActeur> etapeDemActeurList);


}
