package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.service.CelluleService;
import fr.vdm.referentiel.refadmin.service.HistoriqueCelluleService;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/structure/")
public class StructureController {

    @Autowired
    private CelluleService celluleService;

    private final HistoriqueCelluleService historiqueCelluleService;

    public StructureController(HistoriqueCelluleService historiqueCelluleService) {
        this.historiqueCelluleService = historiqueCelluleService;
    }

    @GetMapping("/{code}")
    public ResponseEntity<CelluleDto> findCelluleByCode(@PathVariable String code) {
        return new ResponseEntity<>(this.celluleService.findCelluleByCode(code), HttpStatus.OK);
    }

    @GetMapping("cell-hcell/{code}")
    public ResponseEntity<CelluleDto> getCelluleOrHistoriqueCelluleByCode(@PathVariable String code) throws ServiceException {
        return new ResponseEntity<>(this.historiqueCelluleService.findCelluleOrHistoriqueCelluleByCode(code), HttpStatus.OK);
    }
}
