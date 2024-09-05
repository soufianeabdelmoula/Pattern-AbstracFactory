package fr.vdm.referentiel.refadmin.mapper;


import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.model.HistoriqueCellule;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HistoriqueCelluleMapper {

    HistoriqueCelluleMapper INSTANCE = Mappers.getMapper(HistoriqueCelluleMapper.class);

    @Mappings({
            @Mapping(target = "nom", source = "libldapgrcel"),
            @Mapping(target = "code", expression = "java(historiqueCellule.getCode().trim())"),
            @Mapping(target = "adresse", source = ".", qualifiedByName = "mapAdresseCellule"),
            @Mapping(target = "libelleLDAP", source = "libldapgrcel")
    })
    CelluleDto historiqueCelluleToCelluleDto(HistoriqueCellule historiqueCellule);

    List<CelluleDto> historiqueCellulesToCellulesDto(List<HistoriqueCellule> historiqueCellules);

    @Named("mapAdresseCellule")
    default String getFullAdresse(HistoriqueCellule historiqueCellule) {
        String adresse = "";
        adresse += historiqueCellule.getAdrprinc() != null ? historiqueCellule.getAdrprinc() + " " : "";
        adresse += historiqueCellule.getAdrsec() != null ? historiqueCellule.getAdrsec() + " " : "";
        adresse += historiqueCellule.getCodpost() != null ? historiqueCellule.getCodpost() + " " : "";
        adresse += historiqueCellule.getCommune() != null ? historiqueCellule.getCommune() : "";

        return adresse;
    }
}
