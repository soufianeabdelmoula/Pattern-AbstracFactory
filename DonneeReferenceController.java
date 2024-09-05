package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.DonneeReferenceDto;
import fr.vdm.referentiel.refadmin.dto.TypeDonneeReferenceDto;
import fr.vdm.referentiel.refadmin.service.DonneeReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/donnee-reference")
public class DonneeReferenceController {

    @Autowired
    private DonneeReferenceService donneeReferenceService;

    @GetMapping("/types")
    public ResponseEntity<List<TypeDonneeReferenceDto>> getTypesDonneeReference() {
        return new ResponseEntity<>(this.donneeReferenceService.getAllTypes(), HttpStatus.OK);
    }

    @GetMapping("/{code}")
    public ResponseEntity<List<DonneeReferenceDto>> getDonnesReferenceByCode(@PathVariable("code") String code) {
        return new ResponseEntity<>(this.donneeReferenceService.getByCode(code), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Void> saveDonneeReference(@RequestBody DonneeReferenceDto donnee) {
        this.donneeReferenceService.saveDonneeReference(donnee);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteDonneeReference(@RequestParam String codeType, @RequestParam Long id) {
        this.donneeReferenceService.deleteDonneeReference(codeType, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}