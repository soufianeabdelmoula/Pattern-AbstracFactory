package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Bouquet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BouquetRepository extends JpaRepository<Bouquet, Long> {
    List<Bouquet> findByIdOffre(Long idOffre);

    long deleteByIdOffre(Long idOffre);

    Bouquet findByIdOffreAndIdGrp(Long idOffre, @Nullable Long idGrp);


}