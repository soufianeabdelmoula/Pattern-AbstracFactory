package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.DemandeDroitDto;
import fr.vdm.referentiel.refadmin.dto.ExportDemandeDroitDto;
import fr.vdm.referentiel.refadmin.dto.HistoriqueDemandeDroitDto;
import fr.vdm.referentiel.refadmin.model.DemandeDroit;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DemandeDroitMapper {

    DemandeDroitMapper INSTANCE = Mappers.getMapper(DemandeDroitMapper.class);

    DemandeDroitDto demandeDroitToDto(DemandeDroit demandeDroit);

    List<DemandeDroitDto> demandeDroitToDtoList(List<DemandeDroit> demandeDroits);

    @Mapping(source = "id", target = "idDemande")
    @Mapping(source = "offre.id", target = "idOffrePrincipal")
    @Mapping(source = "offre.libelle", target = "nomOffrePrincipal")
    @Mapping(source = "acteurBenef.login", target = "acteurBenefLogin")
    @Mapping(source = "acteurBenef.nom", target = "nom")
    @Mapping(source = "acteurBenef.prenom", target = "prenom")
    @Mapping(source = "acteurBenef.prenomUsuel", target = "prenomUsuel")
    @Mapping(source = "acteurBenef.nomUsuel", target = "nomUsuel")
    @Mapping(source = "acteurBenef.idActeur", target = "idActeurBenef")
    @Mapping(source = "acteurBenef.typeActeur", target = "typeActeur")
    @Mapping(source = "acteurBenef.idActeur", target = "idActeur")
    @Mapping(source = "statut.libelle", target = "statut")
    @Mapping(source = "statut", target = "statutDemande")
    @Mapping(source = "tsCreat", target = "tsCreat")
    @Mapping(source = "tsModif", target = "tsModif")
    @Mapping(source = "topDem", target = "typeDemande")
    HistoriqueDemandeDroitDto demandeDroitToHistoriqueDemandeDroitDto(DemandeDroit demandeDroit);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "offre.id", target = "idOffre")
    @Mapping(source = "offre.libelle", target = "libelleOffre")
    @Mapping(source = "acteurBenef.login", target = "acteurBenefLogin")
    @Mapping(source = "acteurBenef.nom", target = "nom")
    @Mapping(source = "acteurBenef.prenom", target = "prenom")
    @Mapping(source = "acteurBenef.prenomUsuel", target = "prenomUsuel")
    @Mapping(source = "acteurBenef.nomUsuel", target = "nomUsuel")
    @Mapping(source = "acteurBenef.typeActeur", target = "typeActeur")
    @Mapping(source = "acteurBenef.idActeur", target = "idActeur")
    @Mapping(source = "statut.id", target = "idStatus")
    @Mapping(source = "statut.libelle", target = "statut")
    @Mapping(source = "topDem", target = "typeDemande")
    @Mapping(source = "identifiant", target = "identifiant")
    ExportDemandeDroitDto demandeDroitToExportDemandeDroitDtoDto(DemandeDroit demandeDroit);

    List<ExportDemandeDroitDto> demandeDroitToExportDemandeDroitDtoList(List<DemandeDroit> demandeDroits);


}
