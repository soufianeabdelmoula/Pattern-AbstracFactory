package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.RfsGroupeApplicationRequestDto;
import fr.vdm.referentiel.refadmin.model.RFSGroupeApplicatif;
import fr.vdm.referentiel.refadmin.service.impl.RFSGroupeApplicationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("api/v1/groupe-applicatif/")
public class RFSGroupeApplicatifController {

    private final RFSGroupeApplicationServiceImpl rfsGroupeApplicationService;

    public RFSGroupeApplicatifController(RFSGroupeApplicationServiceImpl rfsGroupeApplicationService) {
        this.rfsGroupeApplicationService = rfsGroupeApplicationService;
    }

    @PostMapping("groupes-principaux")
    public List<RFSGroupeApplicatif> getPrincipalGroupesByCommonNameAndDescription(@RequestBody RfsGroupeApplicationRequestDto rfsGroupeApplicatif) {
        return rfsGroupeApplicationService.findPrincipalGroupesByCommonNameAndDescription(rfsGroupeApplicatif);
    }

    @PostMapping("groupes-principaux/{idPrnGroup}/groupes-secondaires")
    public List<RFSGroupeApplicatif> getSecondaryGroupesByCommonNameAndDescription(@PathVariable Long idPrnGroup, @RequestBody RfsGroupeApplicationRequestDto rfsGroupeApplicatif) {
        return rfsGroupeApplicationService.findSecondaryGroupesByCommonNameAndDescription(idPrnGroup, rfsGroupeApplicatif);
    }

    @GetMapping("groupes-secondaires")
    public List<RFSGroupeApplicatif> getAllSecondaryGroups() {
        return rfsGroupeApplicationService.findAllSecondaryGroupes();
    }

    @GetMapping("/{idGrp}")
    public ResponseEntity<RFSGroupeApplicatif> getProfilsbyId(@PathVariable Long idGrp) {
        return new ResponseEntity<>(this.rfsGroupeApplicationService.findById(idGrp), HttpStatus.OK);
    }
}
