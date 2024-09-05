package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.DonneeReferenceDto;
import fr.vdm.referentiel.refadmin.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DonneesReferenceMapper {

    DonneesReferenceMapper INSTANCE = Mappers.getMapper(DonneesReferenceMapper.class);

    DonneeReferenceDto typeCompteApplicatifToDonneeReferenceDto(TypeCompteApplicatif typeCompteApplicatif);

    List<DonneeReferenceDto> typeCompteApplicatifListToDonneeReferenceDtoList(List<TypeCompteApplicatif> typeCompteApplicatifList);

    DonneeReferenceDto typeCompteRessourceToDonneeReferenceDto(TypeCompteRessource typeCompteRessource);

    List<DonneeReferenceDto> typeCompteRessourceListToDonneeReferenceDtoList(List<TypeCompteRessource> typeCompteApplicatifList);

    DonneeReferenceDto typeCompteServiceToDonneeReferenceDto(TypeCompteService typeCompteService);

    List<DonneeReferenceDto> typeCompteServiceListToDonneeReferenceDtoList(List<TypeCompteService> typeCompteApplicatifList);

    DonneeReferenceDto roleActeurToDonneeReferenceDto(RoleActeur roleActeur);

    List<DonneeReferenceDto> roleActeurListToDonneeReferenceDtoList(List<RoleActeur> roleActeur);

    DonneeReferenceDto roleDroitToDonneeReferenceDto(RoleDroit roleDroit);

    List<DonneeReferenceDto> roleDroitListToDonneeReferenceDtoList(List<RoleDroit> roleDroit);


    TypeCompteApplicatif donneReferenceDtoToTypeCompteApplicatif(DonneeReferenceDto donneeReferenceDto);

    TypeCompteService donneReferenceDtoToTypeCompteService(DonneeReferenceDto donneeReferenceDto);

    TypeCompteRessource donneReferenceDtoToTypeCompteRessource(DonneeReferenceDto donneeReferenceDto);

    RoleActeur donneReferenceDtoToRoleActeur(DonneeReferenceDto donneeReferenceDto);

    RoleDroit donneReferenceDtoToRoleDroit(DonneeReferenceDto donneeReferenceDto);

    DonneeReferenceDto driverToDonneeReferenceDto(Driver driver);

    List<DonneeReferenceDto> driverListToDonneeReferenceDtoList(List<Driver> drivers);

    Driver DonneeReferenceDtoToDriver(DonneeReferenceDto donneeReferenceDto);

    DonneeReferenceDto typeTelephonieToDonneeReferenceDto(TypeTelephonie typeTelephonie);

    List<DonneeReferenceDto> typeTelephonieListToDonneeReferenceDtoList(List<TypeTelephonie> typeTelephonies);

    TypeTelephonie donneReferenceDtoToTypeTelephonie(DonneeReferenceDto donneeReferenceDto);

}