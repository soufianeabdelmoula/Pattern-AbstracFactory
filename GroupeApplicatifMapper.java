package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.GroupeApplicatifDto;
import fr.vdm.referentiel.refadmin.model.GroupeApplicatif;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface GroupeApplicatifMapper {
    GroupeApplicatifMapper INSTANCE = Mappers.getMapper(GroupeApplicatifMapper.class);

    GroupeApplicatifDto groupeApplicatifToDto(GroupeApplicatif groupeApplicatif);
    List<GroupeApplicatifDto> groupeApplicatifToDtoList(List<GroupeApplicatif> groupeApplicatifList);
}
