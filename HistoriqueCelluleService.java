package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

public interface HistoriqueCelluleService {

    CelluleDto findCelluleOrHistoriqueCelluleByCode(String code) throws ServiceException;
}
