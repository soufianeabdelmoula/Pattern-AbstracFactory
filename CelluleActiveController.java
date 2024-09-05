package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.service.CelluleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/cellule/")
public class CelluleActiveController {

    private static final Logger K_LOGGER = LoggerFactory.getLogger(CelluleActiveController.class);

    @Autowired
    private CelluleService celluleService;

    @GetMapping("/niveau/{niveau}")
    public ResponseEntity<List<CelluleDto>> findCellulesByNiveau(@PathVariable("niveau") Long niveau) {
        K_LOGGER.info("Récupération de toutes les cellules de niveau {}", niveau);
        return new ResponseEntity<>(this.celluleService.findCellulesActivesByNiveau(niveau), HttpStatus.OK);
    }

  @GetMapping("/search")
        public ResponseEntity<List<CelluleDto>> findCellulesByInput(@RequestParam String input) {
            return new ResponseEntity<>(this.celluleService.findCellulesActivesByInput(input), HttpStatus.OK);
    }

    @GetMapping("/filles")
    public ResponseEntity<List<CelluleDto>> findCellulesFilles(@RequestParam String code) {
        return new ResponseEntity<>(this.celluleService.findCellulesActivesFilles(code), HttpStatus.OK);
    }

    @GetMapping("/{code}")
    public ResponseEntity<CelluleDto> findCelluleByCode(@PathVariable String code) {
        return new ResponseEntity<>(this.celluleService.findCelluleActiveByCode(code), HttpStatus.OK);
    }
}