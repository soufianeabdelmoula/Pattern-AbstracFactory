package fr.vdm.referentiel.refadmin.mapper;


import fr.vdm.referentiel.refadmin.alfresco.dto.PieceJointeDto;
import fr.vdm.referentiel.refadmin.model.PieceJointe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper( uses = {})
public interface PieceJointeMapper {

    PieceJointeMapper INSTANCE = Mappers.getMapper(PieceJointeMapper.class);
    @Mappings({@Mapping(source = "typePj", target = "typePj")})
    PieceJointe PieceJointeDtoToPieceJointe(PieceJointeDto paramPieceJointeDto);
}

