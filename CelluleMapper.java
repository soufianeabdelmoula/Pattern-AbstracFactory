package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.model.Cellule;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface CelluleMapper {

    CelluleMapper INSTANCE = Mappers.getMapper(CelluleMapper.class);

    @Mappings({
            @Mapping(target = "nom", source = "libLdapGrCel"),
            @Mapping(target = "code", expression = "java(cellule.getCode().trim())"),
            @Mapping(target = "adresse", source = ".", qualifiedByName = "mapAdresseCellule"),
            @Mapping(target = "libelleLDAP", source = "libLdapGrCel"),
            @Mapping(target = "codePere", source = "codeCellulePere")
    })
    CelluleDto celluleToCelluleDto(Cellule cellule);

    List<CelluleDto> celluleToCelluleDto(List<Cellule> cellule);

    @Named("mapAdresseCellule")
    default String getFullAdresse(Cellule cellule) {
        String adresse = "";
        adresse += cellule.getAdressPrincipale() != null ? cellule.getAdressPrincipale() + " " : "";
        adresse += cellule.getAdresseSecondaire() != null ? cellule.getAdresseSecondaire() + " " : "";
        adresse += cellule.getCodePostal() != null ? cellule.getCodePostal() + " " : "";
        adresse += cellule.getCommune() != null ? cellule.getCommune() : "";

        return adresse;
    }
}