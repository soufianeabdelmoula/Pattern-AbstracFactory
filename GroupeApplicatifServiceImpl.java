package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.GroupeApplicatifDto;
import fr.vdm.referentiel.refadmin.dto.GroupeApplicatifRequestDto;
import fr.vdm.referentiel.refadmin.dto.ProfilDto;
import fr.vdm.referentiel.refadmin.mapper.GroupeApplicatifMapper;
import fr.vdm.referentiel.refadmin.mapper.ProfilMapper;
import fr.vdm.referentiel.refadmin.model.GroupeApplicatif;
import fr.vdm.referentiel.refadmin.repository.GroupeApplicatifRepository;
import fr.vdm.referentiel.refadmin.service.GroupeApplicatifService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupeApplicatifServiceImpl implements GroupeApplicatifService {

    private final GroupeApplicatifRepository groupeApplicatifRepository;
    private static final GroupeApplicatifMapper groupeApplicatifMapper = GroupeApplicatifMapper.INSTANCE;

    public GroupeApplicatifServiceImpl(GroupeApplicatifRepository groupeApplicatifRepository) {
        this.groupeApplicatifRepository = groupeApplicatifRepository;
    }

    public List<GroupeApplicatif> findByParametrageLdap_Offre_IdOrderByDnAsc(Long idOffre) {
        return groupeApplicatifRepository.findByParametrageLdap_Offre_IdOrderByDnAsc(idOffre);
    }

    public List<GroupeApplicatifDto> findAll() {
        List<GroupeApplicatif> groupeApplicatifs = groupeApplicatifRepository.findAll();
        return groupeApplicatifMapper.groupeApplicatifToDtoList(groupeApplicatifs);
    }

    public ProfilDto findById(Long idGroupe) {
        return ProfilMapper.INSTANCE.groupeApplicatifToProfilDto(groupeApplicatifRepository.findById(idGroupe).orElse(null));
    }

    public List<GroupeApplicatifDto> findByCommonNameAndDescription(GroupeApplicatifRequestDto groupeApplicatifRequestDto) {
        List<GroupeApplicatif> groupeApplicatifs = groupeApplicatifRepository.findByCnAndDescription(groupeApplicatifRequestDto.getCommonName());
        return groupeApplicatifMapper.groupeApplicatifToDtoList(groupeApplicatifs);
    }

    @Override
    public GroupeApplicatif save(GroupeApplicatif groupeApplicatif) {
        return groupeApplicatifRepository.save(groupeApplicatif);
    }

    @Override
    public GroupeApplicatif getGroupeApplicatifByDn(String dn) {
        return this.groupeApplicatifRepository.findGroupeApplicatifByDn(dn);
    }

    @Override
    public Boolean existsGroupeApplicatifByDn(String dn) {
        return this.groupeApplicatifRepository.existsGroupeApplicatifByDn(dn);
    }

    @Override
    public GroupeApplicatif getGroupeAppSecByIdGrpAppAsecIfIdIsNotNull(Long idGrpApp) {
        return idGrpApp != null ? this.groupeApplicatifRepository.findById(idGrpApp).orElse(null): null;
    }


}
