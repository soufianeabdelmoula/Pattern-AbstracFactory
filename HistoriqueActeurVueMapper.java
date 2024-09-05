package fr.vdm.referentiel.refadmin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper( uses = {},nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface HistoriqueActeurVueMapper {

    HistoriqueActeurVueMapper INSTANCE = Mappers.getMapper(HistoriqueActeurVueMapper.class);

}
