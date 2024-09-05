package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.RfsGroupeApplicationRequestDto;
import fr.vdm.referentiel.refadmin.model.RFSGroupeApplicatif;
import fr.vdm.referentiel.refadmin.repository.RFSGroupeApplicatifRepo;
import fr.vdm.referentiel.refadmin.service.RFSGroupeApplicationService;
import fr.vdm.referentiel.refadmin.utils.DnUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RFSGroupeApplicationServiceImpl implements RFSGroupeApplicationService {

    private final RFSGroupeApplicatifRepo rfsGroupeApplicatifRepo;

    public RFSGroupeApplicationServiceImpl(RFSGroupeApplicatifRepo rfsGroupeApplicatifRepo) {
        this.rfsGroupeApplicatifRepo = rfsGroupeApplicatifRepo;
    }


    public List<RFSGroupeApplicatif> findAllPrincipalGroupes() {
        return rfsGroupeApplicatifRepo.findByGrpApPereId(0L);
    }

    public List<RFSGroupeApplicatif> findAllSecondaryGroupes() {
        return rfsGroupeApplicatifRepo.findByGrpApPereIdNot(0L);
    }

    public List<RFSGroupeApplicatif> findSecondaryGroupesByPRincipalGrp(Long idPrnGroup) {
        return rfsGroupeApplicatifRepo.findByGrpApPereId(idPrnGroup);
    }

    public List<RFSGroupeApplicatif> findPrincipalGroupesByCommonNameAndDescription(RfsGroupeApplicationRequestDto rfsGroupeApplicationRequestDto) {
        return rfsGroupeApplicatifRepo.findPrincipalGroupsByConditions(rfsGroupeApplicationRequestDto.getCommonName(),
                rfsGroupeApplicationRequestDto.getDescription());
    }

    public List<RFSGroupeApplicatif> findSecondaryGroupesByCommonNameAndDescription(Long idPrnGroup, RfsGroupeApplicationRequestDto rfsGroupeApplicationRequestDto) {
        return rfsGroupeApplicatifRepo.findSecondaryGroupsByConditions(rfsGroupeApplicationRequestDto.getCommonName(),
                rfsGroupeApplicationRequestDto.getDescription(), idPrnGroup);
    }

    @Override
    public List<RFSGroupeApplicatif> findSecondaryGroupesByCommonNameAndDescription(RfsGroupeApplicationRequestDto rfsGroupeApplicationRequestDto) {
        return rfsGroupeApplicatifRepo.findSecondaryGroupsByConditions(rfsGroupeApplicationRequestDto.getCommonName(), rfsGroupeApplicationRequestDto.getDescription());
    }

    public RFSGroupeApplicatif findByDN(String dn) {
        return this.rfsGroupeApplicatifRepo.findByLibLdapGrAp(DnUtils.extraireValeurAttributFromDn(dn, "cn"));
    }

    public RFSGroupeApplicatif findByCN(String cn) {
        return this.rfsGroupeApplicatifRepo.findByLibLdapGrAp(cn);
    }

    public RFSGroupeApplicatif findById(Long id) {
        return this.rfsGroupeApplicatifRepo.findById(id).orElse(null);
    }
}
