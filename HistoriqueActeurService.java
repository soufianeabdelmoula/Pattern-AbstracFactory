package fr.vdm.referentiel.refadmin.service;


import fr.vdm.referentiel.refadmin.dto.FicheHistoriqueActeurDto;

public interface HistoriqueActeurService {
    FicheHistoriqueActeurDto getDetailHistoriqueActeur(Long idActeur);

}