package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.ActeurADDto;
import fr.vdm.referentiel.refadmin.dto.CreationCompteDto;
import fr.vdm.referentiel.refadmin.model.ad.ActeurAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteApplicatifAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteRessourceAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteServiceAD;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.ldap.support.LdapUtils;

import javax.naming.Name;
import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ActeurADMapper {

    ActeurADMapper INSTANCE = Mappers.getMapper(ActeurADMapper.class);

    @Mappings({@Mapping(expression = "java( acteurAD.getDn() != null ? acteurAD.getDn().toString() : null)", target = "dn"),
            @Mapping(source = "fonction", target = "title")})
    ActeurADDto acteurADToActeurADDto(ActeurAD acteurAD);

    @Mappings({@Mapping(source = "dn", target = "dn", qualifiedByName = "stringToName"),
            @Mapping(source = "title", target = "fonction")})
    ActeurAD acteurADDtoToActeurAD(ActeurADDto acteurADDto);

    List<ActeurADDto> acteurADToActeurADDtoList(List<ActeurAD> acteurADList);

    List<ActeurADDto> compteApplicatifADToActeurADDtoList(List<CompteApplicatifAD> compteApplicatifADS);

    List<ActeurADDto> compteRessourceADToActeurADDtoList(List<CompteRessourceAD> compteRessourceADS);

    List<ActeurADDto> compteServiceADToActeurADDtoList(List<CompteServiceAD> compteServiceADS);

    @Named("stringToName")
    static Name stringToName(String name) {
        return LdapUtils.newLdapName(name);
    }

    @Mappings({@Mapping(expression = "java( compteServiceAD.getDn() != null ? compteServiceAD.getDn().toString() : null)", target = "dn")})
    ActeurADDto compteServiceAdToActeurADDto(CompteServiceAD compteServiceAD);

    @Mappings({@Mapping(expression = "java( compteRessourceAD.getDn() != null ? compteRessourceAD.getDn().toString() : null)", target = "dn")})
    ActeurADDto compteRessourceAdToActeurADDto(CompteRessourceAD compteRessourceAD);

    @Mappings({@Mapping(expression = "java( compteApplicatifAD.getDn() != null ? compteApplicatifAD.getDn().toString() : null)", target = "dn")})
    ActeurADDto compteApplicatifAdToActeurADDto(CompteApplicatifAD compteApplicatifAD);


    @Mappings({
            @Mapping(target = "identifiant", source = "login"),
            @Mapping(target = "dn", ignore = true)
    })
    CompteServiceAD creationCompteDtoToCompteServiceAd(CreationCompteDto compte);


    void update(@MappingTarget CompteServiceAD cs1, CompteServiceAD cs2);

    void update(@MappingTarget CompteRessourceAD compteRessourceAD, CompteRessourceAD compteRessourceAD1);

    void update(@MappingTarget CompteApplicatifAD compteApplicatifAD, CompteApplicatifAD compteApplicatifAD1);
}