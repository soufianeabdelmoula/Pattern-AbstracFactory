package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.CompteService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteServiceRepository extends JpaRepository<CompteService, Long> {
    CompteService findByIdActeur(Long idActeur);
}