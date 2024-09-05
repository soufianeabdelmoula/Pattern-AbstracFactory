package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.CompteApplicatif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteApplicatifRepository extends JpaRepository<CompteApplicatif, Long> {
    CompteApplicatif findByLogin(String login);
    CompteApplicatif findByIdActeur(Long idActeur);
}