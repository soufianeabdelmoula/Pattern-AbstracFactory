package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    Preference findByActeurIdActeur(Long idActeur);
}
