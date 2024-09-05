package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.LienProfilPermissionDto;
import fr.vdm.referentiel.refadmin.dto.PermissionDto;
import fr.vdm.referentiel.refadmin.dto.ProfilDto;
import fr.vdm.referentiel.refadmin.mapper.PermissionMapper;
import fr.vdm.referentiel.refadmin.mapper.ProfilMapper;
import fr.vdm.referentiel.refadmin.model.LienProfilPermission;
import fr.vdm.referentiel.refadmin.repository.GroupeApplicatifRepository;
import fr.vdm.referentiel.refadmin.repository.LienProfilPermissionRepository;
import fr.vdm.referentiel.refadmin.repository.PermissionRepository;
import fr.vdm.referentiel.refadmin.service.PermissionService;
import fr.vdm.referentiel.refadmin.utils.ConstanteAdministration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private GroupeApplicatifRepository groupeApplicatifRepository;


    private PermissionMapper permissionMapper = PermissionMapper.INSTANCE;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private LienProfilPermissionRepository lienProfilPermissionRepository;

    public List<ProfilDto> getProfilsRfa() {
        return ProfilMapper.INSTANCE.groupesApplicatifsToProfilsDtos(this.groupeApplicatifRepository.findByParametrageLdap_Offre_CodeOffreOrderByDnAsc(ConstanteAdministration.CODE_OFFRE_RFA));
    }

    public List<LienProfilPermissionDto> getAllLiensProfilPermission() {
        return this.permissionMapper.liensProfilPermissionToLienPermissionProfilDto(this.lienProfilPermissionRepository.findAll());
    }

    public List<PermissionDto> getAllPermissions() {
        return this.permissionMapper.permissionsToPermissionDto(this.permissionRepository.findAll());
    }

    public void saveAllLiensProfilPermission(List<LienProfilPermissionDto> liensDto) {
        List<LienProfilPermission> liens = this.permissionMapper.liensProfilPermissionDtoToLienPermissionProfil(liensDto);
        this.lienProfilPermissionRepository.deleteAll();
        this.lienProfilPermissionRepository.saveAll(liens);
    }
}