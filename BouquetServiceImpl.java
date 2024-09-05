package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.BouquetDto;
import fr.vdm.referentiel.refadmin.mapper.BouquetMapper;
import fr.vdm.referentiel.refadmin.model.Bouquet;
import fr.vdm.referentiel.refadmin.repository.BouquetRepository;
import fr.vdm.referentiel.refadmin.service.BouquetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BouquetServiceImpl implements BouquetService {
    private static final Logger K_LOGGER = LoggerFactory.getLogger(BouquetServiceImpl.class);


    @Autowired
    private BouquetRepository bouquetRepository;

    @Override
    public List<BouquetDto> getAllBouquets() {
        return BouquetMapper.INSTANCE.bouquetsToBouquetDtoList(this.bouquetRepository.findAll());
    }

    @Override
    public void saveAll(List<BouquetDto> bouquetsDto) {
        List<Bouquet> bouquets = BouquetMapper.INSTANCE.bouquetDtoToBouquetList(bouquetsDto);

        List<Bouquet> bouquetsBdd = this.bouquetRepository.findByIdOffre(bouquets.get(0).getIdOffre());
        this.bouquetRepository.deleteAll(bouquetsBdd.stream().filter(b -> !bouquets.stream().map(Bouquet::getIdGrp).collect(Collectors.toList()).contains(b.getIdGrp())).collect(Collectors.toList()));

        bouquets.forEach(b -> {
            if (bouquetRepository.findByIdOffreAndIdGrp(b.getIdOffre(), b.getIdGrp()) == null) {
                K_LOGGER.info(String.format("Ajout de l'offre d'id %s et du groupe applicatif %s au bouquet d'offres de service.", b.getIdOffre(), b.getIdGrp()));
                bouquetRepository.save(b);
            } else {
                K_LOGGER.error(String.format("L'offre d'id %s et le groupe applicatif %s appartiennent déjà au bouquet d'offres de service, aucune action effectuée.", b.getIdOffre(), b.getIdGrp()));
            }
        });
    }

    @Override
    @Transactional
    public void deleteBouquet(Long idOffre) {
        bouquetRepository.deleteByIdOffre(idOffre);
    }
}