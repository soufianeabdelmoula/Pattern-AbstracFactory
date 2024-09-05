package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.FiltreHistoriqueDemandesDto;
import fr.vdm.referentiel.refadmin.dto.HistoriqueImportDto;
import fr.vdm.referentiel.refadmin.dto.ImportFileDto;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ImportApplicatifService {

    ImportFileDto readFileCsvImported(MultipartFile file) throws ServiceException;
    ImportFileDto confirmImportCSVApplicatif(ImportFileDto importAppList,  String username) throws ServiceException;
    Page<HistoriqueImportDto> findAllHistoriqueImportAppByFilter(FiltreHistoriqueDemandesDto filtre, Pageable pageable);
    Page<HistoriqueImportDto> findAllHistoriqueImportApp(Pageable pageable);

    byte[] getExportHistoriqueImportAppService();
}