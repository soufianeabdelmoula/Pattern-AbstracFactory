package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.GroupeApplicatifDto;
import fr.vdm.referentiel.refadmin.dto.GroupeApplicatifRequestDto;
import fr.vdm.referentiel.refadmin.dto.ProfilDto;
import fr.vdm.referentiel.refadmin.model.GroupeApplicatif;

import java.util.List;

public interface GroupeApplicatifService {

    List<GroupeApplicatif> findByParametrageLdap_Offre_IdOrderByDnAsc(Long idOffre);

    List<GroupeApplicatifDto> findAll();

    List<GroupeApplicatifDto> findByCommonNameAndDescription(GroupeApplicatifRequestDto groupeApplicatifRequestDto);

    ProfilDto findById(Long idGroupe);

    GroupeApplicatif save(GroupeApplicatif groupeApplicatif);
    GroupeApplicatif getGroupeApplicatifByDn(String dn);
    Boolean existsGroupeApplicatifByDn(String dn);
    GroupeApplicatif getGroupeAppSecByIdGrpAppAsecIfIdIsNotNull(Long idGrpApp);
}
