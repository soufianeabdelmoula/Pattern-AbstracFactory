package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.DemandeActeurDto;
import fr.vdm.referentiel.refadmin.dto.ExportDemandeActeurDto;
import fr.vdm.referentiel.refadmin.dto.HistoriqueDemandeDroitDto;
import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DemandeActeurMapper {

    DemandeActeurMapper INSTANCE = Mappers.getMapper(DemandeActeurMapper.class);

    DemandeActeurDto demandeActeurToDto(DemandeActeur demandeActeur);

    List<DemandeActeurDto> demandeActeurToDtoList(List<DemandeActeur> demandeActeurs);

    @Mapping(source = "statut.libelle", target = "statut")
    @Mapping(source = "login", target = "acteurBenefLogin")
    @Mapping(source = "statut", target = "statutDemande")
    @Mapping(source = "acteurBenef.idActeur", target = "idActeur")
    HistoriqueDemandeDroitDto demandeActeurToHistoriqueDemandeDroitDto(DemandeActeur demandeActeur);
    List<ExportDemandeActeurDto> demandeActeurToExportDemandeActeurDtoList(List<DemandeActeur> demandeActeurs);

    @Mapping(source = "statut.libelle", target = "statut")
    @Mapping(source = "statut.id", target = "idStatus")
    @Mapping(source = "acteurBenef.idActeur", target = "idActeurBenef")
    @Mapping(source = "organisation.id", target = "idOrganisation")
    @Mapping(source = "organisation.nom", target = "organisation")
    ExportDemandeActeurDto demandeActeurToExportDemandeActeurDto(DemandeActeur demandeActeur);

}
