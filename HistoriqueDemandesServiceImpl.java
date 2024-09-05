package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.mapper.*;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.DemandeActeurRepository;
import fr.vdm.referentiel.refadmin.repository.DemandeDroitRepository;
import fr.vdm.referentiel.refadmin.repository.EtapeDemActeurRepository;
import fr.vdm.referentiel.refadmin.service.CelluleService;
import fr.vdm.referentiel.refadmin.service.HistoriqueDemandesService;
import fr.vdm.referentiel.refadmin.utils.ExportFileCsvUtils;
import fr.vdm.referentiel.refadmin.utils.FiltersSpecifications;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class HistoriqueDemandesServiceImpl implements HistoriqueDemandesService {

    private final DemandeDroitRepository demandeDroitRepository;
    private final DemandeActeurRepository demandeActeurRepository;
    private final EtapeDemActeurRepository etapeDemActeurRepository;
    private final StatutDemanderMapper statutDemanderMapper = StatutDemanderMapper.INSTANCE;
    private final CelluleService celluleService;


    public HistoriqueDemandesServiceImpl(DemandeDroitRepository demandeDroitRepository,
                                         DemandeActeurRepository demandeActeurRepository,
                                         EtapeDemActeurRepository etapeDemActeurRepository,
                                         CelluleService celluleService) {
        this.demandeDroitRepository = demandeDroitRepository;
        this.demandeActeurRepository = demandeActeurRepository;
        this.etapeDemActeurRepository = etapeDemActeurRepository;
        this.celluleService = celluleService;
    }

    @Override
    public Page<HistoriqueDemandeDroitDto> findAllDemandesHabilitationByFilter(FiltreHistoriqueDemandesDto filter, Pageable pageable) {

        List<CelluleDto> celluleDtoList = null;
        if (filter.getCodeAffectation() != null && !filter.getCodeAffectation().isEmpty()) {
            celluleDtoList = celluleService.findAllfilsActiveInclus(filter.getCodeAffectation());
        }

        Specification<DemandeDroit> spec = FiltersSpecifications.filterDemandesHabilitation(filter, celluleDtoList);

        return demandeDroitRepository.findAll(spec, pageable)
                .map(DemandeDroitMapper.INSTANCE::demandeDroitToHistoriqueDemandeDroitDto);
    }
    @Override
    public Page<HistoriqueDemandeDroitDto> findAllDemandesCompteActeurByFilter(FiltreHistoriqueDemandesDto filter, Pageable pageable) {
        List<CelluleDto> celluleDtoList = null;
        if (filter.getCodeAffectation() != null && !filter.getCodeAffectation().isEmpty()) {
            celluleDtoList = celluleService.findAllfilsActiveInclus(filter.getCodeAffectation());
        }

        Specification<DemandeActeur> spec = FiltersSpecifications.filterDemandesCompteActeur(filter, celluleDtoList);

        return demandeActeurRepository.findAll(spec, pageable)
                .map(DemandeActeurMapper.INSTANCE::demandeActeurToHistoriqueDemandeDroitDto);
    }

    @Override
    public List<EtapeDemActeurDto> findAllEtapesFromIdDemandeActeur(Long idDemande) {
        return this.etapeDemActeurRepository.findEtapesFromIdDemandeActeur(idDemande).stream()
                .map(EtapeDemActeurMapper.INSTANCE::etapeDemActeurToDto).collect(Collectors.toList());
    }

    @Override
    public StatutDemandeDto findStautDemandeByIdDemandeActeur(Long id) {
        return this.statutDemanderMapper.statutDemandeToStatutDemandeDto(this.demandeActeurRepository.findStautDemandeByIdDemandeActeur(id));
    }

    @Override
    public byte[] getExportCsvHistoriqueDemandesHabilitation() {
        List<ExportDemandeDroitDto> demandeList = DemandeDroitMapper.INSTANCE.demandeDroitToExportDemandeDroitDtoList(this.demandeDroitRepository.findAll());
        return ExportFileCsvUtils.exportCsvFile(demandeList);
    }

    @Override
    public byte[] getExportCsvHistoriqueDemandesCompteActeur() {
        List<ExportDemandeActeurDto> demandeList = DemandeActeurMapper.INSTANCE.demandeActeurToExportDemandeActeurDtoList(this.demandeActeurRepository.findAll());
        return ExportFileCsvUtils.exportCsvFile(demandeList);
    }
}
