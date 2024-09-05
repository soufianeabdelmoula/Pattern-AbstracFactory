package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.ResponsableGrpFoncDto;
import fr.vdm.referentiel.refadmin.model.ResponsableGrpFonc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResponsableGrpFoncMapper {
    ResponsableGrpFoncMapper INSTANCE = Mappers.getMapper(ResponsableGrpFoncMapper.class);
    @Mapping(source="grpFonc.libLdapGrFonc", target="nom")
    @Mapping(source = "grpFonc.libLongGrFonc", target = "description")
    @Mapping(source = "grpFonc.typeParametrage", target = "typeParametrage")
    @Mapping(source="idGrFc", target="idGrF")
    ResponsableGrpFoncDto responsableGrpFoncToDto(ResponsableGrpFonc responsableGrpFonc);
    List<ResponsableGrpFoncDto> responsalbeGrpFoncToDtoList(List<ResponsableGrpFonc> responsableGrpFoncList);
}