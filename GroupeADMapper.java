package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.GroupeADDto;
import fr.vdm.referentiel.refadmin.model.ad.GroupeAD;
import fr.vdm.referentiel.refadmin.model.ad.GroupeTechniqueAD;
import fr.vdm.referentiel.refadmin.model.ad.GroupeTechniqueInterneAD;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GroupeADMapper {
    GroupeADMapper INSTANCE = Mappers.getMapper(GroupeADMapper.class);

    @Mappings({})
    GroupeADDto groupeADToGroupeADDto(GroupeAD groupeAD);

    List<GroupeADDto> groupesADToGroupesADDto(List<GroupeAD> groupesAd);

    GroupeADDto groupeTechniqueADToGroupeADDto(GroupeTechniqueAD byCn);

    GroupeADDto groupeTechniqueInterneADToGroupeADDto(GroupeTechniqueInterneAD byCn);
}