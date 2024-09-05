package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.mapper.HistoriqueCelluleMapper;
import fr.vdm.referentiel.refadmin.model.HistoriqueCellule;
import fr.vdm.referentiel.refadmin.repository.HistoriqueCelluleRepository;
import fr.vdm.referentiel.refadmin.service.CelluleService;
import fr.vdm.referentiel.refadmin.service.HistoriqueCelluleService;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class HistoriqueCelluleServiceImpl implements HistoriqueCelluleService {

    private final HistoriqueCelluleRepository historiqueCelluleRepository;
    private final CelluleService celluleService;

    public HistoriqueCelluleServiceImpl(HistoriqueCelluleRepository historiqueCelluleRepository, CelluleService celluleService) {
        this.historiqueCelluleRepository = historiqueCelluleRepository;
        this.celluleService = celluleService;
    }


    @Override
    public CelluleDto findCelluleOrHistoriqueCelluleByCode(String code) throws ServiceException {
        log.info(String.format("Verifier si le code %s existe dans la table 'cellule'", code));
        if (this.celluleService.findIdCelluleByCode(code) != null){
            log.debug(String.format("Le code %s existe dans la table 'cellule'", code));
            return this.celluleService.findCelluleCollMarByCode(code);
        }
        log.info(String.format("Le code %s n'existe pas dans la table 'cellule'", code));
        log.info(String.format("Verifier si le code %s existe dans la table 'historique des cellules'", code));

        List<HistoriqueCellule> historiqueCelluleList =  this.historiqueCelluleRepository.findHistoriqueCelluleByCode(code);

        if (historiqueCelluleList.isEmpty()){
            log.warn(String.format("Le code %s n'existe pas dans la table 'historique des cellules'", code));
            throw new ServiceException(String.format("Votre code %s n'existe pas", code));
        }

        Optional<HistoriqueCellule> historiqueCellule =
                historiqueCelluleList.stream().max((cell1, cell2) -> cell2.getFinHis()
                        .compareTo(cell1.getFinHis()));
        log.debug(String.format("Le code %s existe dans la table 'historique des cellules'", code));
        return HistoriqueCelluleMapper.INSTANCE.historiqueCelluleToCelluleDto(historiqueCellule.get());


    }
}
