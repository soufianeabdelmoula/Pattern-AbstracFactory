package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.ActeurValidationDto;
import fr.vdm.referentiel.refadmin.dto.OffresValidationDto;

public interface ContexteActeurService {


    OffresValidationDto buildValidationOffreDto(final long idActeur);

    public void clearCache(Long idActeur);

    ActeurValidationDto buildValidationActeurDto(final Long idActeur);
}
