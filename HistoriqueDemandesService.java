package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.EtapeDemActeurDto;
import fr.vdm.referentiel.refadmin.dto.FiltreHistoriqueDemandesDto;
import fr.vdm.referentiel.refadmin.dto.HistoriqueDemandeDroitDto;
import fr.vdm.referentiel.refadmin.dto.StatutDemandeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HistoriqueDemandesService {
    /**
     * Récupère toutes les demandes d'habilitation en fonction des critères de filtre spécifiés,
     * et les retourne sous forme de pages paginées.
     *
     * @param filter    Objet contenant les critères de filtre pour la recherche des demandes d'habilitation.
     * @param pageable  Objet permettant la pagination des résultats.
     * @return          Une page contenant les résultats de la recherche, chaque élément étant une entité de type HistoriqueDemandeDroitDto.
     *                  Les résultats sont triés et paginés en fonction des paramètres spécifiés.
     */
    Page<HistoriqueDemandeDroitDto> findAllDemandesHabilitationByFilter(FiltreHistoriqueDemandesDto filter, Pageable pageable);

    /**
     * Récupère toutes les demandes liées au compte de l'acteur en fonction des critères de filtre spécifiés,
     * et les retourne sous forme de pages paginées.
     *
     * @param filter    Objet contenant les critères de filtre pour la recherche des demandes liées au compte de l'acteur.
     * @param pageable  Objet permettant la pagination des résultats.
     * @return          Une page contenant les résultats de la recherche, chaque élément étant une entité de type HistoriqueDemandeDroitDto.
     *                  Les résultats sont triés et paginés en fonction des paramètres spécifiés.
     */
    Page<HistoriqueDemandeDroitDto> findAllDemandesCompteActeurByFilter(FiltreHistoriqueDemandesDto filter, Pageable pageable);

    List<EtapeDemActeurDto> findAllEtapesFromIdDemandeActeur(Long idDemande);

    StatutDemandeDto findStautDemandeByIdDemandeActeur(Long id);

    byte[] getExportCsvHistoriqueDemandesHabilitation();

    byte[] getExportCsvHistoriqueDemandesCompteActeur();
}
