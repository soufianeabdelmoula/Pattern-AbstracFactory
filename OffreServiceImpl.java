package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.mapper.OffreMapper;
import fr.vdm.referentiel.refadmin.mapper.ProfilMapper;
import fr.vdm.referentiel.refadmin.mapper.QuestionMapper;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.model.ad.GroupeAD;
import fr.vdm.referentiel.refadmin.repository.GroupeApplicatifRepository;
import fr.vdm.referentiel.refadmin.repository.OffreRepository;
import fr.vdm.referentiel.refadmin.repository.ParametrageLdapRepository;
import fr.vdm.referentiel.refadmin.repository.QuestionRepository;
import fr.vdm.referentiel.refadmin.service.GroupeADService;
import fr.vdm.referentiel.refadmin.service.OffreService;
import fr.vdm.referentiel.refadmin.service.RFSGroupeApplicationService;
import fr.vdm.referentiel.refadmin.utils.ExportFileCsvUtils;
import fr.vdm.referentiel.refadmin.utils.ListsUtils;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@Transactional
public class OffreServiceImpl implements OffreService {


    private final OffreMapper offreMapper = OffreMapper.INSTANCE;
    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private GroupeApplicatifServiceImpl groupeApplicatifService;

    @Autowired
    private RFSGroupeApplicationService rfsGroupeApplicationService;


    private final QuestionMapper questionMapper = QuestionMapper.INSTANCE;

    @Autowired
    private ParametrageLdapRepository parametrageLdapRepository;

    private final ProfilMapper profileMapper = ProfilMapper.INSTANCE;
    @Autowired
    private GroupeApplicatifRepository groupeApplicatifRepository;

    @Autowired
    private GroupeADService groupeADService;

    public List<OffreDto> findAllOffres() {
        return this.offreMapper.offreToOffreDtoList(this.offreRepository.findAll());
    }

    public List<QuestionDto> findQuestionsByIdOffre(Long idOffre) {
        return questionMapper.questionsToQuestionsDtos(questionRepository.findByOffre_IdOrderByOrdreDesc(idOffre));
    }

    public List<ProfilDto> findProfilsByIdOffre(Long idOffre) {
        return profileMapper.groupesApplicatifsToProfilsDtos(groupeApplicatifService.findByParametrageLdap_Offre_IdOrderByDnAsc(idOffre));
    }

    public OffreDto saveOffre(OffreRequestDto offreRequestDto) throws ServiceException {
        log.info(String.format("Sauvegarde de l'offre %s", offreRequestDto.getLibelle()));

        Optional<Offre> optionalOffreInDb = Optional.empty();
        if (offreRequestDto.getId() != null) {
            optionalOffreInDb = offreRepository.findById(offreRequestDto.getId());
        }

        Offre offre;
        if (optionalOffreInDb.isPresent()) {
            offre = optionalOffreInDb.get();
            offreMapper.updateOffreFromDto(offre, offreRequestDto);
        } else {
            offre = offreMapper.offreRequestDtoToOffre(offreRequestDto);
        }

        // Update questions
        updateQuestions(offre);

        // Update AD group information
        updateADGroup(offre, offreRequestDto.getAdGroup(), offreRequestDto.getAd());

        return offreMapper.offreToOffreDto(offre);
    }

    private void updateQuestions(Offre offre) {
        if (offre.getQuestions() == null || offre.getQuestions().isEmpty()) {
            offre.getQuestions().clear();
            return;
        }

        offre.getQuestions().forEach(qst -> {
            qst.setOffre(offre);
            qst.setOrdre(1L);
        });
    }

    private void updateADGroup(Offre offre, AdGroupDto adGroupDto, boolean ad) throws ServiceException {
        if (!ad) return;
        if (adGroupDto != null && adGroupDto.getGroupePrincipal() != null) {
            ParametrageLdap parametrageLdap = ParametrageLdap.builder()
                    .offre(offre)
                    .dnPrinc(adGroupDto.getGroupePrincipal().getDn())
                    .build();

            parametrageLdapRepository.save(parametrageLdap);
            //Rattachement des groupes secondaires (profils) au parametrageLDAP
            for (OffreGroupeDto offreGroupeDto : adGroupDto.getGroupesSecondaires()) {

                GroupeApplicatif profil = new GroupeApplicatif();
                GroupeAD groupeAD = this.groupeADService.findByCnAndType(offreGroupeDto.getDn(), 'A');
                if (groupeAD == null) {
                    log.error(String.format("Le groupe %s n'a pas été trouvé dans l'annuaire", offreGroupeDto.getDn()));
                    throw new ServiceException(String.format("Le groupe %s n'a pas été trouvé dans l'annuaire", offreGroupeDto.getDn()));
                }
                profil.setDn(groupeAD.getDn().toString());
                profil.setParametrageLdap(parametrageLdap);
                groupeApplicatifRepository.save(profil);
            }
        }
        offreRepository.save(offre);
    }

    public Page<OffreDto> getOffresByFilters(OffreRequestDto offreRequestDto, Pageable page) {

        Page<Offre> offreList = offreRepository.findWithConditions(
                offreRequestDto.getLibelle(),
                offreRequestDto.getDescription(),
                offreRequestDto.getResponsable(),
                offreRequestDto.getLabel(),
                offreRequestDto.getMetier(),
                offreRequestDto.getTransverse(),
                page);
        return offreList.map(offreMapper::offreToOffreDto);
    }

    public void delete(List<Long> offresIds) {
        offreRepository.deleteAllById(offresIds);
    }

    public List<GroupeApplicatifDto> getGroupApplicatifList() {
        return groupeApplicatifService.findAll();
    }

    public List<GroupeApplicatifDto> getGroupApplicatifListByFilters(GroupeApplicatifRequestDto groupeApplicatifRequestDto) {
        return groupeApplicatifService.findByCommonNameAndDescription(groupeApplicatifRequestDto);
    }

    public void deleteById(Long offreId) {
        offreRepository.deleteById(offreId);
    }

    public OffreDto findOffreByCodeOffre(String codeOffre) {
        return this.offreMapper.offreToOffreDto(offreRepository.findByCodeOffre(codeOffre));
    }

    @Override
    public OffreDto getOffre(Long idOffre) {
        Optional<Offre> offre = offreRepository.findById(idOffre);
        OffreDto offreDto = offre.map(value -> offreMapper.offreToOffreDto(value))
                .orElseThrow(NullPointerException::new);
        List<Question> questions = questionRepository.findByOffre_Id(idOffre);
        List<QuestionDto> questionDtos = questionMapper.questionsToQuestionsDtos(questions);

        offreDto.setQuestions(questionDtos);
        offreMapper.fillFields(offre.get(), offreDto);
        return offreDto;
    }

    public ParametrageLdap findParametrageLdapByIdOffre(Long idOffre) {
        Optional<Offre> offre = offreRepository.findById(idOffre);
        return offre.get().getParametrageLdap();
    }

    public RfsGroupeApplicationRequestDto findGroupePrincipalByIdOffre(Long idOffre) {
        try {
            Optional<Offre> offre = offreRepository.findById(idOffre);
            ParametrageLdap parametrageLdap = offre.get().getParametrageLdap();
            String dn = parametrageLdap.getDnPrinc();
            String cn = dn.substring(3, dn.indexOf(",ou"));
            RfsGroupeApplicationRequestDto rfsGrpAppDto = new RfsGroupeApplicationRequestDto();
            rfsGrpAppDto.setCommonName(cn);
            List<RFSGroupeApplicatif> rfsGroupeApplicatifs = rfsGroupeApplicationService.findPrincipalGroupesByCommonNameAndDescription(rfsGrpAppDto);
            rfsGrpAppDto.setCommonName(dn);
            rfsGrpAppDto.setDescription(rfsGroupeApplicatifs.get(0).getLibLongGrAp());
            rfsGrpAppDto.setId(rfsGroupeApplicatifs.get(0).getId());
            return rfsGrpAppDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<RfsGroupeApplicationRequestDto> findGroupesSecondairesByIdOffre(Long idOffre) {
        List<GroupeApplicatif> groupeApplicatifs = this.groupeApplicatifRepository.findByParametrageLdap_Offre_IdOrderByDnAsc(idOffre);
        List<RfsGroupeApplicationRequestDto> listGrpAppRequestDto = new ArrayList<>();
        for (GroupeApplicatif grp : groupeApplicatifs) {
            RFSGroupeApplicatif rfsGrp = this.rfsGroupeApplicationService.findByDN(grp.getDn());


            RfsGroupeApplicationRequestDto rfsGrpDto = new RfsGroupeApplicationRequestDto();

            rfsGrpDto.setCommonName(rfsGrp.getLibLdapGrAp());
            rfsGrpDto.setId(rfsGrp.getId());
            rfsGrpDto.setDescription(rfsGrp.getLibLongGrAp());
            listGrpAppRequestDto.add(rfsGrpDto);
        }
        return listGrpAppRequestDto;
    }


    public void deleteAllById(List<Long> offresIdList) {
        offreRepository.deleteAllById(offresIdList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OffreDto getOffreDtoByIdOffre(Long idOffre) {
        return OffreMapper.INSTANCE.offreToOffreDto(this.offreRepository.findById(idOffre).orElse(null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Offre getOffreByIdOffreIfIdIsNotNull(Long idOffre) {
        return idOffre != null ? this.offreRepository.findById(idOffre).orElse(null) : null;
    }

    /**
     * Recherche des d�pendances pour une offre
     *
     * @param idOffre l'identifiant de l'offre.
     * @return la liste des d�pendances pour cette offre
     */
    public List<Offre> findDependances(final Long idOffre) {
        return offreRepository.findDependances(idOffre);
    }

    @Override
    public byte[] getExportOffre() {
        List<ExportOffreDto> offreDtoList = OffreMapper.INSTANCE.offreToExportOffreDtoList(this.offreRepository.findAll());
        return ExportFileCsvUtils.exportCsvFile(offreDtoList);
    }
}