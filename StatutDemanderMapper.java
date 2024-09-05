package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.StatutDemandeDto;
import fr.vdm.referentiel.refadmin.model.StatutDemande;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper( nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatutDemanderMapper {

    StatutDemanderMapper INSTANCE = Mappers.getMapper(StatutDemanderMapper.class);
    @Mappings({})
    StatutDemandeDto statutDemandeToStatutDemandeDto(StatutDemande statutDemande);
}
