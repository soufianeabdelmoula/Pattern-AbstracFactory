package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Externe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExterneRepository extends JpaRepository<Externe, Long> {
}