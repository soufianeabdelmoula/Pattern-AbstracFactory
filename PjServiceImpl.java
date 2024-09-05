package fr.vdm.referentiel.refadmin.service.impl;


import fr.vdm.referentiel.refadmin.alfresco.dto.AlfrescoFileDTO;
import fr.vdm.referentiel.refadmin.alfresco.dto.PieceJointeDto;
import fr.vdm.referentiel.refadmin.alfresco.service.AlfrescoService;
import fr.vdm.referentiel.refadmin.mapper.PieceJointeMapper;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.PieceJointe;
import fr.vdm.referentiel.refadmin.repository.PieceJointeRepository;
import fr.vdm.referentiel.refadmin.service.PjService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
public class PjServiceImpl
        implements PjService
{
    private static final Logger K_LOGGER = LoggerFactory.getLogger(PjService.class);

    @Autowired
    private AlfrescoService alfrescoService;


    @Autowired
    private PieceJointeRepository pieceJointeRepository;


    @Transactional
    public void saveFile(AlfrescoFileDTO afd, PieceJointeDto pjDto, String nomoffre, ActeurVue beneficiaire, String loginCreat) {
        String nodeRef = this.alfrescoService.sendFile(afd, pjDto, nomoffre, beneficiaire);
        if (null == nodeRef) {

            K_LOGGER.error("La PJ pour la demande n° {} n'as pas été enregistré correctement", pjDto.getIdDemande());
            return;
        }

        PieceJointe pj = PieceJointeMapper.INSTANCE.PieceJointeDtoToPieceJointe(pjDto);

        if (pj.getIdActeurBeneficiaire() == null) {
            pj.setIdActeurBeneficiaire(Long.valueOf(-1L));
        }

        pj.setNodeRef(nodeRef);
        this.pieceJointeRepository.save(pj);
    }

    @Override
    public List<PieceJointeDto> getAllByIdDemandeAndTypeDemande(Long idDemande, String typeDemande) {
        List<PieceJointeDto> pjDto = new ArrayList<>();
        List<PieceJointe> pjList = this.pieceJointeRepository.findAllByIdDemandeAndTypeDemande(idDemande, typeDemande);

        for (PieceJointe pj : pjList) {
            pjDto.add(new PieceJointeDto(pj));
        }
        return pjDto;
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public void deletePieceJointe(final Long idActeurBeneficiaire, final String typeDemande, final Long idOffre) {
        List<PieceJointe> pjToDeleteList = this.pieceJointeRepository.findAllByIdDemandeAndTypeDemande(idOffre, typeDemande);
        for (PieceJointe pj : pjToDeleteList) {
            this.alfrescoService.deleteFile(pj.getNodeRef());
            this.pieceJointeRepository.delete(pj);
        }
    }
}


