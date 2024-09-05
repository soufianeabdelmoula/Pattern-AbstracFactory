package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.LienProfilPermissionDto;
import fr.vdm.referentiel.refadmin.dto.PermissionDto;
import fr.vdm.referentiel.refadmin.dto.ProfilDto;

import java.util.List;

public interface PermissionService {
    List<ProfilDto> getProfilsRfa();

    List<LienProfilPermissionDto> getAllLiensProfilPermission();

    List<PermissionDto> getAllPermissions();

    void saveAllLiensProfilPermission(List<LienProfilPermissionDto> liens);
}