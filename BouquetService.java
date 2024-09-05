package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.BouquetDto;

import java.util.List;

public interface BouquetService {

    List<BouquetDto> getAllBouquets();

    void saveAll(List<BouquetDto> bouquets);

    void deleteBouquet(Long idOffre);
}