package fr.vdm.referentiel.refadmin.controller;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.service.ImportApplicatifService;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("api/v1/import-applicatif")
@Log4j2
public class ImportApplicatifController {


    private final ImportApplicatifService importApplicatifService;

    public ImportApplicatifController(ImportApplicatifService importApplicatifService) {
        this.importApplicatifService = importApplicatifService;
    }


    @PostMapping("/upload-file")
    public ResponseEntity<ImportFileDto> handleFileCsvUpload(@RequestParam("file") MultipartFile file) throws ServiceException {
        return new ResponseEntity<>(this.importApplicatifService.readFileCsvImported(file), HttpStatus.OK);
    }
    @PostMapping("/confirm")
    public ResponseEntity<ImportFileDto> handleConfirmImportCSVApplicatif(@RequestBody ImportFileDto importApplicatif, @RequestParam String username) throws ServiceException {
        return new ResponseEntity<>(this.importApplicatifService.confirmImportCSVApplicatif(importApplicatif, username), HttpStatus.OK);
    }
    @PostMapping("/historique-by-filter")
    public ResponseEntity<Page<HistoriqueImportDto>> findAllHistoriqueImportAppByFilter(@RequestBody FiltreHistoriqueDemandesDto filtre, Pageable pageable) {
        return new ResponseEntity<>(this.importApplicatifService.findAllHistoriqueImportAppByFilter(filtre, pageable), HttpStatus.OK);
    }

    @PostMapping("/historique-all")
    public ResponseEntity<Page<HistoriqueImportDto>> findAllHistoriqueImportApp(Pageable pageable) {
        return new ResponseEntity<>(this.importApplicatifService.findAllHistoriqueImportApp(pageable), HttpStatus.OK);
    }

    @GetMapping("/historique-import-App/export-csv")
    public ResponseEntity<byte[]> getExportHistoriqueImportApp() {
        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "export.csv");

        // Return CSV as a ResponseEntity
        return ResponseEntity.ok().headers(headers).body(importApplicatifService.getExportHistoriqueImportAppService());
    }

}