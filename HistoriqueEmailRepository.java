package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.HistoriqueEmail;
import fr.vdm.referentiel.refadmin.model.HistoriqueEmailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface HistoriqueEmailRepository extends JpaRepository<HistoriqueEmail, HistoriqueEmailId> {

    HistoriqueEmail findHistoriqueEmailByIdEmailAndDebHisto(Long idEmail, Instant debHisto);
    List<HistoriqueEmail> findAllByIdEmail(Long idEmail);
}
