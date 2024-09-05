package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Acteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActeurRepository extends JpaRepository<Acteur, Long> {
    Acteur findActeurByLogin(String login);
}