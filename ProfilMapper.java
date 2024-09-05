package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.ProfilDto;
import fr.vdm.referentiel.refadmin.model.GroupeApplicatif;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfilMapper {

    ProfilMapper INSTANCE = Mappers.getMapper(ProfilMapper.class);

    @Mappings({
            @Mapping(target = "idGrp", source = "id"),
            @Mapping(target = "libelle", expression = "java(getCnFromDn(groupeApplicatif.getDn()))")
    })
    ProfilDto groupeApplicatifToProfilDto(GroupeApplicatif groupeApplicatif);

    @Mappings({
            @Mapping(target = "id", source = "idGrp"),
            @Mapping(target = "dn", expression = "java(getDnFromCn(profil.getLibelle()))")
    })
    GroupeApplicatif profilDtoToGroupeApplicatif(ProfilDto profil);

    @Mappings({})
    List<ProfilDto> groupesApplicatifsToProfilsDtos(List<GroupeApplicatif> groupesApplicatifs);

    @Named("cnFromDn")
    default String getCnFromDn(String dn) {
        if (dn == null) return null;
        return dn.split(",")[0].split("=")[1];
    }

    @Named("dnFromCn")
    default String getDnFromCn(String cn) {
        if (cn == null) return null;
        return String.format("cn=%s,ou=utilisateurs,dc=vdm,dc=mars", cn);
    }


}