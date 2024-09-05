package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.ExportFIleDto;
import fr.vdm.referentiel.refadmin.dto.RechercheRequest;
import fr.vdm.referentiel.refadmin.dto.RechercheResponse;


public interface ExpotAdService {
    RechercheResponse getActeursADByFilters(RechercheRequest rechercheRequest);
    byte[] exportCsvFile(ExportFIleDto exportFileDto);
}
