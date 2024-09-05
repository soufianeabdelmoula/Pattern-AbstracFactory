package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.CompteRessource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteRessourceRepository extends JpaRepository<CompteRessource, Long> {
    CompteRessource findByIdActeur(Long idActeur);
}