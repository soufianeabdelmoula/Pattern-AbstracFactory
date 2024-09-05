
package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.GroupeFonctionnelDto;
import fr.vdm.referentiel.refadmin.model.GroupeFonctionnel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel="spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface GroupeFonctionnelMapper {
    GroupeFonctionnelMapper INSTANCE = Mappers.getMapper(GroupeFonctionnelMapper.class);

    @Mapping(source = "libLdapGrFonc", target = "nom")
    @Mapping(source = "libLongGrFonc", target = "description")
    @Mapping(source="id", target="idGrpFonc")
    GroupeFonctionnelDto groupeFonctionnelToDto(GroupeFonctionnel groupeFonctionnel);
    List<GroupeFonctionnelDto> groupeFonctionnelToDtoList(List<GroupeFonctionnel> groupeFonctionnelleList);

}