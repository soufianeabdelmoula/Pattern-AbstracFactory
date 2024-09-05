package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    @Query("select e from Email e join ActeurVue v on v.idEmail = e.idEmail where v.idActeur = :idActeur ")
    Email selectByActeur(@Param("idActeur") Long idActeur);

}
