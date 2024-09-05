package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.HistoriqueOffre;
import fr.vdm.referentiel.refadmin.model.HistoriqueOffreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoriqueOffreRepository extends JpaRepository<HistoriqueOffre, HistoriqueOffreId> {
    List<HistoriqueOffre> findAllByIdOffre(Long idActeur);

}
