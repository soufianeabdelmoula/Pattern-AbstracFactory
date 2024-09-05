package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.RfsGroupeApplicationRequestDto;
import fr.vdm.referentiel.refadmin.model.RFSGroupeApplicatif;

import java.util.List;

public interface RFSGroupeApplicationService {

    List<RFSGroupeApplicatif> findAllPrincipalGroupes();

    List<RFSGroupeApplicatif> findAllSecondaryGroupes();

    List<RFSGroupeApplicatif> findSecondaryGroupesByPRincipalGrp(Long idPrnGroup);

    List<RFSGroupeApplicatif> findPrincipalGroupesByCommonNameAndDescription(RfsGroupeApplicationRequestDto rfsGroupeApplicationRequestDto);

    List<RFSGroupeApplicatif> findSecondaryGroupesByCommonNameAndDescription(RfsGroupeApplicationRequestDto rfsGroupeApplicationRequestDto);

    List<RFSGroupeApplicatif> findSecondaryGroupesByCommonNameAndDescription(Long idPrnGroup, RfsGroupeApplicationRequestDto rfsGroupeApplicationRequestDto);

    RFSGroupeApplicatif findByDN(String dn);

    RFSGroupeApplicatif findById(Long id);

    RFSGroupeApplicatif findByCN(String cn);
}
