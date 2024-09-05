package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.LienProfilPermissionDto;
import fr.vdm.referentiel.refadmin.dto.PermissionDto;
import fr.vdm.referentiel.refadmin.model.LienProfilPermission;
import fr.vdm.referentiel.refadmin.model.Permission;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( uses = {ProfilMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {

    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);
    @Mappings({})
    @Named("permissionToPermissionDto")
    PermissionDto permissionToPermissionDto(Permission permission);

    @Mappings({})
    List<PermissionDto> permissionsToPermissionDto(List<Permission> permissions);


    @Mappings({})
    LienProfilPermissionDto lienProfilPermissionToLienProfilPermissionDto(LienProfilPermission lienProfilPermission);

    @Mappings({})
    List<LienProfilPermissionDto> liensProfilPermissionToLienPermissionProfilDto(List<LienProfilPermission> lienProfilPermissions);

    @Mappings({})
    List<LienProfilPermission> liensProfilPermissionDtoToLienPermissionProfil(List<LienProfilPermissionDto> lienProfilPermissionsDtos);

    @Mappings({@Mapping(target = "idPerm", source = "permission.id")})
    LienProfilPermission lienProfilPermissionDtoToLienProfilPermission(LienProfilPermissionDto lienProfilPermissionDto);

}